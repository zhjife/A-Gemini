package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BoardFilter
import com.example.data.model.StockCandidate
import com.example.data.model.StrategyType
import com.example.ui.components.StockCard
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.QuantGold
import com.example.ui.theme.StockRed
import com.example.ui.theme.SurfaceCardDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TechBlue

@Composable
fun ResultsScreen(
    candidates: List<StockCandidate>,
    isScanning: Boolean,
    selectedStrategy: StrategyType?,
    selectedBoard: BoardFilter,
    onSelectStrategy: (StrategyType?) -> Unit,
    onRefreshScan: () -> Unit,
    onAddToTracking: (StockCandidate) -> Unit,
    onAiDiagnose: (StockCandidate) -> Unit,
    onAiDebate: (StockCandidate) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(candidates, searchQuery, selectedStrategy) {
        candidates.filter { item ->
            val matchQuery = searchQuery.isBlank() ||
                    item.name.contains(searchQuery.trim(), ignoreCase = true) ||
                    item.code.contains(searchQuery.trim(), ignoreCase = true)
            val matchStrategy = selectedStrategy == null || item.matchedStrategies.contains(selectedStrategy)
            matchQuery && matchStrategy
        }
    }

    val resonanceCount = filteredList.count { it.radarResult?.isResonance == true }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("results_screen")
    ) {
        // Top Action & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCardDark)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "量化扫描与多周期雷达",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(StockRed.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${filteredList.size} 只候选",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StockRed
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "⭐ 3红及以上强共振置顶: $resonanceCount 只 | 东方财富 60m/日/周/月 并发抓取",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                IconButton(
                    onClick = onRefreshScan,
                    enabled = !isScanning,
                    modifier = Modifier.testTag("refresh_scan_btn")
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            color = QuantGold,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "刷新扫描", tint = QuantGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("搜索股票代码或名称...", fontSize = 13.sp, color = Color.Gray) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "搜索", tint = Color.Gray, modifier = Modifier.size(18.dp))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("search_stock_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = QuantGold,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedContainerColor = SurfaceElevatedDark,
                    unfocusedContainerColor = SurfaceElevatedDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Strategy Filter Pills Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PillChip(
                    label = "全部策略 (${candidates.size})",
                    isSelected = selectedStrategy == null,
                    onClick = { onSelectStrategy(null) }
                )
                StrategyType.entries.forEach { st ->
                    val count = candidates.count { it.matchedStrategies.contains(st) }
                    PillChip(
                        label = "${st.tag} ($count)",
                        isSelected = selectedStrategy == st,
                        onClick = { onSelectStrategy(st) }
                    )
                }
            }
        }

        // Candidates LazyColumn
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isScanning) "正在并发拉取多周期 K 线并计算 MA20 / MACD 趋势..." else "未找到符合当前过滤条件的标的",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredList, key = { it.code }) { candidate ->
                    StockCard(
                        candidate = candidate,
                        onAddToTracking = { onAddToTracking(candidate) },
                        onAiDiagnose = { onAiDiagnose(candidate) },
                        onAiDebate = { onAiDebate(candidate) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PillChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) QuantGold else SurfaceElevatedDark)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else Color.LightGray
        )
    }
}
