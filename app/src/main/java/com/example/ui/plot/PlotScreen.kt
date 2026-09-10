package com.example.ui.plot

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.SceneEntity
import com.example.data.model.SubplotEntity
import com.example.ui.viewmodel.NovelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlotScreen(
    viewModel: NovelViewModel,
    onOpenScene: (SceneEntity) -> Unit
) {
    val subplots by viewModel.subplots.collectAsState()
    val scenes by viewModel.scenes.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Subplots, 1: Timeline
    var showAddSubplotDialog by remember { mutableStateOf(false) }
    var subplotToEdit by remember { mutableStateOf<SubplotEntity?>(null) }
    var subplotToDelete by remember { mutableStateOf<SubplotEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Plot & Timeline", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "${subplots.size} subplots • ${scenes.size} timeline beats",
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
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = { showAddSubplotDialog = true },
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("New Subplot") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("add_subplot_fab")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Subplots & Arcs (${subplots.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Chronological Timeline") }
                )
            }

            if (selectedTab == 0) {
                if (subplots.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No subplots defined", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Track romantic arcs, mystery clues, rivalries, and secondary plotlines across scenes.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(onClick = { showAddSubplotDialog = true }) {
                                Icon(Icons.Default.Add, null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create First Subplot")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(subplots, key = { it.id }) { sp ->
                            SubplotCardItem(
                                subplot = sp,
                                scenesCount = scenes.count { it.subplotName == sp.name },
                                onEdit = { subplotToEdit = sp },
                                onDelete = { subplotToDelete = sp }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            } else {
                // Chronological Timeline View
                val sortedScenes = scenes.sortedWith(compareBy({ it.timeSetting.isNullOrBlank() }, { it.timeSetting }, { it.sortOrder }))
                if (sortedScenes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No scenes written yet.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sortedScenes, key = { it.id }) { sc ->
                            TimelineBeatItem(scene = sc, onClick = { onOpenScene(sc) })
                        }
                    }
                }
            }
        }
    }

    // Modal: Add or Edit Subplot
    if (showAddSubplotDialog || subplotToEdit != null) {
        SubplotDialog(
            initial = subplotToEdit,
            projectId = viewModel.currentProjectId.value ?: "",
            onDismiss = {
                showAddSubplotDialog = false
                subplotToEdit = null
            },
            onSave = { saved ->
                viewModel.saveSubplot(saved)
                showAddSubplotDialog = false
                subplotToEdit = null
            }
        )
    }

    // Dialog: Delete Subplot
    subplotToDelete?.let { sp ->
        AlertDialog(
            onDismissRequest = { subplotToDelete = null },
            title = { Text("Delete Subplot?") },
            text = { Text("Delete '${sp.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSubplot(sp)
                        subplotToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { subplotToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SubplotCardItem(
    subplot: SubplotEntity,
    scenesCount: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(subplot.color))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = subplot.name,
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
                            text = { Text("Edit Subplot") },
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = subplot.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "$scenesCount linked scenes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            if (subplot.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subplot.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Arc Progress", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${subplot.progress}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { subplot.progress / 100f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
            )
        }
    }
}

@Composable
fun TimelineBeatItem(
    scene: SceneEntity,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Timeline, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(scene.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                if (scene.timeSetting != null) {
                    Text(
                        text = "Time: ${scene.timeSetting}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (scene.summary.isNotBlank()) {
                    Text(
                        text = scene.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun SubplotDialog(
    initial: SubplotEntity?,
    projectId: String,
    onDismiss: () -> Unit,
    onSave: (SubplotEntity) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var desc by remember { mutableStateOf(initial?.description ?: "") }
    var status by remember { mutableStateOf(initial?.status ?: "Active") }
    var progress by remember { mutableFloatStateOf((initial?.progress ?: 0).toFloat()) }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }
    var selectedColor by remember { mutableLongStateOf(initial?.color ?: 0xFF8B5CF6) }

    val statusOptions = listOf("Planned", "Active", "Resolved", "Abandoned")
    val colors = listOf(0xFF8B5CF6, 0xFFEC4899, 0xFF10B981, 0xFFF59E0B, 0xFF3B82F6, 0xFFEF4444)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "New Story Thread" else "Edit Subplot") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subplot Name * (e.g. 'The Stolen Heirloom')") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Status", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    statusOptions.forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Arc Resolution Progress: ${progress.toInt()}%", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = progress,
                    onValueChange = { progress = it },
                    valueRange = 0f..100f
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Subplot Description / Stake") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Plot Beats & Notes") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Color Indicator", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { c ->
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
                    if (name.isNotBlank()) {
                        val toSave = (initial ?: SubplotEntity(
                            projectId = projectId,
                            name = name
                        )).copy(
                            name = name,
                            description = desc,
                            status = status,
                            progress = progress.toInt(),
                            notes = notes,
                            color = selectedColor
                        )
                        onSave(toSave)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
