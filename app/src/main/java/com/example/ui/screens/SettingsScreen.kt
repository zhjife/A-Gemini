package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.QuantGold
import com.example.ui.theme.StockGreen
import com.example.ui.theme.StockRed
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.theme.SurfaceCardDark
import com.example.ui.theme.SurfaceElevatedDark
import com.example.ui.theme.TechBlue

@Composable
fun SettingsScreen(
    currentApiKey: String,
    currentEndpoint: String,
    currentModel: String,
    onSaveSettings: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var apiKey by remember(currentApiKey) { mutableStateOf(currentApiKey) }
    var endpoint by remember(currentEndpoint) { mutableStateOf(currentEndpoint) }
    var model by remember(currentModel) { mutableStateOf(currentModel) }
    var isSaved by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "系统与AI投研配置",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "BYOK 个人大模型直连 · 东方财富行情端侧直连架构",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Section 1: BYOK LLM Settings Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorderDark)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "BYOK Key",
                            tint = QuantGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "大模型 API Key 配置 (BYOK 模式)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "支持 DeepSeek / Kimi / 智谱 GLM 或 Gemini。填入个人 Key 后端侧直接向网关请求，无中转更安全；不填时默认使用内置买方量化推演引擎。",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 17.sp
                    )

                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it; isSaved = false },
                        label = { Text("API Key (sk-...)") },
                        placeholder = { Text("输入您的 API Key") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_api_key_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = QuantGold,
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = endpoint,
                        onValueChange = { endpoint = it; isSaved = false },
                        label = { Text("API Gateway Base URL") },
                        placeholder = { Text("例如 https://api.deepseek.com/v1") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_endpoint_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = QuantGold,
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it; isSaved = false },
                        label = { Text("Model 标称") },
                        placeholder = { Text("例如 deepseek-chat 或 moonshot-v1-8k") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_model_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = QuantGold,
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            onSaveSettings(apiKey.trim(), endpoint.trim(), model.trim())
                            isSaved = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("save_settings_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = QuantGold,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "保存", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isSaved) "已保存配置" else "保存大模型配置", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 2: Architecture & Data Policy Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorderDark)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "架构",
                            tint = TechBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "无服务器架构与数据基础设施状态",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    InfoRow("行情通道", "东方财富官方 OpenAPI 集群直连 (HTTPS GET)")
                    InfoRow("CORS 状态", "Android 客户端原生直连，零同源与跨域封锁")
                    InfoRow("端侧防刷保护", "30 秒内存级缓存机制，避免频繁请求被限频")
                    InfoRow("K线并发控制", "最多 4 协程并发通道，严格保证轻量低功耗")
                    InfoRow("算力分工", "全市场 5000+ 标的内存过滤 <10ms，雷达并发 <0.3s")
                    InfoRow("持仓数据持久化", "本地 Room SQLite 数据库，离线可用，绝不上传")
                }
            }
        }

        // Section 3: Risk Warning & Disclaimer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "免责声明",
                            tint = StockRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "合规声明与风险提示",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "本应用为本地量化选股与技术辅助分析工具，所有量化策略指标、多周期雷达及大模型生成内容仅供投研参考，不构成任何形式的投资建议或买卖邀约。市场有风险，投资需谨慎。",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = value, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}
