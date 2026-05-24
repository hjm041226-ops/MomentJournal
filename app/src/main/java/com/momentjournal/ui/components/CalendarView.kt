package com.momentjournal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentjournal.util.DateTimeUtil
import java.util.*

@Composable
fun CalendarView(
    selectedDayStart: Long,
    daysWithRecords: Set<Long>,
    onDaySelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember {
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDayStart * 1000 }
        mutableStateOf(cal.get(Calendar.YEAR) to cal.get(Calendar.MONTH))
    }

    val (year, month) = currentMonth
    val days = remember(year, month) { DateTimeUtil.getMonthDays(year, month) }
    val firstDayOfWeek = remember(days) {
        Calendar.getInstance().apply {
            timeInMillis = days.first() * 1000
        }.get(Calendar.DAY_OF_WEEK) - 1
    }

    Column(
        modifier = modifier
            .padding(horizontal = 12.dp)
    ) {
        // Month header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "◀",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable {
                        currentMonth = if (month == 0) year - 1 to 11 else year to month - 1
                    }
                    .padding(8.dp)
            )
            Text(
                DateTimeUtil.formatMonth(days.first()),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "▶",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable {
                        currentMonth = if (month == 11) year + 1 to 0 else year to month + 1
                    }
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Day of week labels
        val dayLabels = listOf("日", "一", "二", "三", "四", "五", "六")
        Row(modifier = Modifier.fillMaxWidth()) {
            dayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Day grid
        val totalCells = firstDayOfWeek + days.size
        val rows = (totalCells + 6) / 7
        val accumulatedDrag = remember { mutableStateOf(0f) }
        Box(
            modifier = Modifier.pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val (y, m) = currentMonth
                        if (accumulatedDrag.value < -40) {
                            currentMonth = if (m == 11) y + 1 to 0 else y to m + 1
                        } else if (accumulatedDrag.value > 40) {
                            currentMonth = if (m == 0) y - 1 to 11 else y to m - 1
                        }
                        accumulatedDrag.value = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        accumulatedDrag.value += dragAmount
                    }
                )
            }
        ) {
            Column {
                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0..6) {
                            val cellIndex = row * 7 + col
                            val dayIndex = cellIndex - firstDayOfWeek
                            if (dayIndex in days.indices) {
                                val dayStart = days[dayIndex]
                                val isSelected = dayStart == selectedDayStart
                                val hasRecords = dayStart in daysWithRecords
                                val dayOfMonth = Calendar.getInstance().apply {
                                    timeInMillis = dayStart * 1000
                                }.get(Calendar.DAY_OF_MONTH)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else if (hasRecords) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            else MaterialTheme.colorScheme.surface
                                        )
                                        .pointerInput(dayStart) {
                                            detectTapGestures(onTap = { onDaySelected(dayStart) })
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayOfMonth.toString(),
                                        fontSize = 13.sp,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
