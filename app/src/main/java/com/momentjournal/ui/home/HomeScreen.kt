package com.momentjournal.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.momentjournal.MomentJournalApp
import com.momentjournal.ui.components.CalendarView
import com.momentjournal.ui.components.EmptyState
import com.momentjournal.ui.navigation.Routes
import com.momentjournal.util.DateTimeUtil

@Composable
fun HomeScreen(
    navController: androidx.navigation.NavHostController,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
            com.momentjournal.data.repository.RecordRepository(
                (androidx.compose.ui.platform.LocalContext.current.applicationContext as MomentJournalApp)
                    .database.recordDao(),
                (androidx.compose.ui.platform.LocalContext.current.applicationContext as MomentJournalApp)
                    .database.blockDao(),
                (androidx.compose.ui.platform.LocalContext.current.applicationContext as MomentJournalApp)
                    .database.recordTagDao()
            )
        )
    )
) {
    val selectedDayStart by viewModel.selectedDayStart.collectAsState()
    val daysWithRecords by viewModel.daysWithRecords.collectAsState()
    val records by viewModel.recordsForSelectedDay.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.editor()) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CalendarView(
                selectedDayStart = selectedDayStart,
                daysWithRecords = daysWithRecords,
                onDaySelected = { viewModel.selectDay(it) }
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = DateTimeUtil.formatDate(selectedDayStart),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                TextButton(onClick = { navController.navigate(Routes.TAG_MANAGE) }) {
                    Text("🏷 标签", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
            }

            if (records.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        emoji = "🐾",
                        title = "今天还没有记录哦~",
                        subtitle = "点击右下角 + 记录此刻吧 ✨"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(records, key = { it.id }) { record ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp
                        ) {
                            Text(
                                text = DateTimeUtil.formatTime(record.dateTime),
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
