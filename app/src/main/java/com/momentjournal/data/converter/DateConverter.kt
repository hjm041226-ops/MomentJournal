package com.momentjournal.data.converter

import androidx.room.TypeConverter

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Long? = value
}
