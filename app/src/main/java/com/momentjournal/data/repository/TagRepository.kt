package com.momentjournal.data.repository

import com.momentjournal.data.dao.TagDao
import com.momentjournal.data.entity.TagEntity
import kotlinx.coroutines.flow.Flow

class TagRepository(private val tagDao: TagDao) {
    fun getAllTags(): Flow<List<TagEntity>> = tagDao.getAllTags()
    fun getPresetTags(): Flow<List<TagEntity>> = tagDao.getPresetTags()
    fun getCustomTags(): Flow<List<TagEntity>> = tagDao.getCustomTags()

    suspend fun addCustomTag(name: String): Long {
        return tagDao.insert(TagEntity(name = name, isPreset = false))
    }

    suspend fun deleteTag(tag: TagEntity) {
        if (!tag.isPreset) {
            tagDao.delete(tag)
        }
    }
}
