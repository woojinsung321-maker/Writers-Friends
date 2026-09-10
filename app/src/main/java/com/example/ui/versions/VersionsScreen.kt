package com.example.ui.versions

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import com.example.data.model.SceneVersionEntity
import com.example.ui.viewmodel.NovelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionsScreen(
    viewModel: NovelViewModel,
    onBack: () -> Unit
) {
    val activeScene by viewModel.activeScene.collectAsState()
    val versions by viewModel.sceneVersions.collectAsState()

    var showCreateSnapshotDialog by remember { mutableStateOf(false) }
    var versionToRestore by remember { mutableStateOf<SceneVersionEntity?>(null) }
    var versionToDelete by remember { mutableStateOf<SceneVersionEntity?>(null) }
    var previewVersion by remember { mutableStateOf<SceneVersionEntity?>(null) }

    val scene = activeScene

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Version History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            scene?.title ?: "Scene Versions",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Editor")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateSnapshotDialog = true },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Take Snapshot") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("take_snapshot_fab")
            )
        }
    ) { innerPadding ->
        if (versions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No snapshots saved yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Take snapshots at any time before major edits, rewrites, or cuts to preserve past drafts safely.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(onClick = { showCreateSnapshotDialog = true }) {
                        Icon(Icons.Default.Add, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save First Snapshot")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(versions, key = { it.id }) { ver ->
                    val isPreviewing = previewVersion?.id == ver.id
                    val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
                    val dateString = dateFormat.format(Date(ver.createdAt))

                    ElevatedCard(
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ver.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$dateString • ${ver.wordCount} words",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row {
                                    IconButton(onClick = { versionToRestore = ver }) {
                                        Icon(Icons.Default.Restore, contentDescription = "Restore this version", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { versionToDelete = ver }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete version", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }

                            if (ver.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = ver.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    previewVersion = if (isPreviewing) null else ver
                                }
                            ) {
                                Text(if (isPreviewing) "Hide Draft Preview" else "Preview Draft Text")
                            }

                            AnimatedVisibility(visible = isPreviewing) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .testTag("version_preview_surface")
                                ) {
                                    Text(
                                        text = ver.bodySnapshot.ifBlank { "(Empty scene)" },
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = FontFamily.Serif,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }

    // Modal: Create Snapshot
    if (showCreateSnapshotDialog) {
        var label by remember { mutableStateOf("Draft Snapshot (${scene?.wordCount ?: 0} words)") }
        var note by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateSnapshotDialog = false },
            title = { Text("Take Snapshot") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("Snapshot Label") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Notes (optional reason for saving)") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.createVersionSnapshot(label, note)
                        showCreateSnapshotDialog = false
                    }
                ) {
                    Text("Save Snapshot")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateSnapshotDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Modal: Restore Confirmation
    versionToRestore?.let { ver ->
        AlertDialog(
            onDismissRequest = { versionToRestore = null },
            title = { Text("Restore this Version?") },
            text = {
                Text(
                    "Restoring '${ver.name}' will replace the current editor text with this version (${ver.wordCount} words). A safety snapshot of your current text will be preserved."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Take safety snapshot first
                        viewModel.createVersionSnapshot("Auto-backup before restore", "")
                        // Restore
                        viewModel.restoreVersion(ver)
                        versionToRestore = null
                        onBack()
                    }
                ) {
                    Text("Restore Version", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { versionToRestore = null }) { Text("Cancel") }
            }
        )
    }

    // Modal: Delete Confirmation
    versionToDelete?.let { ver ->
        AlertDialog(
            onDismissRequest = { versionToDelete = null },
            title = { Text("Delete Snapshot?") },
            text = { Text("Are you sure you want to permanently delete snapshot '${ver.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteVersion(ver)
                        versionToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { versionToDelete = null }) { Text("Cancel") }
            }
        )
    }
}
