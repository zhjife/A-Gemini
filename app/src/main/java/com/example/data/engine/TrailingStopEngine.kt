package com.example.data.engine

import com.example.data.db.TrackedStockEntity
import java.util.Calendar
import kotlin.math.max
import kotlin.math.roundToInt

object TrailingStopEngine {

    /**
     * Evaluates a tracked stock against price movements and trailing stop rules.
     */
    fun evaluate(
        stock: TrackedStockEntity,
        newPrice: Double
    ): TrackedStockEntity {
        val updatedHighest = max(stock.highestPrice, newPrice)
        val profitFromBuy = if (stock.buyPrice > 0) ((newPrice - stock.buyPrice) / stock.buyPrice) * 100.0 else 0.0

        var newStopLoss = stock.stopLossPrice
        var trailingActive = stock.trailingStopActive

        // 1. Floating profit reaches +10%, stop loss automatically moves up to buy cost price (capital preservation)
        if (profitFromBuy >= 10.0) {
            trailingActive = true
            if (newStopLoss < stock.buyPrice) {
                newStopLoss = stock.buyPrice
            }
        }

        // 2. Trailing Stop ratchet: as price makes new highs, stop loss moves up to (highest - 1.5 * ATR)
        if (trailingActive) {
            val trailingStopLevel = updatedHighest - (1.5 * stock.atr)
            if (trailingStopLevel > newStopLoss) {
                newStopLoss = ((trailingStopLevel * 100.0).roundToInt()) / 100.0
            }
        }

        // 3. Trigger check: did price fall below the stop loss?
        val triggered = newPrice <= newStopLoss

        // 4. 14:50 discipline check: if price breaks key support (MA20 / supportPrice)
        val breaksSupport = newPrice < stock.supportPrice

        return stock.copy(
            currentPrice = newPrice,
            highestPrice = updatedHighest,
            stopLossPrice = newStopLoss,
            trailingStopActive = trailingActive,
            trailingStopTriggered = triggered,
            breakSupport1450 = breaksSupport
        )
    }

    /**
     * Checks if current time is around 14:50 (A-share discipline exit window: 14:45 ~ 15:00)
     */
    fun is1450DisciplineWindow(): Boolean {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return hour == 14 && minute in 45..59
    }
}
