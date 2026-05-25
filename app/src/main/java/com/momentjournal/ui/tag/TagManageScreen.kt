package com.momentjournal.ui.tag

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momentjournal.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManageScreen(
    navController: androidx.navigation.NavHostController,
    viewModel: TagViewModel
) {
    val presetTags by viewModel.presetTags.collectAsState()
    val customTags by viewModel.customTags.collectAsState()
    var newTagName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tag_manage_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            Text(stringResource(R.string.tag_preset), fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                presetTags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(tag.name, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp), fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(stringResource(R.string.tag_custom), fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                customTags.forEach { tag ->
                    Surface(
                        modifier = Modifier.clickable { viewModel.deleteTag(tag) },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${tag.name} ✕", fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = newTagName,
                onValueChange = { newTagName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.tag_new_input_placeholder)) },
                trailingIcon = {
                    TextButton(onClick = {
                        if (newTagName.isNotBlank()) {
                            viewModel.addTag(newTagName)
                            newTagName = ""
                        }
                    }) {
                        Text(stringResource(R.string.tag_add_btn), color = MaterialTheme.colorScheme.primary)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }
    }
}
