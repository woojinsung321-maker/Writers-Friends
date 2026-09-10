package com.example.ui.workspace

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.assistant.AssistantScreen
import com.example.ui.characters.CharactersScreen
import com.example.ui.editor.SceneEditorScreen
import com.example.ui.export.ExportScreen
import com.example.ui.goals.GoalsScreen
import com.example.ui.notes.NotesScreen
import com.example.ui.organize.OrganizeScreen
import com.example.ui.plot.PlotScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.stats.StatsScreen
import com.example.ui.versions.VersionsScreen
import com.example.ui.viewmodel.NovelViewModel
import com.example.ui.viewmodel.WorkspaceTab
import com.example.ui.world.WorldScreen
import kotlinx.coroutines.launch

data class WorkspaceTabItem(
    val tab: WorkspaceTab,
    val label: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectWorkspaceScreen(
    viewModel: NovelViewModel,
    onBackToHome: () -> Unit
) {
    val activeTab by viewModel.currentTab.collectAsState()
    val project by viewModel.currentProject.collectAsState()
    val totalWords by viewModel.totalWordCount.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var isViewingVersions by remember { mutableStateOf(false) }

    // Intercept back button
    BackHandler {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (isViewingVersions) {
            isViewingVersions = false
        } else if (activeTab != WorkspaceTab.WRITE && activeTab != WorkspaceTab.ORGANIZE) {
            viewModel.selectTab(WorkspaceTab.WRITE)
        } else {
            onBackToHome()
        }
    }

    val primaryTabs = listOf(
        WorkspaceTabItem(WorkspaceTab.WRITE, "Write", Icons.Default.Create),
        WorkspaceTabItem(WorkspaceTab.ORGANIZE, "Organize", Icons.Default.FormatListNumbered),
        WorkspaceTabItem(WorkspaceTab.CHARACTERS, "Cast", Icons.Default.People),
        WorkspaceTabItem(WorkspaceTab.WORLD, "World", Icons.Default.Language),
        WorkspaceTabItem(WorkspaceTab.PLOT, "Plot", Icons.Default.Timeline)
    )

    val drawerTabs = listOf(
        WorkspaceTabItem(WorkspaceTab.NOTES, "Notes & Checklists", Icons.Default.Description),
        WorkspaceTabItem(WorkspaceTab.GOALS, "Goals & Targets", Icons.Default.Flag),
        WorkspaceTabItem(WorkspaceTab.STATS, "Statistics & Metrics", Icons.Default.Speed),
        WorkspaceTabItem(WorkspaceTab.EXPORT, "Compile & Export", Icons.Default.Share),
        WorkspaceTabItem(WorkspaceTab.ASSISTANT, "Literary Muse & AI", Icons.Default.AutoAwesome),
        WorkspaceTabItem(WorkspaceTab.SETTINGS, "Project Settings", Icons.Default.Settings)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    Text(
                        text = project?.title ?: "Novel Workspace",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "$totalWords words written",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Drawer items
                primaryTabs.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.label) },
                        selected = activeTab == item.tab,
                        onClick = {
                            viewModel.selectTab(item.tab)
                            isViewingVersions = false
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                drawerTabs.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.label) },
                        selected = activeTab == item.tab,
                        onClick = {
                            viewModel.selectTab(item.tab)
                            isViewingVersions = false
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
                    label = { Text("Exit to Project Library") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onBackToHome()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                // Workspace Top Bar (only show if not inside scene editor which has its own custom toolbar)
                if (activeTab != WorkspaceTab.WRITE) {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = project?.title ?: "Novel",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    maxLines = 1
                                )
                                Text(
                                    text = "$totalWords words",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onBackToHome,
                                modifier = Modifier.testTag("exit_to_home_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit to library")
                            }
                        },
                        actions = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Book, contentDescription = "Open Drawer Menu")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                    )
                }
            },
            bottomBar = {
                // Bottom Bar showing quick access tabs
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    primaryTabs.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 11.sp) },
                            selected = activeTab == item.tab,
                            onClick = {
                                viewModel.selectTab(item.tab)
                                isViewingVersions = false
                            },
                            modifier = Modifier.testTag("nav_tab_${item.label.lowercase()}")
                        )
                    }
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Book, contentDescription = "More") },
                        label = { Text("More", fontSize = 11.sp) },
                        selected = drawerTabs.any { it.tab == activeTab },
                        onClick = { coroutineScope.launch { drawerState.open() } },
                        modifier = Modifier.testTag("nav_tab_more")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isViewingVersions) {
                    VersionsScreen(
                        viewModel = viewModel,
                        onBack = { isViewingVersions = false }
                    )
                } else {
                    when (activeTab) {
                        WorkspaceTab.WRITE -> {
                            SceneEditorScreen(
                                viewModel = viewModel,
                                onOpenVersions = { isViewingVersions = true },
                                onOpenSceneList = { viewModel.selectTab(WorkspaceTab.ORGANIZE) }
                            )
                        }
                        WorkspaceTab.ORGANIZE -> {
                            OrganizeScreen(
                                viewModel = viewModel,
                                onOpenScene = { scene ->
                                    viewModel.setActiveScene(scene)
                                    viewModel.selectTab(WorkspaceTab.WRITE)
                                }
                            )
                        }
                        WorkspaceTab.CHARACTERS -> {
                            CharactersScreen(viewModel = viewModel)
                        }
                        WorkspaceTab.WORLD -> {
                            WorldScreen(viewModel = viewModel)
                        }
                        WorkspaceTab.PLOT -> {
                            PlotScreen(
                                viewModel = viewModel,
                                onOpenScene = { scene ->
                                    viewModel.setActiveScene(scene)
                                    viewModel.selectTab(WorkspaceTab.WRITE)
                                }
                            )
                        }
                        WorkspaceTab.NOTES -> {
                            NotesScreen(viewModel = viewModel)
                        }
                        WorkspaceTab.GOALS -> {
                            GoalsScreen(viewModel = viewModel)
                        }
                        WorkspaceTab.STATS -> {
                            StatsScreen(viewModel = viewModel)
                        }
                        WorkspaceTab.EXPORT -> {
                            ExportScreen(viewModel = viewModel)
                        }
                        WorkspaceTab.ASSISTANT -> {
                            AssistantScreen(viewModel = viewModel)
                        }
                        WorkspaceTab.SETTINGS -> {
                            SettingsScreen(viewModel = viewModel)
                        }
                        WorkspaceTab.MORE -> {
                            OrganizeScreen(
                                viewModel = viewModel,
                                onOpenScene = { scene ->
                                    viewModel.setActiveScene(scene)
                                    viewModel.selectTab(WorkspaceTab.WRITE)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
