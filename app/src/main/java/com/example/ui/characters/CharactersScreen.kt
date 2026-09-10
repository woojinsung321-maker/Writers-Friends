package com.example.ui.characters

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CharacterEntity
import com.example.data.model.RelationshipEntity
import com.example.data.model.SceneEntity
import com.example.ui.viewmodel.NovelViewModel
import com.example.util.CharacterImageStorage
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CharactersScreen(
    viewModel: NovelViewModel,
    onOpenScene: (SceneEntity) -> Unit = {}
) {
    val characters by viewModel.characters.collectAsState()
    val relationships by viewModel.relationships.collectAsState()

    var activeWorkspaceCharacter by remember { mutableStateOf<CharacterEntity?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Characters, 1: Relationships
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("All") }
    var selectedStatusFilter by remember { mutableStateOf("All") }

    var characterToDelete by remember { mutableStateOf<CharacterEntity?>(null) }
    var showAddRelationshipDialog by remember { mutableStateOf(false) }
    var relToDelete by remember { mutableStateOf<RelationshipEntity?>(null) }

    // If a character is opened in the dedicated workspace:
    if (activeWorkspaceCharacter != null) {
        val currentCharId = activeWorkspaceCharacter!!.id
        // Keep the reference reactive to any updates from the DB
        val latestChar = characters.firstOrNull { it.id == currentCharId } ?: activeWorkspaceCharacter!!

        CharacterWorkspaceScreen(
            character = latestChar,
            viewModel = viewModel,
            onBack = { activeWorkspaceCharacter = null },
            onOpenScene = onOpenScene
        )
        return
    }

    val roleFilters = listOf("All", "Protagonist", "Antagonist", "Deuteragonist", "Mentor", "Supporting", "POV")
    val statusFilters = listOf("All", "Alive", "Deceased", "Missing")

    val filteredCharacters = characters.filter { c ->
        val matchesQuery = if (searchQuery.isBlank()) true else {
            c.name.contains(searchQuery, ignoreCase = true) ||
                    c.nickname.contains(searchQuery, ignoreCase = true) ||
                    c.aliases.contains(searchQuery, ignoreCase = true) ||
                    c.occupation.contains(searchQuery, ignoreCase = true) ||
                    c.roleInStory.contains(searchQuery, ignoreCase = true) ||
                    c.goals.contains(searchQuery, ignoreCase = true) ||
                    c.tags.contains(searchQuery, ignoreCase = true) ||
                    c.shortDescription.contains(searchQuery, ignoreCase = true)
        }
        val matchesRole = if (selectedRoleFilter == "All") true else c.roleInStory.equals(selectedRoleFilter, ignoreCase = true)
        val matchesStatus = if (selectedStatusFilter == "All") true else c.status.equals(selectedStatusFilter, ignoreCase = true)

        matchesQuery && matchesRole && matchesStatus
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Characters & Cast",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "${characters.size} characters • ${relationships.size} relationships",
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
                onClick = {
                    if (selectedTab == 0) {
                        val newId = UUID.randomUUID().toString()
                        val projectId = viewModel.currentProjectId.value ?: ""
                        val newChar = CharacterEntity(
                            id = newId,
                            projectId = projectId,
                            name = "New Character",
                            status = "Alive",
                            color = 0xFF1E3A8A // Deep bookish navy
                        )
                        viewModel.saveCharacter(newChar)
                        activeWorkspaceCharacter = newChar
                    } else {
                        showAddRelationshipDialog = true
                    }
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(if (selectedTab == 0) "New Character" else "Map Relationship") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_character_fab")
            )
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
                    text = { Text("Cast Roster (${characters.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Relationships (${relationships.size})") }
                )
            }

            if (selectedTab == 0) {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search characters by name, role, occupation, goal...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Role Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    items(roleFilters) { role ->
                        FilterChip(
                            selected = selectedRoleFilter == role,
                            onClick = { selectedRoleFilter = role },
                            label = { Text(role, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                if (characters.isEmpty()) {
                    // Empty state matching prompt constraints: "Do not create fake characters. The character database starts empty."
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
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Character Database is Empty",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Create deep character profiles with multi-section workspaces covering psychology, desires, flaws, transformation arcs, speech patterns, and secrets.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            OutlinedButton(
                                onClick = {
                                    val newId = UUID.randomUUID().toString()
                                    val projectId = viewModel.currentProjectId.value ?: ""
                                    val newChar = CharacterEntity(
                                        id = newId,
                                        projectId = projectId,
                                        name = "New Character",
                                        status = "Alive",
                                        color = 0xFF1E3A8A
                                    )
                                    viewModel.saveCharacter(newChar)
                                    activeWorkspaceCharacter = newChar
                                }
                            ) {
                                Icon(Icons.Default.Add, null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create First Character")
                            }
                        }
                    }
                } else if (filteredCharacters.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No characters match \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredCharacters, key = { it.id }) { char ->
                            CharacterRosterCard(
                                character = char,
                                onClick = { activeWorkspaceCharacter = char },
                                onDelete = { characterToDelete = char }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            } else {
                // Relationships Tab
                if (relationships.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No relationships mapped yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Map dynamic bonds between cast members like Alliances, Rivalries, Mentorships, or Family Ties.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(onClick = { showAddRelationshipDialog = true }) {
                                Icon(Icons.Default.Add, null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Map Relationship")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(relationships, key = { it.id }) { rel ->
                            RelationshipCardItem(
                                relationship = rel,
                                onDelete = { relToDelete = rel }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal: Delete Character
    characterToDelete?.let { char ->
        AlertDialog(
            onDismissRequest = { characterToDelete = null },
            title = { Text("Delete Character?") },
            text = { Text("Are you sure you want to delete \"${char.name}\"? All notes and details will be removed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCharacter(char)
                        characterToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { characterToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // Modal: Add Relationship
    if (showAddRelationshipDialog) {
        AddRelationshipDialog(
            characters = characters,
            projectId = viewModel.currentProjectId.value ?: "",
            onDismiss = { showAddRelationshipDialog = false },
            onSave = { rel ->
                viewModel.saveRelationship(rel)
                showAddRelationshipDialog = false
            }
        )
    }

    // Modal: Delete Relationship
    relToDelete?.let { rel ->
        AlertDialog(
            onDismissRequest = { relToDelete = null },
            title = { Text("Remove Relationship?") },
            text = { Text("Remove tie between ${rel.sourceName} and ${rel.targetName}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRelationship(rel)
                        relToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { relToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun CharacterRosterCard(
    character: CharacterEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var menuOpen by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("character_card_${character.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar or Portrait
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(character.color).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (character.imageUri != null) {
                    AsyncImage(
                        model = CharacterImageStorage.getFileForPath(context, character.imageUri!!),
                        contentDescription = character.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = character.name.take(1).uppercase().ifBlank { "?" },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = character.name.ifBlank { "Untitled" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        if (character.nickname.isNotBlank()) {
                            Text(
                                text = "\"${character.nickname}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Box {
                        IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options")
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(
                                text = { Text("Open Workspace") },
                                onClick = { menuOpen = false; onClick() },
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

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (character.roleInStory.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = character.roleInStory,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (character.status == "Alive") MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = character.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (character.status == "Alive") MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (character.occupation.isNotBlank()) {
                        Text(
                            text = "• ${character.occupation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }

                if (character.shortDescription.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = character.shortDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                } else if (character.goals.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Goal: ${character.goals}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun RelationshipCardItem(
    relationship: RelationshipEntity,
    onDelete: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(relationship.sourceName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 8.dp).size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(relationship.targetName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = relationship.relationType,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "• Strength: ${relationship.strength}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (relationship.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(relationship.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddRelationshipDialog(
    characters: List<CharacterEntity>,
    projectId: String,
    onDismiss: () -> Unit,
    onSave: (RelationshipEntity) -> Unit
) {
    if (characters.size < 2) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Need At Least 2 Characters") },
            text = { Text("Please create at least two characters first before mapping relationships.") },
            confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
        )
        return
    }

    var sourceChar by remember { mutableStateOf(characters[0]) }
    var targetChar by remember { mutableStateOf(characters[1]) }
    var relType by remember { mutableStateOf("Friend") }
    var strength by remember { mutableStateOf("Strong") }
    var description by remember { mutableStateOf("") }

    val relTypes = listOf("Friend", "Enemy", "Romantic", "Family", "Rival", "Mentor", "Student", "Employer", "Member", "Complicated")
    val strengthOptions = listOf("Weak", "Moderate", "Strong", "Unbreakable")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Map Character Relationship") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Between:", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(sourceChar.name, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.SwapHoriz, null, tint = MaterialTheme.colorScheme.primary)
                    Text(targetChar.name, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Relationship Nature:", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(relTypes) { t ->
                        FilterChip(
                            selected = relType == t,
                            onClick = { relType = t },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Bond Strength:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    strengthOptions.forEach { s ->
                        FilterChip(
                            selected = strength == s,
                            onClick = { strength = s },
                            label = { Text(s, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Dynamic / History Details") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        RelationshipEntity(
                            projectId = projectId,
                            sourceId = sourceChar.id,
                            sourceName = sourceChar.name,
                            targetId = targetChar.id,
                            targetName = targetChar.name,
                            relationType = relType,
                            strength = strength,
                            description = description
                        )
                    )
                }
            ) {
                Text("Save Relationship")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
