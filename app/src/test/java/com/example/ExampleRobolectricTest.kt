package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.TrackedStockEntity
import com.example.data.engine.TrailingStopEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("A股量化选股与AI投研", appName)
    }

    @Test
    fun `test trailing stop logic on profit surge`() {
        val initialStock = TrackedStockEntity(
            id = 1,
            code = "000977",
            name = "浪潮信息",
            buyPrice = 40.0,
            currentPrice = 40.0,
            highestPrice = 40.0,
            supportPrice = 38.0,
            stopLossPrice = 37.0,
            atr = 1.5,
            trailingStopActive = false
        )

        // Simulate price surge +15% to 46.0
        val updated = TrailingStopEngine.evaluate(initialStock, 46.0)

        // Trailing stop should now be activated
        assertTrue("Trailing stop should be active", updated.trailingStopActive)
        // Stop loss should have ratcheted up above buy price (46.0 - 1.5 * 1.5 = 43.75)
        assertTrue("Stop loss should be at least buy price", updated.stopLossPrice >= 40.0)
    }

    @Test
    fun `test trailing stop trigger on drawdown`() {
        val activeStock = TrackedStockEntity(
            id = 1,
            code = "000977",
            name = "浪潮信息",
            buyPrice = 40.0,
            currentPrice = 46.0,
            highestPrice = 46.0,
            supportPrice = 38.0,
            stopLossPrice = 43.75,
            atr = 1.5,
            trailingStopActive = true
        )

        // Price drops to 43.0 below stopLossPrice 43.75
        val triggered = TrailingStopEngine.evaluate(activeStock, 43.0)
        assertTrue("Trailing stop should be triggered", triggered.trailingStopTriggered)
    }
}
