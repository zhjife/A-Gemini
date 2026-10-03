package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BoardFilter
import com.example.data.model.MarketEnvironment
import com.example.data.model.StockCandidate
import com.example.data.model.StrategyType
import com.example.ui.components.MarketThermometerCard
import com.example.ui.components.StockCard
import com.example.ui.components.StrategyCarousel
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.QuantGold
import com.example.ui.theme.StockRed
import com.example.ui.theme.SurfaceCardDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TechBlue

@Composable
fun ScreenerScreen(
    marketEnv: MarketEnvironment,
    candidates: List<StockCandidate>,
    isScanning: Boolean,
    scanDurationMs: Long,
    selectedStrategy: StrategyType?,
    selectedBoard: BoardFilter,
    onSelectStrategy: (StrategyType?) -> Unit,
    onSelectBoard: (BoardFilter) -> Unit,
    onPerformScan: () -> Unit,
    onNavigateToResults: () -> Unit,
    onAddToTracking: (StockCandidate) -> Unit,
    onAiDiagnose: (StockCandidate) -> Unit,
    onAiDebate: (StockCandidate) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("screener_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Market Sentiment Thermometer Card (环境层)
        item {
            MarketThermometerCard(env = marketEnv)
        }

        // 2. 4 Quant Strategies Carousel + Board selector (策略层与制度层)
        item {
            StrategyCarousel(
                selectedStrategy = selectedStrategy,
                onSelectStrategy = onSelectStrategy,
                selectedBoard = selectedBoard,
                onSelectBoard = onSelectBoard
            )
        }

        // 3. Prominent Main Scan Button
        item {
            Button(
                onClick = onPerformScan,
                enabled = !isScanning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("full_market_scan_main_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StockRed,
                    disabledContainerColor = Color.DarkGray
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "正在直连东财全市场 5000+ 标的内存极速初筛...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "扫描",
                        tint = QuantGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⚡ 立即全市场量化扫描 (<0.5秒)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 4. Performance & Infrastructure Stats Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceElevatedDark)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "性能",
                        tint = TechBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (scanDurationMs > 0) "最近扫描耗时: ${scanDurationMs}ms (端侧直连)" else "东财官方集群直连 · 零服务器成本",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }

                Text(
                    text = "30s端侧缓存保护",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }

        // 5. Featured Resonance Candidates Preview Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "多周期强共振极优候选 (Top 5)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                TextButton(
                    onClick = onNavigateToResults,
                    modifier = Modifier.testTag("view_all_results_btn")
                ) {
                    Text(text = "查看全部 (${candidates.size})", fontSize = 12.sp, color = QuantGold)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "更多",
                        tint = QuantGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // 6. Top candidates preview cards (Top 3~5)
        if (candidates.isEmpty() && !isScanning) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceCardDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无标的，点击上方按钮开启全市场量化海选",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(candidates.take(5), key = { it.code }) { candidate ->
                StockCard(
                    candidate = candidate,
                    onAddToTracking = { onAddToTracking(candidate) },
                    onAiDiagnose = { onAiDiagnose(candidate) },
                    onAiDebate = { onAiDebate(candidate) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
