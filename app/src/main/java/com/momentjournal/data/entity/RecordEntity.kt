package com.momentjournal.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateTime: Long,      // epoch seconds of the recorded moment
    val createdAt: Long,     // epoch seconds when created
    val updatedAt: Long      // epoch seconds when last modified
)
