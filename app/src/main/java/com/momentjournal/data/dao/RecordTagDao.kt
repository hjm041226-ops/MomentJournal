package com.momentjournal.data.dao

import androidx.room.*
import com.momentjournal.data.entity.RecordTagCrossRef
import com.momentjournal.data.entity.TagEntity

@Dao
interface RecordTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crossRef: RecordTagCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(crossRefs: List<RecordTagCrossRef>)

    @Delete
    suspend fun delete(crossRef: RecordTagCrossRef)

    @Query("DELETE FROM record_tag_cross_ref WHERE recordId = :recordId")
    suspend fun deleteByRecordId(recordId: Long)

    @Query("SELECT t.* FROM tags t INNER JOIN record_tag_cross_ref rt ON t.id = rt.tagId WHERE rt.recordId = :recordId")
    suspend fun getTagsForRecord(recordId: Long): List<TagEntity>
}
