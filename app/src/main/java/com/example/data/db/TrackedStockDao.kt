package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedStockDao {
    @Query("SELECT * FROM tracked_stocks ORDER BY createdAt DESC")
    fun getAllTrackedStocks(): Flow<List<TrackedStockEntity>>

    @Query("SELECT * FROM tracked_stocks WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): TrackedStockEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: TrackedStockEntity): Long

    @Update
    suspend fun update(stock: TrackedStockEntity)

    @Query("DELETE FROM tracked_stocks WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM tracked_stocks WHERE code = :code")
    suspend fun deleteByCode(code: String)
}
