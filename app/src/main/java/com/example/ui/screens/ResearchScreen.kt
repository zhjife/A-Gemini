package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiMode
import com.example.data.model.StockCandidate
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.QuantGold
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockRed
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.theme.SurfaceCardDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TechBlue

@Composable
fun ResearchScreen(
    candidates: List<StockCandidate>,
    activeCandidate: StockCandidate?,
    activeMode: AiMode,
    streamingText: String,
    isLoading: Boolean,
    onSelectStock: (StockCandidate) -> Unit,
    onSwitchMode: (AiMode) -> Unit,
    onAddToTracking: (StockCandidate) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val currentStock = activeCandidate ?: candidates.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("research_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI投研",
                        tint = QuantGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI 私募级投研研报与多空辩论",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "深度研报 · 激进多空辩论台 · 秒级量化诊断 (支持端侧大模型直连)",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        // Horizontal Stock Selector Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "选择研报标的：", fontSize = 12.sp, color = Color.Gray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    candidates.forEach { c ->
                        val isSelected = c.code == currentStock?.code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) QuantGold else SurfaceElevatedDark)
                                .clickable { onSelectStock(c) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("select_research_stock_${c.code}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${c.name} (${c.code})",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        }
                    }
                }
            }
        }

        // Mode Switcher Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceElevatedDark)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AiMode.entries.forEach { m ->
                    val isSelected = activeMode == m
                    val icon = when (m) {
                        AiMode.FAST_DIAGNOSIS -> Icons.Default.FlashOn
                        AiMode.IN_DEPTH_REPORT -> Icons.Default.Description
                        AiMode.BULL_BEAR_DEBATE -> Icons.Default.Balance
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) QuantGold else Color.Transparent)
                            .clickable { onSwitchMode(m) }
                            .padding(vertical = 8.dp)
                            .testTag("research_mode_tab_${m.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = m.title,
                                tint = if (isSelected) Color.Black else Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = m.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else Color.LightGray
                            )
                        }
                    }
                }
            }
        }

        // Stock Fundamental Summary Card
        if (currentStock != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCardDark)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${currentStock.name} · ${currentStock.industry}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "￥${String.format("%.2f", currentStock.price)} (+${currentStock.pctChg}%)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = StockRed
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "市值: ${currentStock.totalVal}亿", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "PE: ${currentStock.pe}倍", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "PB: ${currentStock.pb}", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "RPS120: ${currentStock.rps120}", fontSize = 11.sp, color = QuantGold)
                            Text(text = "集中度: ${currentStock.chipConc}%", fontSize = 11.sp, color = TechBlue)
                        }
                    }
                }
            }
        }

        // Generated Content Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorderDark)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isLoading && streamingText.isBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(color = QuantGold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "大模型正在深度解构与推演...", fontSize = 13.sp, color = Color.Gray)
                        }
                    } else {
                        Text(
                            text = streamingText.ifBlank { "点击上方模式标签生成专业研报" },
                            fontSize = 13.sp,
                            color = Color(0xFFF3F4F6),
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(streamingText))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "复制", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "复制研报", fontSize = 12.sp)
                        }

                        if (currentStock != null) {
                            Button(
                                onClick = { onAddToTracking(currentStock) },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StockRed, contentColor = Color.White)
                            ) {
                                Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = "加入持仓", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "加入持仓跟踪", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
