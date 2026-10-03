package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.MarketEnvironment
import com.example.ui.theme.QuantGold
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockRed
import com.example.ui.theme.SurfaceCardDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TechBlue

@Composable
fun MarketThermometerCard(
    env: MarketEnvironment,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("market_thermometer_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (env.isBullAttack) StockRed.copy(alpha = 0.5f)
                else if (env.isBearDefense) StockGreen.copy(alpha = 0.5f)
                else QuantGold.copy(alpha = 0.4f)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Status Zone badge + Index overview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = if (env.isBullAttack) StockRed else if (env.isBearDefense) StockGreen else QuantGold
                    val statusText = if (env.isBullAttack) "🟢 适宜进攻区" else if (env.isBearDefense) "🔴 空头防御区" else "🟡 箱体整固区"

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Index quote
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "上证指数 ${String.format("%.2f", env.indexPrice)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val pctColor = if (env.indexChangePct >= 0) StockRed else StockGreen
                    val pctSign = if (env.indexChangePct >= 0) "+" else ""
                    Text(
                        text = "$pctSign${String.format("%.2f", env.indexChangePct)}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = pctColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3-Metric Metric Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceElevatedDark)
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metric 1: Liquidity Volume
                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "两市成交额", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${env.marketAmountTrillion} 万亿",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (env.marketAmountTrillion >= 1.0) QuantGold else Color.LightGray
                    )
                }

                // Metric 2: Rising Ratio
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "上涨占比", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${env.risingRatio}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (env.risingRatio >= 50.0) StockRed else StockGreen
                    )
                }

                // Metric 3: Suggested Position
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "建议仓位", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = env.suggestedPosition,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (env.isBullAttack) StockRed else QuantGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Environmental Guidance description
            Text(
                text = env.statusMessage,
                fontSize = 12.sp,
                color = Color.LightGray,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mine Clearance Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(TechBlue.copy(alpha = 0.12f))
                    .border(1.dp, TechBlue.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "制度排雷",
                    tint = TechBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🛡️ 自动排雷：已过滤ST/退市、封死涨停板(买不进)、亏损股及<1亿僵尸股",
                    fontSize = 11.sp,
                    color = TechBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
