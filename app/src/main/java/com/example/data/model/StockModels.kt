package com.example.data.model

enum class KPeriod(val klt: Int, val label: String) {
    MIN_60(60, "60分"),
    DAY(101, "日线"),
    WEEK(102, "周线"),
    MONTH(103, "月线")
}

enum class StrategyType(
    val title: String,
    val subtitle: String,
    val tag: String,
    val iconName: String
) {
    RPS_BREAKTHROUGH(
        title = "主线领头羊 · RPS强势突破",
        subtitle = "板块前15% + RPS≥90 + 换手4%~12% + 量比>1.8",
        tag = "RPS突破",
        iconName = "trending_up"
    ),
    CHIP_CONCENTRATION(
        title = "筹码单峰密集 · 主升爆破",
        subtitle = "40日振幅≤20% + 筹码集中度≤10% + 获利盘≥90%",
        tag = "单峰突破",
        iconName = "layers"
    ),
    OBV_ACCUMULATION(
        title = "主力暗中吸筹 · OBV隐形底背离",
        subtitle = "30日阴跌但OBV创新高 + ADX>25 + 换手3%~8%",
        tag = "OBV吸筹",
        iconName = "visibility"
    ),
    ATR_SQUEEZE(
        title = "极致地量反转 · ATR波动挤压",
        subtitle = "成交量萎缩至1/3 + ATR挤压低位 + 次日阳线反转",
        tag = "地量变盘",
        iconName = "compress"
    )
}

enum class BoardFilter(val label: String) {
    ALL("全部"),
    MAIN_BOARD("主板 10%"),
    CHINEXT_STAR("创业·科创 20%")
}

data class RadarResult(
    val monthRed: Boolean,
    val weekRed: Boolean,
    val dayRed: Boolean,
    val min60Red: Boolean,
    val score: Double,
    val isResonance: Boolean = (if (monthRed) 1 else 0) + (if (weekRed) 1 else 0) + (if (dayRed) 1 else 0) + (if (min60Red) 1 else 0) >= 3,
    val isReboundWarning: Boolean = !monthRed && dayRed,
    val supportPrice: Double = 0.0,
    val stopLossPrice: Double = 0.0,
    val targetPrice: Double = 0.0,
    val ma20: Double = 0.0
)

data class StockCandidate(
    val code: String,
    val name: String,
    val price: Double,
    val pctChg: Double,
    val turnover: Double,
    val volRatio: Double,
    val amount: Double,
    val pe: Double = 25.0,
    val pb: Double = 2.8,
    val totalVal: Double = 250.0, // 亿元
    val industry: String = "科技成长",
    val rps120: Double = 88.0,
    val rps250: Double = 85.0,
    val chipConc: Double = 8.5, // 90%筹码集中度% (越小越集中)
    val profitRatio: Double = 88.0, // 获利盘比例%
    val atr: Double = 1.25,
    val matchedStrategies: List<StrategyType> = listOf(StrategyType.RPS_BREAKTHROUGH),
    val radarResult: RadarResult? = null
)

data class MarketEnvironment(
    val indexPrice: Double = 3368.50,
    val indexChangePct: Double = 0.85,
    val indexMa20: Double = 3310.0,
    val isAboveMa20: Boolean = true,
    val marketAmountTrillion: Double = 1.28,
    val risingRatio: Double = 68.5, // 68.5% 股票上涨
    val isBullAttack: Boolean = true,
    val isBearDefense: Boolean = false,
    val suggestedPosition: String = "70% ~ 100%",
    val statusMessage: String = "指数站上20日线，两市成交放量万亿以上，适宜进攻型突破策略"
)

enum class AiMode(val title: String) {
    FAST_DIAGNOSIS("秒级轻诊断"),
    IN_DEPTH_REPORT("全景投研研报"),
    BULL_BEAR_DEBATE("激进多空辩论")
}

data class AiAnalysisResult(
    val stockCode: String,
    val stockName: String,
    val quantScore: Int = 9,
    val bullLogics: List<String> = emptyList(),
    val fatalRisk: String = "",
    val entryRange: String = "",
    val stopLoss: String = "",
    val targetPrice: String = "",
    val rawContent: String = "",
    val mode: AiMode = AiMode.FAST_DIAGNOSIS
)
