package com.momentjournal.data.dao

import androidx.room.*
import com.momentjournal.data.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: TagEntity): Long

    @Delete
    suspend fun delete(tag: TagEntity)

    @Query("SELECT * FROM tags WHERE isPreset = 1 ORDER BY id ASC")
    fun getPresetTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE isPreset = 0 ORDER BY id ASC")
    fun getCustomTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags ORDER BY isPreset DESC, id ASC")
    fun getAllTags(): Flow<List<TagEntity>>
}
