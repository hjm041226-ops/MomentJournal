package com.momentjournal.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentjournal.MomentJournalApp
import com.momentjournal.R
import com.momentjournal.data.entity.TagEntity
import com.momentjournal.data.repository.TagRepository
import com.momentjournal.ui.theme.TagColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSelectorDialog(
    selectedTagIds: List<Long>,
    onToggleTag: (Long) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MomentJournalApp
    val tagRepository = remember { TagRepository(app.database.tagDao()) }
    val allTags by tagRepository.getAllTags().collectAsState(initial = emptyList())
    var newTagName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.tag_selector_title), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) {
                    items(allTags) { tag ->
                        val isSelected = tag.id in selectedTagIds
                        val colorIndex = allTags.indexOf(tag)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { onToggleTag(tag.id) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected)
                                TagColors.getColor(colorIndex).copy(alpha = 0.3f)
                            else
                                MaterialTheme.colorScheme.surface,
                            border = if (isSelected)
                                BorderStroke(1.5.dp, TagColors.getColor(colorIndex))
                            else
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(tag.name, fontSize = 14.sp)
                                if (tag.isPreset) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("⭐", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    BasicTextField(
                        value = newTagName,
                        onValueChange = { newTagName = it },
                        modifier = Modifier.weight(1f).padding(8.dp),
                        textStyle = TextStyle(fontSize = 13.sp),
                        decorationBox = { inner ->
                            if (newTagName.isEmpty()) Text(stringResource(R.string.tag_new_placeholder), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                            inner()
                        }
                    )
                    TextButton(onClick = {
                        if (newTagName.isNotBlank()) {
                            scope.launch {
                                tagRepository.addCustomTag(newTagName)
                                newTagName = ""
                            }
                        }
                    }) {
                        Text(stringResource(R.string.tag_add), color = MaterialTheme.colorScheme.primary)
                    }
                }

                if (selectedTagIds.isNotEmpty()) {
                    Text(stringResource(R.string.tag_selected_count, selectedTagIds.size), fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.tag_save), fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.tag_cancel)) }
        }
    )
}
