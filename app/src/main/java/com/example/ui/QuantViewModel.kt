package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.TrackedStockEntity
import com.example.data.engine.TrailingStopEngine
import com.example.data.model.AiMode
import com.example.data.model.BoardFilter
import com.example.data.model.MarketEnvironment
import com.example.data.model.StockCandidate
import com.example.data.model.StrategyType
import com.example.data.repository.QuantRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab(val title: String) {
    SCREENER("选股大厅"),
    RESULTS("雷达看板"),
    TRACKING("持仓监控"),
    RESEARCH("AI投研"),
    SETTINGS("设置")
}

data class UiState(
    val currentTab: ScreenTab = ScreenTab.SCREENER,
    val selectedStrategy: StrategyType? = null,
    val selectedBoard: BoardFilter = BoardFilter.ALL,
    val marketEnv: MarketEnvironment = MarketEnvironment(),
    val candidates: List<StockCandidate> = emptyList(),
    val isScanning: Boolean = false,
    val scanTimeMillis: Long = 0L,
    val lastScanTimestamp: Long = 0L,
    // AI Drawer state
    val showAiDrawer: Boolean = false,
    val activeAiCandidate: StockCandidate? = null,
    val activeAiMode: AiMode = AiMode.FAST_DIAGNOSIS,
    val aiStreamingText: String = "",
    val isAiLoading: Boolean = false,
    // Settings
    val customApiKey: String = "",
    val customEndpoint: String = "https://api.deepseek.com/v1",
    val customModel: String = "deepseek-chat",
    val onlyMainBoard: Boolean = false
)

class QuantViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuantRepository(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val trackedStocks: StateFlow<List<TrackedStockEntity>> = repository.trackedStocks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _eventMessage = MutableSharedFlow<String>()
    val eventMessage: SharedFlow<String> = _eventMessage.asSharedFlow()

    private var aiJob: Job? = null

    init {
        // Initial quick load of environment and baseline candidates
        viewModelScope.launch {
            try {
                val env = repository.getMarketEnvironment()
                _uiState.value = _uiState.value.copy(marketEnv = env)
                // Execute initial fast scan so the user sees results immediately
                performScan(false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectTab(tab: ScreenTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun selectStrategy(strategy: StrategyType?) {
        _uiState.value = _uiState.value.copy(selectedStrategy = strategy)
    }

    fun selectBoard(board: BoardFilter) {
        _uiState.value = _uiState.value.copy(selectedBoard = board)
    }

    fun performScan(forceRefresh: Boolean = true) {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            _uiState.value = _uiState.value.copy(isScanning = true)

            try {
                val (env, results) = repository.scanAndEvaluate(
                    strategy = _uiState.value.selectedStrategy,
                    boardFilter = _uiState.value.selectedBoard,
                    forceRefresh = forceRefresh
                )
                val duration = System.currentTimeMillis() - startTime

                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    marketEnv = env,
                    candidates = results,
                    scanTimeMillis = duration,
                    lastScanTimestamp = System.currentTimeMillis()
                )

                val resonanceCount = results.count { it.radarResult?.isResonance == true }
                _eventMessage.emit("⚡ 量化扫描完成（耗时 ${duration}ms）：初筛 ${results.size} 只，强共振 $resonanceCount 只")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isScanning = false)
                _eventMessage.emit("扫描网络异常，已载入高保真基准池")
            }
        }
    }

    fun openAiDrawer(candidate: StockCandidate, mode: AiMode = AiMode.FAST_DIAGNOSIS) {
        aiJob?.cancel()
        _uiState.value = _uiState.value.copy(
            showAiDrawer = true,
            activeAiCandidate = candidate,
            activeAiMode = mode,
            aiStreamingText = "",
            isAiLoading = true
        )

        aiJob = viewModelScope.launch {
            try {
                repository.generateAiAnalysis(
                    candidate = candidate,
                    mode = mode,
                    apiKey = _uiState.value.customApiKey,
                    endpoint = _uiState.value.customEndpoint,
                    model = _uiState.value.customModel
                ).collect { chunk ->
                    _uiState.value = _uiState.value.copy(
                        aiStreamingText = chunk,
                        isAiLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    aiStreamingText = "AI生成异常：${e.localizedMessage}",
                    isAiLoading = false
                )
            }
        }
    }

    fun closeAiDrawer() {
        aiJob?.cancel()
        _uiState.value = _uiState.value.copy(showAiDrawer = false)
    }

    fun switchAiMode(mode: AiMode) {
        val current = _uiState.value.activeAiCandidate ?: return
        openAiDrawer(current, mode)
    }

    fun addToTracking(candidate: StockCandidate, customBuyPrice: Double? = null, customNotes: String = "") {
        viewModelScope.launch {
            val price = customBuyPrice ?: candidate.price
            repository.addTrackedStock(candidate, price, customNotes)
            _eventMessage.emit("✅ 已加入持仓跟踪：${candidate.name}（成本 ￥$price）")
        }
    }

    fun deleteTracked(id: Int, stockName: String) {
        viewModelScope.launch {
            repository.deleteTrackedStock(id)
            _eventMessage.emit("已移除跟踪标的：$stockName")
        }
    }

    fun updateTrackedPrice(stock: TrackedStockEntity, newPrice: Double) {
        viewModelScope.launch {
            val updated = TrailingStopEngine.evaluate(stock, newPrice)
            repository.updateTrackedStock(updated)
            if (updated.trailingStopTriggered) {
                _eventMessage.emit("⚠️ 移动止盈/止损触发提醒：${stock.name} 现价 ￥$newPrice 已破位移动防守线 ￥${updated.stopLossPrice}")
            } else if (updated.breakSupport1450) {
                _eventMessage.emit("🚨 14:50尾盘趋势破位警报：${stock.name} 跌破关键支撑 ￥${stock.supportPrice}，建议收盘前坚决止损！")
            }
        }
    }

    fun simulateTrailingStopProfit(stock: TrackedStockEntity) {
        // Simulates price rising by +12%, activating trailing stop
        val simulatePrice = (stock.buyPrice * 1.12 * 100).toInt() / 100.0
        updateTrackedPrice(stock, simulatePrice)
    }

    fun simulateSupportBreakdown(stock: TrackedStockEntity) {
        // Simulates price dropping below support
        val simulatePrice = (stock.supportPrice * 0.98 * 100).toInt() / 100.0
        updateTrackedPrice(stock, simulatePrice)
    }

    fun updateSettings(apiKey: String, endpoint: String, model: String) {
        _uiState.value = _uiState.value.copy(
            customApiKey = apiKey,
            customEndpoint = endpoint,
            customModel = model
        )
        viewModelScope.launch {
            _eventMessage.emit("AI模型配置已更新")
        }
    }
}
