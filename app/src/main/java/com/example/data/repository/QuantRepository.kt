package com.example.data.repository

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.TrackedStockEntity
import com.example.data.engine.MarketScanEngine
import com.example.data.engine.RadarEngine
import com.example.data.engine.TrailingStopEngine
import com.example.data.model.AiMode
import com.example.data.model.BoardFilter
import com.example.data.model.MarketEnvironment
import com.example.data.model.StockCandidate
import com.example.data.model.StrategyType
import com.example.data.network.AiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuantRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.trackedStockDao()
    private val scanEngine = MarketScanEngine()
    private val radarEngine = RadarEngine()
    private val aiService = AiService()

    val trackedStocks: Flow<List<TrackedStockEntity>> = dao.getAllTrackedStocks()

    suspend fun getMarketEnvironment(): MarketEnvironment {
        return scanEngine.getMarketEnvironment()
    }

    suspend fun scanAndEvaluate(
        strategy: StrategyType?,
        boardFilter: BoardFilter,
        forceRefresh: Boolean = false
    ): Pair<MarketEnvironment, List<StockCandidate>> {
        val env = scanEngine.getMarketEnvironment()
        val candidates = scanEngine.scanMarket(strategy, boardFilter, forceRefresh)
        val radarResults = radarEngine.evaluateCandidates(candidates)
        return Pair(env, radarResults)
    }

    fun generateAiAnalysis(
        candidate: StockCandidate,
        mode: AiMode,
        apiKey: String = "",
        endpoint: String = "",
        model: String = ""
    ): Flow<String> {
        return aiService.generateAnalysisStream(candidate, mode, apiKey, endpoint, model)
    }

    suspend fun addTrackedStock(
        candidate: StockCandidate,
        buyPrice: Double = candidate.price,
        notes: String = ""
    ): Long {
        val radar = candidate.radarResult
        val support = radar?.supportPrice ?: (candidate.price * 0.95)
        val stopLoss = radar?.stopLossPrice ?: (candidate.price - 2.0 * candidate.atr)
        val target = radar?.targetPrice ?: (candidate.price + 3.0 * candidate.atr)

        val entity = TrackedStockEntity(
            code = candidate.code,
            name = candidate.name,
            buyPrice = buyPrice,
            currentPrice = candidate.price,
            highestPrice = candidate.price,
            supportPrice = support,
            stopLossPrice = stopLoss,
            atr = candidate.atr,
            trailingStopActive = false,
            trailingStopTriggered = false,
            breakSupport1450 = false,
            targetPrice = target,
            notes = if (notes.isNotBlank()) notes else "量化策略: ${candidate.matchedStrategies.firstOrNull()?.tag ?: "多周期共振"}"
        )
        return dao.insert(entity)
    }

    suspend fun updateTrackedStock(stock: TrackedStockEntity) {
        dao.update(stock)
    }

    suspend fun deleteTrackedStock(id: Int) {
        dao.deleteById(id)
    }

    suspend fun refreshTrackedPrices(currentPrices: Map<String, Double>) {
        // Evaluate each stock with TrailingStopEngine
    }
}
