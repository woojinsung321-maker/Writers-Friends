package com.example.ui.world

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorldEntryEntity
import com.example.ui.viewmodel.NovelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldScreen(
    viewModel: NovelViewModel
) {
    val worldEntries by viewModel.worldEntries.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    var showAddDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<WorldEntryEntity?>(null) }
    var entryToDelete by remember { mutableStateOf<WorldEntryEntity?>(null) }

    val categories = listOf(
        "All", "Location", "Faction", "Culture", "Religion", "Magic System",
        "Technology", "History", "Prop/Object", "Rule", "Event", "Custom"
    )

    val filteredEntries = worldEntries.filter { entry ->
        val matchesCategory = (selectedCategory == "All" || entry.category.equals(selectedCategory, ignoreCase = true))
        val matchesSearch = if (searchQuery.isBlank()) true else {
            entry.title.contains(searchQuery, ignoreCase = true) ||
            entry.description.contains(searchQuery, ignoreCase = true) ||
            entry.tags.contains(searchQuery, ignoreCase = true)
        }
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("World-Building Lore", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "${worldEntries.size} world elements documented",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Lore Entry") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_world_entry_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search lore, locations, factions...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Category Filters
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }

            if (filteredEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No lore entries found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Document locations, magic rules, technology, factions, and props for consistent world-building.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create Lore Entry")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredEntries, key = { it.id }) { entry ->
                        WorldEntryCard(
                            entry = entry,
                            onEdit = { entryToEdit = entry },
                            onDelete = { entryToDelete = entry }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Modal: Add or Edit World Entry
    if (showAddDialog || entryToEdit != null) {
        WorldEntryDialog(
            initial = entryToEdit,
            initialCategory = if (selectedCategory != "All") selectedCategory else "Location",
            projectId = viewModel.currentProjectId.value ?: "",
            onDismiss = {
                showAddDialog = false
                entryToEdit = null
            },
            onSave = { saved ->
                viewModel.saveWorldEntry(saved)
                showAddDialog = false
                entryToEdit = null
            }
        )
    }

    // Dialog: Delete Entry
    entryToDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Delete Lore Entry?") },
            text = { Text("Are you sure you want to remove '${entry.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteWorldEntry(entry)
                        entryToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun WorldEntryCard(
    entry: WorldEntryEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = onEdit,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth().testTag("world_entry_card_${entry.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(entry.color))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = entry.title,
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
                            text = { Text("Edit Entry") },
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
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = entry.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            if (entry.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = entry.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
            }

            if (entry.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rules/Notes: ${entry.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun WorldEntryDialog(
    initial: WorldEntryEntity?,
    initialCategory: String,
    projectId: String,
    onDismiss: () -> Unit,
    onSave: (WorldEntryEntity) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: initialCategory) }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }
    var tags by remember { mutableStateOf(initial?.tags ?: "") }
    var selectedColor by remember { mutableLongStateOf(initial?.color ?: 0xFF4338CA) }

    val categories = listOf(
        "Location", "Faction", "Culture", "Religion", "Magic System",
        "Technology", "History", "Prop/Object", "Rule", "Event", "Custom"
    )

    val presetColors = listOf(
        0xFF4338CA, 0xFF059669, 0xFFD97706, 0xFFDC2626, 0xFF7C3AED, 0xFF2563EB, 0xFF4B5563
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initial == null) "New Lore Entry" else "Edit Lore Entry")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Name * (e.g. 'Citadel of Whispers')") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("world_entry_title_input")
                )

                Text("Category", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Sensory Lore") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Rules, Constraints, & Mechanics") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma-separated)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Color Accent", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    presetColors.forEach { c ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(c))
                                .clickable { selectedColor = c },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == c) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        val toSave = (initial ?: WorldEntryEntity(
                            projectId = projectId,
                            title = title,
                            category = category
                        )).copy(
                            title = title,
                            category = category,
                            description = description,
                            notes = notes,
                            tags = tags,
                            color = selectedColor
                        )
                        onSave(toSave)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("confirm_save_world_button")
            ) {
                Text("Save Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
