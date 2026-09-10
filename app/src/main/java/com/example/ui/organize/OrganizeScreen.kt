package com.example.ui.organize

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.data.model.ActEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.SceneEntity
import com.example.ui.editor.StatusBadgeChip
import com.example.ui.viewmodel.NovelViewModel
import com.example.ui.viewmodel.WorkspaceTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizeScreen(
    viewModel: NovelViewModel,
    onOpenScene: (SceneEntity) -> Unit
) {
    val acts by viewModel.acts.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val scenes by viewModel.scenes.collectAsState()
    val subplots by viewModel.subplots.collectAsState()

    var statusFilter by remember { mutableStateOf<String?>(null) }
    var subplotFilter by remember { mutableStateOf<String?>(null) }

    // Collapsed/Expanded states
    val chapterExpanded = remember { mutableStateMapOf<String, Boolean>() }

    // Dialogs
    var showCreateChapterDialog by remember { mutableStateOf(false) }
    var showCreateActDialog by remember { mutableStateOf(false) }
    var chapterToEdit by remember { mutableStateOf<ChapterEntity?>(null) }
    var chapterToDelete by remember { mutableStateOf<ChapterEntity?>(null) }
    var sceneToMove by remember { mutableStateOf<SceneEntity?>(null) }
    var sceneToDelete by remember { mutableStateOf<SceneEntity?>(null) }

    val scenesByChapter = remember(scenes, statusFilter, subplotFilter) {
        scenes
            .filter { sc ->
                (statusFilter == null || sc.status == statusFilter) &&
                (subplotFilter == null || sc.subplotName == subplotFilter)
            }
            .groupBy { it.chapterId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Story Organization", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "${acts.size} acts • ${chapters.size} chapters • ${scenes.size} scenes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { showCreateActDialog = true },
                        modifier = Modifier.testTag("add_act_button")
                    ) {
                        Text("+ Act")
                    }
                    TextButton(
                        onClick = { showCreateChapterDialog = true },
                        modifier = Modifier.testTag("add_chapter_button")
                    ) {
                        Text("+ Chapter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filters Strip
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    FilterChip(
                        selected = statusFilter == null && subplotFilter == null,
                        onClick = { statusFilter = null; subplotFilter = null },
                        label = { Text("All Scenes") }
                    )
                }
                listOf("To Do", "First Draft", "Revised", "Final Draft", "Done").forEach { st ->
                    item {
                        FilterChip(
                            selected = statusFilter == st,
                            onClick = { statusFilter = if (statusFilter == st) null else st },
                            label = { Text(st) }
                        )
                    }
                }
            }

            if (chapters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No chapters yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Create your first chapter to begin organizing scenes and acts.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = { showCreateChapterDialog = true }) {
                            Icon(Icons.Default.Add, null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create Chapter 1")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(chapters.sortedBy { it.sortOrder }, key = { it.id }) { chapter ->
                        val chapterScenes = (scenesByChapter[chapter.id] ?: emptyList()).sortedBy { it.sortOrder }
                        val isExpanded = chapterExpanded[chapter.id] ?: true
                        val totalWords = chapterScenes.sumOf { it.wordCount }

                        ChapterCard(
                            chapter = chapter,
                            scenes = chapterScenes,
                            isExpanded = isExpanded,
                            totalWords = totalWords,
                            onToggleExpand = { chapterExpanded[chapter.id] = !isExpanded },
                            onAddScene = { viewModel.createScene(chapter.id) },
                            onOpenScene = onOpenScene,
                            onEdit = { chapterToEdit = chapter },
                            onDelete = { chapterToDelete = chapter },
                            onMoveScene = { sceneToMove = it },
                            onDeleteScene = { sceneToDelete = it },
                            onReorderScene = { sc, up ->
                                val currentIdx = chapterScenes.indexOf(sc)
                                if (up && currentIdx > 0) {
                                    val other = chapterScenes[currentIdx - 1]
                                    viewModel.reorderScene(sc, other.sortOrder - 1)
                                } else if (!up && currentIdx < chapterScenes.size - 1) {
                                    val other = chapterScenes[currentIdx + 1]
                                    viewModel.reorderScene(sc, other.sortOrder + 1)
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        }
    }

    // Dialog: Create Chapter
    if (showCreateChapterDialog) {
        var chTitle by remember { mutableStateOf("") }
        var chSummary by remember { mutableStateOf("") }
        var selectedActId by remember { mutableStateOf<String?>(acts.firstOrNull()?.id) }

        AlertDialog(
            onDismissRequest = { showCreateChapterDialog = false },
            title = { Text("Create New Chapter") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = chTitle,
                        onValueChange = { chTitle = it },
                        label = { Text("Chapter Title (e.g. 'The Midnight Call')") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_chapter_title_input")
                    )
                    OutlinedTextField(
                        value = chSummary,
                        onValueChange = { chSummary = it },
                        label = { Text("Chapter Summary / Premise") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCreateChapterDialog = false
                        viewModel.createChapter(selectedActId, chTitle, chSummary)
                    },
                    modifier = Modifier.testTag("confirm_create_chapter_button")
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateChapterDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Create Act
    if (showCreateActDialog) {
        var actTitle by remember { mutableStateOf("") }
        var actDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateActDialog = false },
            title = { Text("Create Story Act") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = actTitle,
                        onValueChange = { actTitle = it },
                        label = { Text("Act Title (e.g. 'Act II: The Confrontation')") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = actDesc,
                        onValueChange = { actDesc = it },
                        label = { Text("Act Description") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (actTitle.isNotBlank()) {
                            showCreateActDialog = false
                            viewModel.createAct(actTitle, actDesc)
                        }
                    }
                ) {
                    Text("Create Act")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateActDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Move Scene to Chapter
    sceneToMove?.let { scene ->
        AlertDialog(
            onDismissRequest = { sceneToMove = null },
            title = { Text("Move Scene: '${scene.title}'") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select the destination chapter:", style = MaterialTheme.typography.bodySmall)
                    chapters.forEach { ch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.moveScene(scene.id, ch.id)
                                    sceneToMove = null
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Chapter ${ch.chapterNumber}: ${ch.title}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (ch.id == scene.chapterId) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        HorizontalDivider()
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { sceneToMove = null }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Delete Scene Confirmation
    sceneToDelete?.let { scene ->
        AlertDialog(
            onDismissRequest = { sceneToDelete = null },
            title = { Text("Delete Scene?") },
            text = { Text("Are you sure you want to delete '${scene.title}'? This will delete all its text and snapshots.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteScene(scene.id)
                        sceneToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { sceneToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Delete Chapter Confirmation
    chapterToDelete?.let { ch ->
        AlertDialog(
            onDismissRequest = { chapterToDelete = null },
            title = { Text("Delete Chapter?") },
            text = { Text("Are you sure you want to delete '${ch.title}' and all scenes inside it?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteChapter(ch.id)
                        chapterToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { chapterToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ChapterCard(
    chapter: ChapterEntity,
    scenes: List<SceneEntity>,
    isExpanded: Boolean,
    totalWords: Int,
    onToggleExpand: () -> Unit,
    onAddScene: () -> Unit,
    onOpenScene: (SceneEntity) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveScene: (SceneEntity) -> Unit,
    onDeleteScene: (SceneEntity) -> Unit,
    onReorderScene: (SceneEntity, Boolean) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Chapter Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Chapter ${chapter.chapterNumber}: ${chapter.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "$totalWords words • ${scenes.size} scenes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Chapter options")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Add Scene") },
                            onClick = { menuExpanded = false; onAddScene() },
                            leadingIcon = { Icon(Icons.Default.Add, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Chapter", color = MaterialTheme.colorScheme.error) },
                            onClick = { menuExpanded = false; onDelete() },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            }

            // Collapsible Scenes List
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    if (scenes.isEmpty()) {
                        Text(
                            "No scenes yet in this chapter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        scenes.forEachIndexed { index, scene ->
                            SceneRowItem(
                                scene = scene,
                                onOpen = { onOpenScene(scene) },
                                onMove = { onMoveScene(scene) },
                                onDelete = { onDeleteScene(scene) },
                                onMoveUp = { onReorderScene(scene, true) },
                                onMoveDown = { onReorderScene(scene, false) }
                            )
                            if (index < scenes.size - 1) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onAddScene,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Scene to Chapter ${chapter.chapterNumber}")
                    }
                }
            }
        }
    }
}

@Composable
fun SceneRowItem(
    scene: SceneEntity,
    onOpen: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = scene.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadgeChip(status = scene.status, onClick = {})
            }

            Row(
                modifier = Modifier.padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${scene.wordCount} words",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (scene.povCharacterName != null) {
                    Text(
                        text = "POV: ${scene.povCharacterName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        IconButton(onClick = onMoveUp, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
        }
        IconButton(onClick = onMoveDown, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
        }

        Box {
            IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.MoreVert, contentDescription = "Scene menu")
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text("Move to Chapter") },
                    onClick = { menuOpen = false; onMove() },
                    leadingIcon = { Icon(Icons.Default.DriveFileMove, null) }
                )
                DropdownMenuItem(
                    text = { Text("Delete Scene", color = MaterialTheme.colorScheme.error) },
                    onClick = { menuOpen = false; onDelete() },
                    leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                )
            }
        }
    }
}
