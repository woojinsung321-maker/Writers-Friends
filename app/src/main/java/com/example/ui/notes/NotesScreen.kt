package com.example.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoryNoteEntity
import com.example.ui.viewmodel.NovelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NovelViewModel
) {
    val notes by viewModel.notes.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    var showAddDialog by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<StoryNoteEntity?>(null) }
    var noteToDelete by remember { mutableStateOf<StoryNoteEntity?>(null) }

    val categories = listOf("All", "General", "Research", "Idea", "Checklist", "Plot Hole", "Revision")

    val filteredNotes = notes.filter { note ->
        val matchesCategory = selectedCategory == "All" || note.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = if (searchQuery.isBlank()) true else {
            note.title.contains(searchQuery, ignoreCase = true) ||
            note.content.contains(searchQuery, ignoreCase = true)
        }
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Story Notes & Checklists", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("${notes.size} research notes and items", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("New Note") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_note_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search notes, research, ideas...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }

            if (filteredNotes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No notes found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Record research findings, brainstormed twists, plot holes to fix, and revision checklists.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create First Note")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        NoteCardItem(
                            note = note,
                            onEdit = { noteToEdit = note },
                            onDelete = { noteToDelete = note },
                            onToggleChecklistItem = { updatedContent ->
                                viewModel.saveNote(note.copy(content = updatedContent))
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Dialog: Add or Edit Note
    if (showAddDialog || noteToEdit != null) {
        NoteDialog(
            initial = noteToEdit,
            initialCategory = if (selectedCategory != "All") selectedCategory else "General",
            projectId = viewModel.currentProjectId.value ?: "",
            onDismiss = {
                showAddDialog = false
                noteToEdit = null
            },
            onSave = { saved ->
                viewModel.saveNote(saved)
                showAddDialog = false
                noteToEdit = null
            }
        )
    }

    // Dialog: Delete Note
    noteToDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Delete Note?") },
            text = { Text("Delete '${note.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNote(note)
                        noteToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun NoteCardItem(
    note: StoryNoteEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleChecklistItem: (String) -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = onEdit,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                }

                Box {
                    IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = { menuOpen = false; onEdit() },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            onClick = { menuOpen = false; onDelete() },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = note.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (note.isChecklist) {
                // Interactive checklist display
                val lines = note.content.split("\n").filter { it.isNotBlank() }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    lines.forEachIndexed { index, line ->
                        val isChecked = line.startsWith("[x]") || line.startsWith("[X]")
                        val itemText = line.removePrefix("[x]").removePrefix("[X]").removePrefix("[ ]").trim()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val newLines = lines.toMutableList()
                                    newLines[index] = if (isChecked) "[ ] $itemText" else "[x] $itemText"
                                    onToggleChecklistItem(newLines.joinToString("\n"))
                                }
                        ) {
                            Icon(
                                if (isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = null,
                                tint = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = itemText,
                                style = MaterialTheme.typography.bodyMedium,
                                textDecoration = if (isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 5
                )
            }
        }
    }
}

@Composable
fun NoteDialog(
    initial: StoryNoteEntity?,
    initialCategory: String,
    projectId: String,
    onDismiss: () -> Unit,
    onSave: (StoryNoteEntity) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: initialCategory) }
    var content by remember { mutableStateOf(initial?.content ?: "") }
    var isChecklist by remember { mutableStateOf(initial?.isChecklist ?: (category == "Checklist")) }

    val categories = listOf("General", "Research", "Idea", "Checklist", "Plot Hole", "Revision")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "New Story Note" else "Edit Note") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("note_title_input")
                )

                Text("Category", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = {
                                category = cat
                                if (cat == "Checklist") isChecklist = true
                            },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Format as Interactive Checklist", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isChecklist, onCheckedChange = { isChecklist = it })
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(if (isChecklist) "Checklist Items (one per line, e.g. '[ ] Check fact')" else "Note Content") },
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        val formattedContent = if (isChecklist && !content.contains("[ ]") && !content.contains("[x]")) {
                            content.split("\n").filter { it.isNotBlank() }.joinToString("\n") { "[ ] $it" }
                        } else {
                            content
                        }
                        val toSave = (initial ?: StoryNoteEntity(
                            projectId = projectId,
                            title = title
                        )).copy(
                            title = title,
                            category = category,
                            content = formattedContent,
                            isChecklist = isChecklist
                        )
                        onSave(toSave)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
