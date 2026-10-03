package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BoardFilter
import com.example.data.model.StrategyType
import com.example.ui.theme.QuantGold
import com.example.ui.theme.QuantGoldBg
import com.example.ui.theme.StockRed
import com.example.ui.theme.SurfaceCardDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TechBlue

@Composable
fun StrategyCarousel(
    selectedStrategy: StrategyType?,
    onSelectStrategy: (StrategyType?) -> Unit,
    selectedBoard: BoardFilter,
    onSelectBoard: (BoardFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Title & Board Segmented Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "四大王牌量化策略",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Board chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                BoardFilter.entries.forEach { board ->
                    val isSelected = selectedBoard == board
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) QuantGold.copy(alpha = 0.2f) else SurfaceElevatedDark)
                            .border(
                                1.dp,
                                if (isSelected) QuantGold else Color.Transparent,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onSelectBoard(board) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("board_chip_${board.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = board.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) QuantGold else Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Strategy Cards Horizontal Scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // "All Strategies" card
            StrategyItemCard(
                title = "全部策略联合初筛",
                subtitle = "并发运行4大王牌算法，择优输出多周期共振标的",
                tag = "综合海选",
                icon = Icons.Default.AutoGraph,
                isSelected = selectedStrategy == null,
                onClick = { onSelectStrategy(null) }
            )

            StrategyType.entries.forEach { strategy ->
                val icon = when (strategy) {
                    StrategyType.RPS_BREAKTHROUGH -> Icons.AutoMirrored.Filled.TrendingUp
                    StrategyType.CHIP_CONCENTRATION -> Icons.Default.Layers
                    StrategyType.OBV_ACCUMULATION -> Icons.Default.RemoveRedEye
                    StrategyType.ATR_SQUEEZE -> Icons.Default.Compress
                }
                StrategyItemCard(
                    title = strategy.title,
                    subtitle = strategy.subtitle,
                    tag = strategy.tag,
                    icon = icon,
                    isSelected = selectedStrategy == strategy,
                    onClick = { onSelectStrategy(strategy) }
                )
            }
        }
    }
}

@Composable
private fun StrategyItemCard(
    title: String,
    subtitle: String,
    tag: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(115.dp)
            .clickable { onClick() }
            .testTag("strategy_card_$tag"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) QuantGoldBg else SurfaceCardDark
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isSelected) QuantGold else Color.DarkGray.copy(alpha = 0.5f)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = tag,
                        tint = if (isSelected) QuantGold else TechBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tag,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) QuantGold else Color.White
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(QuantGold)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "已选",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )
        }
    }
}
