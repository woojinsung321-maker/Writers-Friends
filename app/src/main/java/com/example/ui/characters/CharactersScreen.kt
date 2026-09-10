package com.example.ui.characters

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterEntity
import com.example.data.model.RelationshipEntity
import com.example.ui.viewmodel.NovelViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    viewModel: NovelViewModel
) {
    val characters by viewModel.characters.collectAsState()
    val relationships by viewModel.relationships.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Characters, 1: Relationships
    var searchQuery by remember { mutableStateOf("") }

    var characterToEdit by remember { mutableStateOf<CharacterEntity?>(null) }
    var showAddCharacterDialog by remember { mutableStateOf(false) }
    var characterToDelete by remember { mutableStateOf<CharacterEntity?>(null) }

    var showAddRelationshipDialog by remember { mutableStateOf(false) }
    var relToDelete by remember { mutableStateOf<RelationshipEntity?>(null) }

    val filteredCharacters = characters.filter {
        if (searchQuery.isBlank()) true
        else it.name.contains(searchQuery, ignoreCase = true) ||
                it.occupation.contains(searchQuery, ignoreCase = true) ||
                it.goals.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Characters & Cast", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "${characters.size} characters • ${relationships.size} relationships",
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
                    if (selectedTab == 0) showAddCharacterDialog = true
                    else showAddRelationshipDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(if (selectedTab == 0) "New Character" else "New Relationship") },
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
                // Characters List View
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search characters by name, role, goal...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                if (filteredCharacters.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No characters yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Create character profiles with detailed motivations, flaws, arcs, and relationships.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(onClick = { showAddCharacterDialog = true }) {
                                Icon(Icons.Default.Add, null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Create First Character")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredCharacters, key = { it.id }) { char ->
                            CharacterCardItem(
                                character = char,
                                onEdit = { characterToEdit = char },
                                onDelete = { characterToDelete = char }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            } else {
                // Relationships View
                if (relationships.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No relationships mapped", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Connect characters and factions with ties like Friendship, Enmity, Romance, or Mentorship.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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

    // Modal: Add or Edit Character
    if (showAddCharacterDialog || characterToEdit != null) {
        val editing = characterToEdit
        CharacterDetailDialog(
            initial = editing,
            projectId = viewModel.currentProjectId.value ?: "",
            onDismiss = {
                showAddCharacterDialog = false
                characterToEdit = null
            },
            onSave = { updated ->
                viewModel.saveCharacter(updated)
                showAddCharacterDialog = false
                characterToEdit = null
            }
        )
    }

    // Dialog: Delete Character
    characterToDelete?.let { char ->
        AlertDialog(
            onDismissRequest = { characterToDelete = null },
            title = { Text("Delete Character?") },
            text = { Text("Are you sure you want to remove '${char.name}'?") },
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

    // Dialog: Delete Relationship
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
fun CharacterCardItem(
    character: CharacterEntity,
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
        modifier = Modifier.fillMaxWidth().testTag("character_card_${character.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar badge with character color
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(character.color)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = character.name.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = character.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )

                    Box {
                        IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options")
                        }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(
                                text = { Text("Edit Profile") },
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

                if (character.occupation.isNotBlank()) {
                    Text(
                        text = character.occupation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (character.goals.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Goal: ${character.goals}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }

                if (character.internalConflict.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Conflict: ${character.internalConflict}",
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
fun CharacterDetailDialog(
    initial: CharacterEntity?,
    projectId: String,
    onDismiss: () -> Unit,
    onSave: (CharacterEntity) -> Unit
) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Basic, 1: Psychology & Arc, 2: Notes

    var name by remember { mutableStateOf(initial?.name ?: "") }
    var nickname by remember { mutableStateOf(initial?.nickname ?: "") }
    var age by remember { mutableStateOf(initial?.age ?: "") }
    var gender by remember { mutableStateOf(initial?.gender ?: "") }
    var occupation by remember { mutableStateOf(initial?.occupation ?: "") }
    var appearance by remember { mutableStateOf(initial?.appearance ?: "") }
    var personality by remember { mutableStateOf(initial?.personality ?: "") }

    var goals by remember { mutableStateOf(initial?.goals ?: "") }
    var motivation by remember { mutableStateOf(initial?.motivation ?: "") }
    var fears by remember { mutableStateOf(initial?.fears ?: "") }
    var strengths by remember { mutableStateOf(initial?.strengths ?: "") }
    var weaknesses by remember { mutableStateOf(initial?.weaknesses ?: "") }
    var secrets by remember { mutableStateOf(initial?.secrets ?: "") }
    var characterArc by remember { mutableStateOf(initial?.characterArc ?: "") }
    var internalConflict by remember { mutableStateOf(initial?.internalConflict ?: "") }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }
    var selectedColor by remember { mutableLongStateOf(initial?.color ?: 0xFFB45309) }

    val presetColors = listOf(
        0xFFB45309, 0xFF312E81, 0xFF047857, 0xFF9D174D, 0xFF1E293B, 0xFF6D28D9, 0xFFC2410C
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initial == null) "New Character Profile" else "Edit: ${initial.name}")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Profile Section Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedSection,
                    edgePadding = 0.dp
                ) {
                    Tab(selected = selectedSection == 0, onClick = { selectedSection = 0 }, text = { Text("Profile") })
                    Tab(selected = selectedSection == 1, onClick = { selectedSection = 1 }, text = { Text("Arc & Psychology") })
                    Tab(selected = selectedSection == 2, onClick = { selectedSection = 2 }, text = { Text("Appearance & Lore") })
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (selectedSection == 0) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Character Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("character_name_input")
                    )
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        label = { Text("Alias / Nickname") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it },
                            label = { Text("Age") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { gender = it },
                            label = { Text("Gender") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = occupation,
                        onValueChange = { occupation = it },
                        label = { Text("Role / Occupation (e.g. Inquisitor, Pilot)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = personality,
                        onValueChange = { personality = it },
                        label = { Text("Personality Traits") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Avatar Color Tag", style = MaterialTheme.typography.labelMedium)
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
                } else if (selectedSection == 1) {
                    OutlinedTextField(
                        value = goals,
                        onValueChange = { goals = it },
                        label = { Text("Core Desire / Objective (What do they want?)") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = motivation,
                        onValueChange = { motivation = it },
                        label = { Text("Motivation (Why do they want it?)") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = internalConflict,
                        onValueChange = { internalConflict = it },
                        label = { Text("Internal Conflict / Moral Dilemma") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = characterArc,
                        onValueChange = { characterArc = it },
                        label = { Text("Character Arc (How do they transform?)") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = fears,
                        onValueChange = { fears = it },
                        label = { Text("Deepest Fear / Vulnerability") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = secrets,
                        onValueChange = { secrets = it },
                        label = { Text("Secret / Hidden Past") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OutlinedTextField(
                        value = appearance,
                        onValueChange = { appearance = it },
                        label = { Text("Physical Appearance & Attire") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = strengths,
                        onValueChange = { strengths = it },
                        label = { Text("Strengths & Talents") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = weaknesses,
                        onValueChange = { weaknesses = it },
                        label = { Text("Weaknesses & Flaws") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Additional Author Notes") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val toSave = (initial ?: CharacterEntity(projectId = projectId, name = name)).copy(
                            name = name,
                            nickname = nickname,
                            age = age,
                            gender = gender,
                            occupation = occupation,
                            appearance = appearance,
                            personality = personality,
                            goals = goals,
                            motivation = motivation,
                            fears = fears,
                            strengths = strengths,
                            weaknesses = weaknesses,
                            secrets = secrets,
                            characterArc = characterArc,
                            internalConflict = internalConflict,
                            notes = notes,
                            color = selectedColor
                        )
                        onSave(toSave)
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("confirm_save_character_button")
            ) {
                Text("Save Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
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
