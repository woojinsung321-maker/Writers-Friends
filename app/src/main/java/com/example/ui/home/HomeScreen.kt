package com.example.ui.home

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Unarchive
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
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.ui.viewmodel.NovelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NovelViewModel,
    onOpenProject: (String) -> Unit,
    onOpenGuide: () -> Unit = {}
) {
    val activeProjects by viewModel.activeProjects.collectAsState()
    val archivedProjects by viewModel.archivedProjects.collectAsState()
    val totalWords by viewModel.totalWordCount.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Active, 1: Archived
    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var projectToEdit by remember { mutableStateOf<ProjectEntity?>(null) }
    var projectToDelete by remember { mutableStateOf<ProjectEntity?>(null) }

    val displayedProjects = (if (selectedTab == 0) activeProjects else archivedProjects)
        .filter {
            if (searchQuery.isBlank()) true
            else it.title.contains(searchQuery, ignoreCase = true) ||
                    it.author.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = "Logo",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Novel Writer",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = "Personal Publishing Studio",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.testTag("import_manuscript_button")
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = "Import Manuscript")
                    }
                    IconButton(
                        onClick = { showRestoreDialog = true },
                        modifier = Modifier.testTag("restore_backup_button")
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = "Restore Backup")
                    }
                    IconButton(
                        onClick = onOpenGuide,
                        modifier = Modifier.testTag("guide_button")
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Writing Guide")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Novel", fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("create_novel_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_projects_input"),
                placeholder = { Text("Search your novels and manuscripts...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Tabs: Active vs Archived
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Active Novels (${activeProjects.size})") },
                    modifier = Modifier.testTag("active_projects_tab")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Archived (${archivedProjects.size})") },
                    modifier = Modifier.testTag("archived_projects_tab")
                )
            }

            if (displayedProjects.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = if (selectedTab == 0) "This is your personal writing workspace." else "No archived novels",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedTab == 0)
                                "Create your first project to start planning characters, structuring chapters, and writing distraction-free."
                            else
                                "Novels you archive will appear here safely stored.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        if (selectedTab == 0) {
                            OutlinedButton(
                                onClick = { showCreateDialog = true },
                                modifier = Modifier.testTag("empty_state_create_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Create Your First Project")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(displayedProjects, key = { it.id }) { project ->
                        ProjectCard(
                            project = project,
                            onOpen = { onOpenProject(project.id) },
                            onEdit = { projectToEdit = project },
                            onDuplicate = { viewModel.duplicateProject(project.id) { newId -> onOpenProject(newId) } },
                            onArchiveToggle = { viewModel.archiveProject(project.id, !project.isArchived) },
                            onDelete = { projectToDelete = project },
                            onExport = {
                                viewModel.selectProject(project.id)
                                viewModel.exportManuscript(context, "EPUB") { file ->
                                    shareFile(context, file, "application/epub+zip")
                                }
                            },
                            onBackup = {
                                viewModel.selectProject(project.id)
                                viewModel.backupProject(context) { file ->
                                    shareFile(context, file, "application/json")
                                }
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

    // Dialog: Create Project
    if (showCreateDialog) {
        CreateOrEditProjectDialog(
            initialProject = null,
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, author, desc, target, color, icon ->
                showCreateDialog = false
                viewModel.createProject(title, author, desc, target, color, icon) { newId ->
                    onOpenProject(newId)
                }
            }
        )
    }

    // Dialog: Edit Project
    projectToEdit?.let { proj ->
        CreateOrEditProjectDialog(
            initialProject = proj,
            onDismiss = { projectToEdit = null },
            onConfirm = { title, author, desc, target, color, icon ->
                viewModel.updateProject(
                    proj.copy(
                        title = title,
                        author = author,
                        description = desc,
                        targetWordCount = target,
                        coverColor = color,
                        coverIcon = icon
                    )
                )
                projectToEdit = null
            }
        )
    }

    // Dialog: Delete Confirmation
    projectToDelete?.let { proj ->
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text("Delete Novel Permanently?") },
            text = { Text("Are you sure you want to delete '${proj.title}'? All acts, chapters, scenes, characters, and notes will be permanently removed. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProject(proj.id)
                        projectToDelete = null
                    },
                    modifier = Modifier.testTag("confirm_delete_project_button")
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Import Manuscript
    if (showImportDialog) {
        ImportManuscriptDialog(
            onDismiss = { showImportDialog = false },
            onImport = { title, content ->
                showImportDialog = false
                viewModel.importManuscript(title, content) { importedId ->
                    onOpenProject(importedId)
                }
            }
        )
    }

    // Dialog: Restore Backup
    if (showRestoreDialog) {
        RestoreBackupDialog(
            onDismiss = { showRestoreDialog = false },
            onRestore = { json ->
                showRestoreDialog = false
                viewModel.restoreProject(json) { restoredId ->
                    onOpenProject(restoredId)
                }
            }
        )
    }
}

@Composable
fun ProjectCard(
    project: ProjectEntity,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onArchiveToggle: () -> Unit,
    onDelete: () -> Unit,
    onExport: () -> Unit,
    onBackup: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val dateStr = remember(project.updatedAt) {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(project.updatedAt))
    }

    ElevatedCard(
        onClick = onOpen,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_card_${project.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Realistic Hardcover Book Cover with Spine, Gilded Border & Silk Bookmark
            Box(
                modifier = Modifier
                    .width(76.dp)
                    .height(112.dp)
                    .clip(RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp, topEnd = 8.dp, bottomEnd = 8.dp))
                    .background(Color(project.coverColor))
            ) {
                // Background bookcloth subtle texture / depth gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0x77000000), // deep spine shadow on left
                                    Color(0x22000000),
                                    Color.Transparent,
                                    Color(0x18000000)  // page edge on right
                                )
                            )
                        )
                )

                // Gold foil embossed decorative frame
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0x99F59E0B),
                            shape = RoundedCornerShape(3.dp)
                        )
                )

                // Silk Ribbon Bookmark poking out at top
                Box(
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .width(7.dp)
                        .height(15.dp)
                        .clip(RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                        .background(Color(0xFFBE123C)) // Crimson silk bookmark
                )

                // Spine ribs (embossed horizontal lines on spine)
                Column(
                    modifier = Modifier
                        .width(8.dp)
                        .fillMaxHeight()
                        .padding(vertical = 14.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(Color(0x55FFFFFF))
                        )
                    }
                }

                // Center Title & Seal
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFFFDE68A), // Gilded Gold
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "A NOVEL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 7.sp,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                        color = Color(0xFFFDE68A).copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 9.sp,
                            lineHeight = 11.sp
                        ),
                        color = Color.White,
                        maxLines = 2,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Project Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Top)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("project_menu_button")
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Open Novel") },
                                onClick = { menuExpanded = false; onOpen() },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.MenuBook, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Edit Details") },
                                onClick = { menuExpanded = false; onEdit() },
                                leadingIcon = { Icon(Icons.Default.Edit, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Duplicate") },
                                onClick = { menuExpanded = false; onDuplicate() },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Export EPUB") },
                                onClick = { menuExpanded = false; onExport() },
                                leadingIcon = { Icon(Icons.Default.FileDownload, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Backup to JSON") },
                                onClick = { menuExpanded = false; onBackup() },
                                leadingIcon = { Icon(Icons.Default.Backup, null) }
                            )
                            DropdownMenuItem(
                                text = { Text(if (project.isArchived) "Unarchive" else "Archive") },
                                onClick = { menuExpanded = false; onArchiveToggle() },
                                leadingIcon = { Icon(if (project.isArchived) Icons.Default.Unarchive else Icons.Default.Archive, null) }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                onClick = { menuExpanded = false; onDelete() },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                            )
                        }
                    }
                }

                if (project.author.isNotBlank()) {
                    Text(
                        text = "by ${project.author}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (project.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Word count & Goal target
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Goal: ${project.targetWordCount} words",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CreateOrEditProjectDialog(
    initialProject: ProjectEntity?,
    onDismiss: () -> Unit,
    onConfirm: (title: String, author: String, description: String, targetWords: Int, color: Long, icon: String) -> Unit
) {
    var title by remember { mutableStateOf(initialProject?.title ?: "") }
    var author by remember { mutableStateOf(initialProject?.author ?: "") }
    var description by remember { mutableStateOf(initialProject?.description ?: "") }
    var targetWordsStr by remember { mutableStateOf(initialProject?.targetWordCount?.toString() ?: "50000") }
    var selectedColor by remember { mutableLongStateOf(initialProject?.coverColor ?: 0xFF1E3A8A) }
    var selectedIcon by remember { mutableStateOf(initialProject?.coverIcon ?: "book") }

    val presetColors = listOf(
        0xFF1E3A8A, // Classic Oxford Navy Blue
        0xFF1D4ED8, // Royal Bookcloth Blue
        0xFF0F2744, // Deep Ocean Midnight
        0xFF2563EB, // Vibrant Sapphire Blue
        0xFF1E1B4B, // Deep Indigo Leather
        0xFF0284C7  // Mediterranean Blue
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialProject == null) "Create New Novel" else "Edit Novel Details")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Novel Title *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("novel_title_input")
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Premise / Synopsis") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetWordsStr,
                    onValueChange = { targetWordsStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Target Word Count") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Cover Spine Color", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    presetColors.forEach { colorValue ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(colorValue))
                                .clickable { selectedColor = colorValue },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == colorValue) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
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
                        val words = targetWordsStr.toIntOrNull() ?: 50000
                        onConfirm(title, author, description, words, selectedColor, selectedIcon)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("confirm_create_project_button")
            ) {
                Text(if (initialProject == null) "Create Novel" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ImportManuscriptDialog(
    onDismiss: () -> Unit,
    onImport: (title: String, content: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Manuscript") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Paste your existing manuscript text. Chapters (e.g., 'Chapter 1') and scenes (split by '***') will be automatically recognized and created.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Manuscript Title *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("import_title_input")
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Manuscript Text *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .testTag("import_content_input"),
                    placeholder = { Text("Chapter 1: The Crossing\n\nThe cold wind bit...\n\n* * *\n\nLater that evening...") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onImport(title, content)
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                modifier = Modifier.testTag("confirm_import_button")
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun RestoreBackupDialog(
    onDismiss: () -> Unit,
    onRestore: (json: String) -> Unit
) {
    var jsonText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restore Novel from Backup") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Paste the contents of your exported JSON backup file. All chapters, scenes, characters, and notes will be restored safely.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = jsonText,
                    onValueChange = { jsonText = it },
                    label = { Text("Backup JSON Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("restore_json_input"),
                    placeholder = { Text("{\"project\": {\"title\": \"My Novel\"}...}") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (jsonText.isNotBlank()) {
                        onRestore(jsonText)
                    }
                },
                enabled = jsonText.isNotBlank(),
                modifier = Modifier.testTag("confirm_restore_button")
            ) {
                Text("Restore")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun shareFile(context: android.content.Context, file: java.io.File, mimeType: String) {
    try {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Export Manuscript"))
    } catch (e: Exception) {
        // Fallback toast
        android.widget.Toast.makeText(context, "Saved to ${file.name}", android.widget.Toast.LENGTH_LONG).show()
    }
}
