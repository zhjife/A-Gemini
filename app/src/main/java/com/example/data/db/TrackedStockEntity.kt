package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_stocks")
data class TrackedStockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val code: String,
    val name: String,
    val buyPrice: Double,
    val currentPrice: Double,
    val highestPrice: Double,
    val supportPrice: Double,
    val stopLossPrice: Double,
    val atr: Double = 1.2,
    val trailingStopActive: Boolean = false,
    val trailingStopTriggered: Boolean = false,
    val breakSupport1450: Boolean = false,
    val targetPrice: Double = 0.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val profitPct: Double
        get() = if (buyPrice > 0) ((currentPrice - buyPrice) / buyPrice) * 100.0 else 0.0

    val maxProfitPct: Double
        get() = if (buyPrice > 0) ((highestPrice - buyPrice) / buyPrice) * 100.0 else 0.0

    val drawdownFromPeak: Double
        get() = if (highestPrice > 0) ((highestPrice - currentPrice) / highestPrice) * 100.0 else 0.0
}
