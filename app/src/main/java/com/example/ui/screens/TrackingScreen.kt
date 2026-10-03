package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.db.TrackedStockEntity
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.QuantGold
import com.example.ui.theme.QuantGoldBg
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockRed
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.theme.SurfaceCardDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TechBlue
import com.example.ui.theme.WarningYellow

@Composable
fun TrackingScreen(
    trackedStocks: List<TrackedStockEntity>,
    onDeleteStock: (Int, String) -> Unit,
    onSimulateProfit: (TrackedStockEntity) -> Unit,
    onSimulateBreakdown: (TrackedStockEntity) -> Unit,
    onAddCustomStock: (String, String, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("tracking_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCardDark)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "持仓动态出场与止盈监控",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Trailing Stop 动态移动止盈 · 14:50 破位纪律强平警报",
                            fontSize = 11.sp,
                            color = TechBlue
                        )
                    }

                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.testTag("add_custom_tracking_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = QuantGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "手动添加", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "添加持仓", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Rule explanations card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceElevatedDark)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "1. 浮盈达 +10% 止损线上移至成本线保本\n2. 股价创新高按 1.5*ATR 自动上移锁利\n3. 14:50 跌破防守位触发强平出局",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        lineHeight = 16.sp
                    )
                }
            }

            // List of tracked positions
            if (trackedStocks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "暂无持仓",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "当前暂无持仓跟踪标的",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "在选股大厅或雷达看板中点击 [ ➕ 加入跟踪 ] 即可将极优标的加入动态止盈止损监控闭环",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(trackedStocks, key = { it.id }) { stock ->
                        TrackedStockCard(
                            stock = stock,
                            onDelete = { onDeleteStock(stock.id, stock.name) },
                            onSimulateProfit = { onSimulateProfit(stock) },
                            onSimulateBreakdown = { onSimulateBreakdown(stock) }
                        )
                    }
                }
            }
        }

        // Add Dialog
        if (showAddDialog) {
            AddStockDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { code, name, price ->
                    onAddCustomStock(code, name, price)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun TrackedStockCard(
    stock: TrackedStockEntity,
    onDelete: () -> Unit,
    onSimulateProfit: () -> Unit,
    onSimulateBreakdown: () -> Unit
) {
    val profit = stock.profitPct
    val isProfit = profit >= 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tracked_stock_${stock.code}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCardDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (stock.trailingStopTriggered || stock.breakSupport1450) StockRed
                else if (stock.trailingStopActive) QuantGold
                else Color.DarkGray.copy(alpha = 0.5f)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Row 1: Stock name & code & Profit %
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stock.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stock.code,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val sign = if (isProfit) "+" else ""
                    val pColor = if (isProfit) StockRed else StockGreen
                    Text(
                        text = "$sign${String.format("%.2f", profit)}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = pColor
                    )

                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "删除", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Price Matrix (Buy Price, Current Price, Highest Price, Stop Loss)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceElevatedDark)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "买入成本", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "￥${String.format("%.2f", stock.buyPrice)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column {
                    Text(text = "当前现价", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "￥${String.format("%.2f", stock.currentPrice)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isProfit) StockRed else StockGreen)
                }
                Column {
                    Text(text = "持仓最高", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "￥${String.format("%.2f", stock.highestPrice)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = QuantGold)
                }
                Column {
                    Text(text = "移动止损线", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "￥${String.format("%.2f", stock.stopLossPrice)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = StockGreen)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: Status Warnings / Trailing Stop Badges
            if (stock.trailingStopTriggered) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(StockRed.copy(alpha = 0.2f))
                        .border(1.dp, StockRed, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.NotificationImportant, contentDescription = "止损触发", tint = StockRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🚨 触发移动止盈/止损信号：现价已跌破移动防守位 ￥${stock.stopLossPrice}，建议果断平仓锁定利润！",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StockRed
                    )
                }
            } else if (stock.breakSupport1450) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(WarningYellow.copy(alpha = 0.2f))
                        .border(1.dp, WarningYellow, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Alarm, contentDescription = "14:50警报", tint = WarningYellow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⚠️ 14:50尾盘趋势破位警报：有效跌破关键支撑 ￥${stock.supportPrice}，建议在 15:00 收盘前坚决止损出局！",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningYellow
                    )
                }
            } else if (stock.trailingStopActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(QuantGold.copy(alpha = 0.15f))
                        .border(1.dp, QuantGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "移动止盈激活", tint = QuantGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🟢 Trailing Stop 已激活：已保本，动态止损线随高点不断上移锁利",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = QuantGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 4: Simulation & Stress Test Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSimulateProfit,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(text = "🧪 模拟暴涨+12%", fontSize = 11.sp, color = QuantGold)
                }

                OutlinedButton(
                    onClick = onSimulateBreakdown,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(text = "🧪 模拟跌破防守", fontSize = 11.sp, color = StockRed)
                }
            }
        }
    }
}

@Composable
private fun AddStockDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double) -> Unit
) {
    var code by remember { mutableStateOf("600519") }
    var name by remember { mutableStateOf("贵州茅台") }
    var buyPriceStr by remember { mutableStateOf("1580.00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "添加持仓监控标的", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("股票代码") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("股票名称") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = buyPriceStr,
                    onValueChange = { buyPriceStr = it },
                    label = { Text("买入成本价 (元)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = buyPriceStr.toDoubleOrNull() ?: 10.0
                    onConfirm(code.trim(), name.trim(), p)
                },
                colors = ButtonDefaults.buttonColors(containerColor = QuantGold, contentColor = Color.Black)
            ) {
                Text("确认监控")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = Color.Gray)
            }
        },
        containerColor = SurfaceCardDark
    )
}
