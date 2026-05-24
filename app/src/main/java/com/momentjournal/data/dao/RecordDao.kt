package com.momentjournal.data.dao

import androidx.room.*
import com.momentjournal.data.entity.RecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: RecordEntity): Long

    @Update
    suspend fun update(record: RecordEntity)

    @Delete
    suspend fun delete(record: RecordEntity)

    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getById(id: Long): RecordEntity?

    @Query("SELECT * FROM records WHERE dateTime BETWEEN :dayStart AND :dayEnd ORDER BY dateTime DESC")
    fun getByDateRange(dayStart: Long, dayEnd: Long): Flow<List<RecordEntity>>

    @Query("SELECT DISTINCT dateTime / 86400 * 86400 AS dayStart FROM records ORDER BY dayStart DESC")
    fun getDistinctDays(): Flow<List<Long>>

    @Query("SELECT * FROM records WHERE dateTime / 86400 * 86400 = :dayStart")
    fun getByDay(dayStart: Long): Flow<List<RecordEntity>>
}
