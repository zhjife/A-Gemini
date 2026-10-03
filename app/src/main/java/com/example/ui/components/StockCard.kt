package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiMode
import com.example.data.model.RadarResult
import com.example.data.model.StockCandidate
import com.example.ui.theme.QuantGold
import com.example.ui.theme.QuantGoldBg
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockRed
import com.example.ui.theme.StockRedBg
import com.example.ui.theme.SurfaceCardDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TechBlue
import com.example.ui.theme.TechBlueBg
import com.example.ui.theme.WarningYellow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StockCard(
    candidate: StockCandidate,
    onAddToTracking: () -> Unit,
    onAiDiagnose: () -> Unit,
    onAiDebate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val radar = candidate.radarResult
    val isResonance = radar?.isResonance == true
    val isRebound = radar?.isReboundWarning == true

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("stock_card_${candidate.code}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceCardDark
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isResonance) QuantGold.copy(alpha = 0.8f) else Color.DarkGray.copy(alpha = 0.4f)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Line 1: Header: Name, Code, Price, Change%, Board & Sector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = candidate.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = candidate.code,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "￥${String.format("%.2f", candidate.price)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = StockRed
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val pctColor = if (candidate.pctChg >= 0) StockRed else StockGreen
                    val pctSign = if (candidate.pctChg >= 0) "+" else ""
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(StockRed.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$pctSign${String.format("%.2f", candidate.pctChg)}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = pctColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subline: Industry + Board info
            val boardName = when {
                candidate.code.startsWith("688") -> "科创板"
                candidate.code.startsWith("30") -> "创业板"
                candidate.code.startsWith("60") -> "沪市主板"
                else -> "深市主板"
            }
            Text(
                text = "$boardName · ${candidate.industry}",
                fontSize = 11.sp,
                color = TechBlue,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Line 2: Quant indicator tags flow
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricTag("RPS: ${candidate.rps120}", QuantGold)
                candidate.matchedStrategies.forEach { st ->
                    MetricTag(st.tag, StockRed)
                }
                MetricTag("换手: ${candidate.turnover}%", Color.LightGray)
                MetricTag("量比: ${candidate.volRatio}", if (candidate.volRatio >= 2.0) QuantGold else Color.LightGray)
                MetricTag("ATR: ${candidate.atr}", Color.LightGray)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Line 3: Multi-Period Resonance Radar Red/Green Lights
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceElevatedDark)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "周期雷达",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val mRed = radar?.monthRed == true
                    val wRed = radar?.weekRed == true
                    val dRed = radar?.dayRed == true
                    val h1Red = radar?.min60Red == true

                    PeriodStatusLight("月", mRed, if (mRed) "🔴多" else "🟢空")
                    PeriodStatusLight("周", wRed, if (wRed) "🔴多" else "🟢空")
                    PeriodStatusLight("日", dRed, if (dRed) "🔴突破" else "🟡震荡")
                    PeriodStatusLight("60分", h1Red, if (h1Red) "🔴极佳" else "🟡回踩")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Line 4: State Verdict Banner
            if (isResonance) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(QuantGold.copy(alpha = 0.15f))
                        .border(1.dp, QuantGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "强共振",
                        tint = QuantGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "⭐⭐⭐⭐ 多周期强共振 (置顶推荐 · 共振分 ${radar?.score?.toInt()}分)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuantGold
                    )
                }
            } else if (isRebound) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(WarningYellow.copy(alpha = 0.12f))
                        .border(1.dp, WarningYellow.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "超跌反弹",
                        tint = WarningYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "⚠️ 超跌短线反弹 (月线大周期承压，严格快进快出)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningYellow
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Line 5: Defense & Support Plan
            val support = radar?.supportPrice ?: (candidate.price * 0.95)
            val stopLoss = radar?.stopLossPrice ?: (candidate.price - 2.0 * candidate.atr)
            val target = radar?.targetPrice ?: (candidate.price + 3.0 * candidate.atr)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "防守: 关键支撑 ￥${String.format("%.2f", support)}",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
                Text(
                    text = "动态止损 ￥${String.format("%.2f", stopLoss)} (2*ATR)",
                    fontSize = 11.sp,
                    color = StockGreen,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.4f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Line 6: Actions buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Add to Tracking
                OutlinedButton(
                    onClick = onAddToTracking,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("add_tracking_btn_${candidate.code}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TechBlue
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(TechBlue.copy(alpha = 0.6f))
                    )
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "加入跟踪", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "加入跟踪", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // AI Fast Diagnosis (1s)
                Button(
                    onClick = onAiDiagnose,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(38.dp)
                        .testTag("ai_diagnose_btn_${candidate.code}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QuantGold,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI诊断", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "💡 AI极速诊断 (1s)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // AI Debate Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceElevatedDark)
                        .clickable { onAiDebate() }
                        .testTag("ai_debate_btn_${candidate.code}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Balance,
                        contentDescription = "多空辩论",
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricTag(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .border(0.5.dp, color.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = text, fontSize = 10.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PeriodStatusLight(period: String, isRed: Boolean, statusLabel: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$period: ", fontSize = 11.sp, color = Color.Gray)
        Text(
            text = statusLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isRed) StockRed else StockGreen
        )
    }
}
