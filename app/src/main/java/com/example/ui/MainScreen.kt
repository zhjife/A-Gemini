package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AiMode
import com.example.ui.components.AiDiagnosisSheet
import com.example.ui.screens.ResearchScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.screens.ScreenerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TrackingScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.QuantGold
import com.example.ui.theme.StockRed
import com.example.ui.theme.SurfaceCardDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TechBlue

@Composable
fun MainScreen(
    viewModel: QuantViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val trackedStocks by viewModel.trackedStocks.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect event messages (e.g. scan finish, alerts)
    LaunchedEffect(Unit) {
        viewModel.eventMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Android back button handling
    BackHandler(enabled = uiState.currentTab != ScreenTab.SCREENER) {
        viewModel.selectTab(ScreenTab.SCREENER)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceCardDark,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                ScreenTab.entries.forEach { tab ->
                    val isSelected = uiState.currentTab == tab
                    val (icon, label) = when (tab) {
                        ScreenTab.SCREENER -> Pair(Icons.Default.ElectricBolt, "选股大厅")
                        ScreenTab.RESULTS -> Pair(Icons.Default.Radar, "雷达看板")
                        ScreenTab.TRACKING -> Pair(Icons.Default.Bookmark, "持仓监控")
                        ScreenTab.RESEARCH -> Pair(Icons.Default.AutoAwesome, "AI投研")
                        ScreenTab.SETTINGS -> Pair(Icons.Default.Settings, "设置")
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = QuantGold,
                            indicatorColor = QuantGold,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                ScreenTab.SCREENER -> ScreenerScreen(
                    marketEnv = uiState.marketEnv,
                    candidates = uiState.candidates,
                    isScanning = uiState.isScanning,
                    scanDurationMs = uiState.scanTimeMillis,
                    selectedStrategy = uiState.selectedStrategy,
                    selectedBoard = uiState.selectedBoard,
                    onSelectStrategy = { viewModel.selectStrategy(it) },
                    onSelectBoard = { viewModel.selectBoard(it) },
                    onPerformScan = { viewModel.performScan(true) },
                    onNavigateToResults = { viewModel.selectTab(ScreenTab.RESULTS) },
                    onAddToTracking = { viewModel.addToTracking(it) },
                    onAiDiagnose = { viewModel.openAiDrawer(it, AiMode.FAST_DIAGNOSIS) },
                    onAiDebate = { viewModel.openAiDrawer(it, AiMode.BULL_BEAR_DEBATE) }
                )

                ScreenTab.RESULTS -> ResultsScreen(
                    candidates = uiState.candidates,
                    isScanning = uiState.isScanning,
                    selectedStrategy = uiState.selectedStrategy,
                    selectedBoard = uiState.selectedBoard,
                    onSelectStrategy = { viewModel.selectStrategy(it) },
                    onRefreshScan = { viewModel.performScan(true) },
                    onAddToTracking = { viewModel.addToTracking(it) },
                    onAiDiagnose = { viewModel.openAiDrawer(it, AiMode.FAST_DIAGNOSIS) },
                    onAiDebate = { viewModel.openAiDrawer(it, AiMode.BULL_BEAR_DEBATE) }
                )

                ScreenTab.TRACKING -> TrackingScreen(
                    trackedStocks = trackedStocks,
                    onDeleteStock = { id, name -> viewModel.deleteTracked(id, name) },
                    onSimulateProfit = { viewModel.simulateTrailingStopProfit(it) },
                    onSimulateBreakdown = { viewModel.simulateSupportBreakdown(it) },
                    onAddCustomStock = { code, name, price ->
                        val dummyCandidate = com.example.data.model.StockCandidate(
                            code = code,
                            name = name,
                            price = price,
                            pctChg = 0.0,
                            turnover = 4.0,
                            volRatio = 1.5,
                            amount = 500_000_000.0,
                            atr = price * 0.03
                        )
                        viewModel.addToTracking(dummyCandidate, price, "手动添加监控")
                    }
                )

                ScreenTab.RESEARCH -> ResearchScreen(
                    candidates = uiState.candidates,
                    activeCandidate = uiState.activeAiCandidate,
                    activeMode = uiState.activeAiMode,
                    streamingText = uiState.aiStreamingText,
                    isLoading = uiState.isAiLoading,
                    onSelectStock = { viewModel.openAiDrawer(it, uiState.activeAiMode) },
                    onSwitchMode = { viewModel.switchAiMode(it) },
                    onAddToTracking = { viewModel.addToTracking(it) }
                )

                ScreenTab.SETTINGS -> SettingsScreen(
                    currentApiKey = uiState.customApiKey,
                    currentEndpoint = uiState.customEndpoint,
                    currentModel = uiState.customModel,
                    onSaveSettings = { k, e, m -> viewModel.updateSettings(k, e, m) }
                )
            }

            // On-demand AI Drawer (Modal BottomSheet / Side Panel)
            if (uiState.showAiDrawer) {
                AiDiagnosisSheet(
                    candidate = uiState.activeAiCandidate,
                    mode = uiState.activeAiMode,
                    streamingText = uiState.aiStreamingText,
                    isLoading = uiState.isAiLoading,
                    onDismiss = { viewModel.closeAiDrawer() },
                    onSwitchMode = { viewModel.switchAiMode(it) },
                    onAddTracking = { viewModel.addToTracking(it) }
                )
            }
        }
    }
}
