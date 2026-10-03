package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiMode
import com.example.data.model.StockCandidate
import com.example.ui.theme.QuantGold
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockRed
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.theme.SurfaceCardDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TechBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiDiagnosisSheet(
    candidate: StockCandidate?,
    mode: AiMode,
    streamingText: String,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSwitchMode: (AiMode) -> Unit,
    onAddTracking: (StockCandidate) -> Unit,
    modifier: Modifier = Modifier
) {
    if (candidate == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val clipboardManager = LocalClipboardManager.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Gray.copy(alpha = 0.5f))
            )
        },
        modifier = modifier.testTag("ai_diagnosis_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI投研",
                        tint = QuantGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${candidate.name} (${candidate.code})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "￥${String.format("%.2f", candidate.price)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = StockRed
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_ai_sheet_btn")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "关闭", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // AI Mode Tabs: [💡 秒级轻诊断] [📄 深度全景研报] [⚖️ 激进多空辩论]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceElevatedDark)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AiMode.entries.forEach { m ->
                    val isSelected = mode == m
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) QuantGold else Color.Transparent)
                            .clickable { onSwitchMode(m) }
                            .padding(vertical = 8.dp)
                            .testTag("ai_mode_tab_${m.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = m.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quant context chips summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceCardDark)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "量比: ${candidate.volRatio}", fontSize = 11.sp, color = Color.Gray)
                Text(text = "换手: ${candidate.turnover}%", fontSize = 11.sp, color = Color.Gray)
                Text(text = "集中度: ${candidate.chipConc}%", fontSize = 11.sp, color = Color.Gray)
                Text(text = "获利盘: ${candidate.profitRatio}%", fontSize = 11.sp, color = QuantGold)
                Text(text = "ATR: ${candidate.atr}", fontSize = 11.sp, color = TechBlue)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Content Area with Streaming Text
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCardDark)
                    .border(1.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                if (isLoading && streamingText.isBlank()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = QuantGold,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "私募量化大脑正在秒级推理与雷达推演...",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = streamingText,
                            fontSize = 13.sp,
                            lineHeight = 22.sp,
                            color = Color(0xFFE5E7EB),
                            fontFamily = FontFamily.Default
                        )

                        if (isLoading) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(QuantGold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "实时流式输出中...", fontSize = 11.sp, color = QuantGold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Actions: Copy & Add to Tracking
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
                        .height(44.dp)
                        .testTag("copy_ai_text_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "复制", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "复制内容", fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        onAddTracking(candidate)
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1.4f)
                        .height(44.dp)
                        .testTag("add_to_tracking_from_ai_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StockRed,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = "加入持仓", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "一键加入持仓监控", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
