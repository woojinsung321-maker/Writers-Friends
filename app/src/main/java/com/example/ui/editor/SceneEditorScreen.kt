package com.example.ui.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterEntity
import com.example.data.model.SceneEntity
import com.example.data.model.WorldEntryEntity
import com.example.ui.theme.PaperLightBg
import com.example.ui.theme.StatusDraft
import com.example.ui.theme.StatusFinal
import com.example.ui.theme.StatusRevised
import com.example.ui.theme.StatusTodo
import com.example.ui.viewmodel.NovelViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SceneEditorScreen(
    viewModel: NovelViewModel,
    onOpenVersions: () -> Unit,
    onOpenSceneList: () -> Unit
) {
    val project by viewModel.currentProject.collectAsState()
    val activeScene by viewModel.activeScene.collectAsState()
    val characters by viewModel.characters.collectAsState()
    val worldEntries by viewModel.worldEntries.collectAsState()
    val subplots by viewModel.subplots.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val settings by viewModel.projectSettings.collectAsState()

    if (activeScene == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No scene selected", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onOpenSceneList) {
                    Text("Select or Create a Scene")
                }
            }
        }
        return
    }

    val scene = activeScene!!
    val currentChapter = chapters.find { it.id == scene.chapterId }

    // Text Editor State with Undo/Redo support
    var textValue by remember(scene.id) {
        mutableStateOf(TextFieldValue(scene.body, selection = TextRange(scene.body.length)))
    }

    // Local Undo/Redo stacks
    val undoStack = remember(scene.id) { mutableStateListOf<String>() }
    val redoStack = remember(scene.id) { mutableStateListOf<String>() }

    // Synchronize external changes (e.g. from version restore)
    LaunchedEffect(scene.body) {
        if (scene.body != textValue.text) {
            textValue = textValue.copy(text = scene.body)
        }
    }

    // Focus mode toggle
    var isFocusMode by remember { mutableStateOf(false) }

    // Search and Replace Bar
    var showFindReplace by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var replaceQuery by remember { mutableStateOf("") }

    // Metadata Edit Modal
    var showMetadataDialog by remember { mutableStateOf(false) }

    // Reference Drawer / Bottom Sheet
    var showReferenceSheet by remember { mutableStateOf(false) }

    // Mention / Quick Insert Picker
    var showMentionSheet by remember { mutableStateOf(false) }

    // Snapshot Dialog
    var showSnapshotDialog by remember { mutableStateOf(false) }

    val fontFamily = when (settings?.defaultFontFamily) {
        "Sans" -> FontFamily.SansSerif
        "Monospace" -> FontFamily.Monospace
        else -> FontFamily.Serif
    }
    val fontSize = (settings?.fontSizeSp ?: 17f).sp
    val lineSpacing = (settings?.lineSpacing ?: 1.6f)

    Scaffold(
        topBar = {
            if (!isFocusMode) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = scene.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "${currentChapter?.title ?: "Chapter"} • ${scene.wordCount} words • ${scene.characterCount} chars",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onOpenSceneList) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Scenes")
                        }
                    },
                    actions = {
                        // Status badge button
                        StatusBadgeChip(
                            status = scene.status,
                            onClick = { showMetadataDialog = true }
                        )

                        // Quick Reference
                        IconButton(
                            onClick = { showReferenceSheet = true },
                            modifier = Modifier.testTag("editor_references_button")
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = "Project Lore & References")
                        }

                        // Focus Mode
                        IconButton(
                            onClick = { isFocusMode = true },
                            modifier = Modifier.testTag("enter_focus_mode_button")
                        ) {
                            Icon(Icons.Default.Fullscreen, contentDescription = "Distraction-Free Mode")
                        }

                        // Version History
                        IconButton(
                            onClick = onOpenVersions,
                            modifier = Modifier.testTag("editor_versions_button")
                        ) {
                            Icon(Icons.Default.History, contentDescription = "Scene Versions")
                        }

                        // Metadata / Details
                        IconButton(
                            onClick = { showMetadataDialog = true },
                            modifier = Modifier.testTag("scene_details_button")
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "Scene Details")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            // Formatting & Quick Tools Bar
            Surface(
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Column {
                    // Find and Replace Strip
                    AnimatedVisibility(visible = showFindReplace) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Find...") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            OutlinedTextField(
                                value = replaceQuery,
                                onValueChange = { replaceQuery = it },
                                placeholder = { Text("Replace...") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = {
                                    if (searchQuery.isNotEmpty()) {
                                        val newText = textValue.text.replace(searchQuery, replaceQuery)
                                        undoStack.add(textValue.text)
                                        textValue = TextFieldValue(newText, selection = TextRange(newText.length))
                                        viewModel.onSceneContentChanged(newText)
                                    }
                                }
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = "Replace All")
                            }
                            IconButton(onClick = { showFindReplace = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close Find")
                            }
                        }
                    }

                    // Formatting Buttons Row
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Undo
                        item {
                            IconButton(
                                onClick = {
                                    if (undoStack.isNotEmpty()) {
                                        val previous = undoStack.removeAt(undoStack.lastIndex)
                                        redoStack.add(textValue.text)
                                        textValue = TextFieldValue(previous, selection = TextRange(previous.length))
                                        viewModel.onSceneContentChanged(previous)
                                    }
                                },
                                enabled = undoStack.isNotEmpty()
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
                            }
                        }
                        // Redo
                        item {
                            IconButton(
                                onClick = {
                                    if (redoStack.isNotEmpty()) {
                                        val next = redoStack.removeAt(redoStack.lastIndex)
                                        undoStack.add(textValue.text)
                                        textValue = TextFieldValue(next, selection = TextRange(next.length))
                                        viewModel.onSceneContentChanged(next)
                                    }
                                },
                                enabled = redoStack.isNotEmpty()
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo")
                            }
                        }

                        // Bold
                        item {
                            IconButton(onClick = {
                                textValue = wrapSelection(textValue, "**", "**")
                                viewModel.onSceneContentChanged(textValue.text)
                            }) {
                                Icon(Icons.Default.FormatBold, contentDescription = "Bold")
                            }
                        }
                        // Italic
                        item {
                            IconButton(onClick = {
                                textValue = wrapSelection(textValue, "*", "*")
                                viewModel.onSceneContentChanged(textValue.text)
                            }) {
                                Icon(Icons.Default.FormatItalic, contentDescription = "Italic")
                            }
                        }
                        // Heading 1
                        item {
                            IconButton(onClick = {
                                textValue = insertPrefix(textValue, "# ")
                                viewModel.onSceneContentChanged(textValue.text)
                            }) {
                                Text("H1", fontWeight = FontWeight.Bold)
                            }
                        }
                        // Heading 2
                        item {
                            IconButton(onClick = {
                                textValue = insertPrefix(textValue, "## ")
                                viewModel.onSceneContentChanged(textValue.text)
                            }) {
                                Text("H2", fontWeight = FontWeight.Bold)
                            }
                        }
                        // Quote
                        item {
                            IconButton(onClick = {
                                textValue = insertPrefix(textValue, "> ")
                                viewModel.onSceneContentChanged(textValue.text)
                            }) {
                                Icon(Icons.Default.FormatQuote, contentDescription = "Blockquote")
                            }
                        }
                        // Bullet
                        item {
                            IconButton(onClick = {
                                textValue = insertPrefix(textValue, "- ")
                                viewModel.onSceneContentChanged(textValue.text)
                            }) {
                                Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet List")
                            }
                        }
                        // Scene Break (* * *)
                        item {
                            IconButton(onClick = {
                                textValue = insertText(textValue, "\n\n* * *\n\n")
                                viewModel.onSceneContentChanged(textValue.text)
                            }) {
                                Text("* * *", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }
                        // Quick Mention (@)
                        item {
                            IconButton(onClick = { showMentionSheet = true }) {
                                Icon(Icons.Default.AlternateEmail, contentDescription = "Insert Character/Location")
                            }
                        }
                        // Find/Replace
                        item {
                            IconButton(onClick = { showFindReplace = !showFindReplace }) {
                                Icon(Icons.Default.Search, contentDescription = "Find & Replace")
                            }
                        }
                        // Snapshot
                        item {
                            IconButton(onClick = { showSnapshotDialog = true }) {
                                Icon(Icons.Default.History, contentDescription = "Save Snapshot")
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Editor text area with responsive reading width
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = if (isFocusMode) 12.dp else 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Focus mode header with minimal word counter and exit button
                if (isFocusMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 740.dp)
                            .padding(bottom = 12.dp, start = 8.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${project?.title ?: "Novel"} • ${scene.title} (${scene.wordCount} words)",
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Serif),
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = { isFocusMode = false },
                            modifier = Modifier.testTag("exit_focus_mode_button")
                        ) {
                            Icon(Icons.Default.FullscreenExit, contentDescription = "Exit Focus Mode")
                        }
                    }
                }

                // Physical Book Page Container
                ElevatedCard(
                    shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp, topEnd = 6.dp, bottomEnd = 6.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 740.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Book spine gutter shadow on the left edge
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .matchParentSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0x22000000),
                                            Color(0x0A000000),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = if (isFocusMode) 28.dp else 32.dp,
                                    end = if (isFocusMode) 24.dp else 28.dp,
                                    top = 22.dp,
                                    bottom = 28.dp
                                )
                        ) {
                            // Running Header: Book Title • ❧ • Chapter Title
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = (project?.title ?: "MANUSCRIPT").uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        letterSpacing = 1.6.sp,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                    maxLines = 1
                                )
                                Text(
                                    text = "❧",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = (currentChapter?.title ?: "SCENE").uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        letterSpacing = 1.6.sp,
                                        fontSize = 10.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    maxLines = 1
                                )
                            }
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                                modifier = Modifier.padding(top = 8.dp, bottom = 18.dp)
                            )

                            // Scene Title editable header in normal mode
                            if (!isFocusMode) {
                                // Chapter Small Caps Label
                                Text(
                                    text = (currentChapter?.title ?: "CHAPTER").uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        letterSpacing = 2.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                BasicTextField(
                                    value = scene.title,
                                    onValueChange = { newTitle ->
                                        viewModel.updateActiveSceneMetadata(
                                            title = newTitle,
                                            status = scene.status,
                                            povName = scene.povCharacterName,
                                            locationName = scene.locationName,
                                            timeSetting = scene.timeSetting,
                                            summary = scene.summary,
                                            purpose = scene.purpose,
                                            notes = scene.notes
                                        )
                                    },
                                    textStyle = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp)
                                        .testTag("scene_title_editor_input")
                                )

                                // Classic book ornamental divider
                                Text(
                                    text = "— ✦ —",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(bottom = 12.dp)
                                )

                                // Quick metadata chips
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (scene.povCharacterName != null) {
                                        AssistChip(
                                            onClick = { showMetadataDialog = true },
                                            label = { Text("POV: ${scene.povCharacterName}") },
                                            leadingIcon = { Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp)) }
                                        )
                                    }
                                    if (scene.locationName != null) {
                                        AssistChip(
                                            onClick = { showMetadataDialog = true },
                                            label = { Text("Loc: ${scene.locationName}") },
                                            leadingIcon = { Icon(Icons.Default.Place, null, modifier = Modifier.size(16.dp)) }
                                        )
                                    }
                                    if (scene.summary.isNotBlank()) {
                                        AssistChip(
                                            onClick = { showMetadataDialog = true },
                                            label = { Text("Summary: ${scene.summary.take(20)}...") },
                                            leadingIcon = { Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp)) }
                                        )
                                    }
                                }
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.padding(bottom = 18.dp)
                                )
                            }

                            // Core Writing Canvas (Book Page Interior)
                            BasicTextField(
                                value = textValue,
                                onValueChange = { newValue ->
                                    if (newValue.text != textValue.text) {
                                        undoStack.add(textValue.text)
                                        redoStack.clear()
                                        viewModel.onSceneContentChanged(newValue.text)
                                    }
                                    textValue = newValue
                                },
                                textStyle = TextStyle(
                                    fontFamily = fontFamily,
                                    fontSize = fontSize,
                                    lineHeight = (fontSize.value * lineSpacing).sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1000.dp) // Generous scrollable writing height
                                    .testTag("scene_body_editor_input")
                            )

                            // Classic Book Page Footer
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                modifier = Modifier.padding(top = 28.dp, bottom = 12.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "— ${scene.wordCount} words —",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Serif,
                                        letterSpacing = 1.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "❖",
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Scene Metadata & Planning Inspector
    if (showMetadataDialog) {
        SceneMetadataDialog(
            scene = scene,
            characters = characters,
            worldEntries = worldEntries,
            subplots = subplots,
            onDismiss = { showMetadataDialog = false },
            onSave = { title, status, pov, loc, time, summary, purpose, notes ->
                viewModel.updateActiveSceneMetadata(title, status, pov, loc, time, summary, purpose, notes)
                showMetadataDialog = false
            }
        )
    }

    // Modal: Side References Sheet
    if (showReferenceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReferenceSheet = false }
        ) {
            ReferenceSheetContent(
                characters = characters,
                worldEntries = worldEntries,
                scene = scene,
                onInsertText = { toInsert ->
                    textValue = insertText(textValue, toInsert)
                    viewModel.onSceneContentChanged(textValue.text)
                    showReferenceSheet = false
                }
            )
        }
    }

    // Modal: Quick Mention / Insert
    if (showMentionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMentionSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Insert Story Reference", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    item {
                        Text("Characters", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    items(characters) { char ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    textValue = insertText(textValue, char.name)
                                    viewModel.onSceneContentChanged(textValue.text)
                                    showMentionSheet = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(char.color)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(char.name.take(1), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(char.name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("World & Locations", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    items(worldEntries) { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    textValue = insertText(textValue, entry.title)
                                    viewModel.onSceneContentChanged(textValue.text)
                                    showMentionSheet = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Place, null, tint = Color(entry.color), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(entry.title, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }

    // Modal: Save Snapshot
    if (showSnapshotDialog) {
        var snapName by remember { mutableStateOf("Snapshot ${scene.wordCount} words") }
        var snapNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSnapshotDialog = false },
            title = { Text("Create Version Snapshot") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Save a point-in-time recovery backup of this scene.", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = snapName,
                        onValueChange = { snapName = it },
                        label = { Text("Version Label") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = snapNotes,
                        onValueChange = { snapNotes = it },
                        label = { Text("Notes (e.g. before major rewrite)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.createVersionSnapshot(snapName, snapNotes)
                        showSnapshotDialog = false
                    }
                ) {
                    Text("Save Snapshot")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSnapshotDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun StatusBadgeChip(status: String, onClick: () -> Unit) {
    val color = when (status) {
        "First Draft" -> StatusDraft
        "Revised" -> StatusRevised
        "Final Draft" -> StatusFinal
        "Done" -> Color(0xFF10B981)
        else -> StatusTodo
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f),
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ReferenceSheetContent(
    characters: List<CharacterEntity>,
    worldEntries: List<WorldEntryEntity>,
    scene: SceneEntity,
    onInsertText: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Characters, 1: World, 2: Scene Notes

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
            Text("Story Lore & References", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                label = { Text("Characters (${characters.size})") }
            )
            FilterChip(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                label = { Text("World (${worldEntries.size})") }
            )
            FilterChip(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                label = { Text("Scene Notes") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.height(350.dp)) {
            if (selectedTab == 0) {
                items(characters) { char ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(char.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                TextButton(onClick = { onInsertText(char.name) }) {
                                    Text("Insert", fontSize = 12.sp)
                                }
                            }
                            if (char.occupation.isNotBlank()) Text("Role: ${char.occupation}", style = MaterialTheme.typography.bodySmall)
                            if (char.goals.isNotBlank()) Text("Goal: ${char.goals}", style = MaterialTheme.typography.bodySmall)
                            if (char.internalConflict.isNotBlank()) Text("Conflict: ${char.internalConflict}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else if (selectedTab == 1) {
                items(worldEntries) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${entry.title} (${entry.category})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                TextButton(onClick = { onInsertText(entry.title) }) {
                                    Text("Insert", fontSize = 12.sp)
                                }
                            }
                            if (entry.description.isNotBlank()) Text(entry.description, style = MaterialTheme.typography.bodySmall)
                            if (entry.notes.isNotBlank()) Text("Notes: ${entry.notes}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Summary:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text(scene.summary.ifBlank { "No summary specified." }, style = MaterialTheme.typography.bodyMedium)

                        Text("Dramatic Purpose:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text(scene.purpose.ifBlank { "No purpose specified." }, style = MaterialTheme.typography.bodyMedium)

                        Text("Working Notes:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        Text(scene.notes.ifBlank { "No notes." }, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun SceneMetadataDialog(
    scene: SceneEntity,
    characters: List<CharacterEntity>,
    worldEntries: List<WorldEntryEntity>,
    subplots: List<com.example.data.model.SubplotEntity>,
    onDismiss: () -> Unit,
    onSave: (title: String, status: String, pov: String?, loc: String?, time: String?, summary: String, purpose: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf(scene.title) }
    var status by remember { mutableStateOf(scene.status) }
    var pov by remember { mutableStateOf(scene.povCharacterName ?: "") }
    var loc by remember { mutableStateOf(scene.locationName ?: "") }
    var time by remember { mutableStateOf(scene.timeSetting ?: "") }
    var summary by remember { mutableStateOf(scene.summary) }
    var purpose by remember { mutableStateOf(scene.purpose) }
    var notes by remember { mutableStateOf(scene.notes) }

    val statusOptions = listOf("To Do", "First Draft", "Revised", "Final Draft", "Done")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Scene Information & Goals") },
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
                    label = { Text("Scene Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Status", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statusOptions.forEach { opt ->
                        FilterChip(
                            selected = status == opt,
                            onClick = { status = opt },
                            label = { Text(opt, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = pov,
                    onValueChange = { pov = it },
                    label = { Text("POV Character") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = loc,
                    onValueChange = { loc = it },
                    label = { Text("Setting / Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Story Timeline / Date") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Scene Summary") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    label = { Text("Dramatic Purpose (Why must this scene exist?)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Scratchpad Notes") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        title,
                        status,
                        pov.ifBlank { null },
                        loc.ifBlank { null },
                        time.ifBlank { null },
                        summary,
                        purpose,
                        notes
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// Helpers for editor formatting actions
fun wrapSelection(value: TextFieldValue, prefix: String, suffix: String): TextFieldValue {
    val selection = value.selection
    val text = value.text
    return if (selection.collapsed) {
        val newText = text.substring(0, selection.start) + prefix + suffix + text.substring(selection.end)
        value.copy(
            text = newText,
            selection = TextRange(selection.start + prefix.length)
        )
    } else {
        val selectedText = text.substring(selection.min, selection.max)
        val newText = text.substring(0, selection.min) + prefix + selectedText + suffix + text.substring(selection.max)
        value.copy(
            text = newText,
            selection = TextRange(selection.min + prefix.length, selection.max + prefix.length)
        )
    }
}

fun insertPrefix(value: TextFieldValue, prefix: String): TextFieldValue {
    val selection = value.selection
    val text = value.text
    // find start of current line
    val lineStart = text.lastIndexOf('\n', (selection.start - 1).coerceAtLeast(0)).let {
        if (it == -1) 0 else it + 1
    }
    val newText = text.substring(0, lineStart) + prefix + text.substring(lineStart)
    return value.copy(
        text = newText,
        selection = TextRange(selection.start + prefix.length)
    )
}

fun insertText(value: TextFieldValue, textToInsert: String): TextFieldValue {
    val selection = value.selection
    val text = value.text
    val newText = text.substring(0, selection.min) + textToInsert + text.substring(selection.max)
    return value.copy(
        text = newText,
        selection = TextRange(selection.min + textToInsert.length)
    )
}
