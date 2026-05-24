package com.momentjournal.data.repository

import com.momentjournal.data.dao.BlockDao
import com.momentjournal.data.dao.RecordDao
import com.momentjournal.data.dao.RecordTagDao
import com.momentjournal.data.entity.*
import kotlinx.coroutines.flow.Flow

class RecordRepository(
    private val recordDao: RecordDao,
    private val blockDao: BlockDao,
    private val recordTagDao: RecordTagDao
) {
    fun getDistinctDays(): Flow<List<Long>> = recordDao.getDistinctDays()

    fun getRecordsForDay(dayStart: Long): Flow<List<RecordEntity>> = recordDao.getByDay(dayStart)

    suspend fun getBlocksForRecord(recordId: Long): List<BlockEntity> = blockDao.getByRecordId(recordId)

    suspend fun getTagsForRecord(recordId: Long): List<TagEntity> = recordTagDao.getTagsForRecord(recordId)

    suspend fun saveRecord(
        dateTime: Long,
        blocks: List<BlockEntity>,
        tagIds: List<Long>,
        existingRecordId: Long? = null
    ): Long {
        val now = System.currentTimeMillis() / 1000
        val recordId = if (existingRecordId != null) {
            recordDao.update(RecordEntity(id = existingRecordId, dateTime = dateTime, createdAt = 0, updatedAt = now))
            blockDao.deleteByRecordId(existingRecordId)
            recordTagDao.deleteByRecordId(existingRecordId)
            existingRecordId
        } else {
            recordDao.insert(RecordEntity(dateTime = dateTime, createdAt = now, updatedAt = now))
        }

        blockDao.insertAll(blocks.mapIndexed { index, block ->
            block.copy(recordId = recordId, sortOrder = index)
        })

        recordTagDao.insertAll(tagIds.map { RecordTagCrossRef(recordId = recordId, tagId = it) })

        return recordId
    }

    suspend fun deleteRecord(recordId: Long) {
        val record = recordDao.getById(recordId) ?: return
        recordDao.delete(record)
    }

    suspend fun getRecordById(id: Long): RecordEntity? = recordDao.getById(id)
}
