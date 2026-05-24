package com.momentjournal.data.dao

import androidx.room.*
import com.momentjournal.data.entity.BlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(block: BlockEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(blocks: List<BlockEntity>)

    @Update
    suspend fun update(block: BlockEntity)

    @Delete
    suspend fun delete(block: BlockEntity)

    @Query("SELECT * FROM blocks WHERE recordId = :recordId ORDER BY sortOrder ASC")
    suspend fun getByRecordId(recordId: Long): List<BlockEntity>

    @Query("SELECT * FROM blocks WHERE recordId = :recordId ORDER BY sortOrder ASC")
    fun observeByRecordId(recordId: Long): Flow<List<BlockEntity>>

    @Query("DELETE FROM blocks WHERE recordId = :recordId")
    suspend fun deleteByRecordId(recordId: Long)
}
