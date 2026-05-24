package com.momentjournal.util

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    fun formatTime(epochSeconds: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(epochSeconds * 1000))
    }

    fun formatDate(epochSeconds: Long): String {
        val sdf = SimpleDateFormat("M月d日 EEEE", Locale.CHINESE)
        return sdf.format(Date(epochSeconds * 1000))
    }

    fun formatMonth(epochSeconds: Long): String {
        val sdf = SimpleDateFormat("yyyy年M月", Locale.CHINESE)
        return sdf.format(Date(epochSeconds * 1000))
    }

    fun getDayStart(epochSeconds: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = epochSeconds * 1000
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis / 1000
    }

    fun getMonthDays(year: Int, month: Int): List<Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        return (1..maxDay).map { day ->
            cal.set(Calendar.DAY_OF_MONTH, day)
            cal.timeInMillis / 1000
        }
    }
}
