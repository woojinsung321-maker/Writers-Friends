package com.example.ui.characters

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CharacterEntity
import com.example.data.model.CharacterTemplateEntity
import com.example.data.model.RelationshipEntity
import com.example.data.model.SceneEntity
import com.example.ui.characters.components.AppearanceSection
import com.example.ui.characters.components.ArcSection
import com.example.ui.characters.components.CharacterTemplatesDialog
import com.example.ui.characters.components.CustomFieldsSection
import com.example.ui.characters.components.CustomNotesSection
import com.example.ui.characters.components.GoalsSection
import com.example.ui.characters.components.HistorySection
import com.example.ui.characters.components.ImageGallerySection
import com.example.ui.characters.components.InventorySection
import com.example.ui.characters.components.KnowledgeSection
import com.example.ui.characters.components.LocationsSection
import com.example.ui.characters.components.PersonalitySection
import com.example.ui.characters.components.ProfileSection
import com.example.ui.characters.components.PsychologySection
import com.example.ui.characters.components.RelationshipsSection
import com.example.ui.characters.components.ScenesSection
import com.example.ui.characters.components.SectionContainer
import com.example.ui.characters.components.VoiceSection
import com.example.ui.characters.model.CharacterAppearanceData
import com.example.ui.characters.model.CharacterArcData
import com.example.ui.characters.model.CharacterDataSerializers
import com.example.ui.characters.model.CharacterGoalItem
import com.example.ui.characters.model.CharacterHistoryData
import com.example.ui.characters.model.CharacterInventoryItem
import com.example.ui.characters.model.CharacterKnowledgeData
import com.example.ui.characters.model.CharacterLocationsData
import com.example.ui.characters.model.CharacterPersonalityData
import com.example.ui.characters.model.CharacterPsychologyData
import com.example.ui.characters.model.CharacterReferenceImage
import com.example.ui.characters.model.CharacterVoiceData
import com.example.ui.viewmodel.NovelViewModel
import com.example.util.CharacterImageStorage
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CharacterWorkspaceScreen(
    character: CharacterEntity,
    viewModel: NovelViewModel,
    onBack: () -> Unit,
    onOpenScene: (SceneEntity) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val allCharacters by viewModel.characters.collectAsState()
    val relationships by viewModel.relationships.collectAsState()
    val scenes by viewModel.scenes.collectAsState()
    val templates by viewModel.characterTemplates.collectAsState()

    // --- State values for CharacterEntity ---
    var name by remember { mutableStateOf(character.name) }
    var nickname by remember { mutableStateOf(character.nickname) }
    var aliases by remember { mutableStateOf(character.aliases) }
    var age by remember { mutableStateOf(character.age) }
    var dateOfBirth by remember { mutableStateOf(character.dateOfBirth) }
    var gender by remember { mutableStateOf(character.gender) }
    var pronouns by remember { mutableStateOf(character.pronouns) }
    var occupation by remember { mutableStateOf(character.occupation) }
    var roleInStory by remember { mutableStateOf(character.roleInStory) }
    var species by remember { mutableStateOf(character.species) }
    var nationality by remember { mutableStateOf(character.nationality) }
    var currentLocation by remember { mutableStateOf(character.currentLocation) }
    var status by remember { mutableStateOf(character.status) }
    var shortDescription by remember { mutableStateOf(character.shortDescription) }
    var biography by remember { mutableStateOf(character.biography) }
    var imageUri by remember { mutableStateOf(character.imageUri) }
    var imageDisplayMode by remember { mutableStateOf(character.imageDisplayMode) }
    var color by remember { mutableStateOf(character.color) }
    var tags by remember { mutableStateOf(character.tags) }
    var templateName by remember { mutableStateOf(character.templateName) }

    // --- Structured Complex Data States ---
    var gallery by remember {
        mutableStateOf(CharacterDataSerializers.deserializeGallery(character.galleryJson))
    }
    var appearanceData by remember {
        mutableStateOf(CharacterDataSerializers.deserializeAppearance(character.appearanceDataJson))
    }
    var personalityData by remember {
        mutableStateOf(CharacterDataSerializers.deserializePersonality(character.personalityDataJson))
    }
    var psychologyData by remember {
        mutableStateOf(CharacterDataSerializers.deserializePsychology(character.psychologyDataJson))
    }
    var historyData by remember {
        mutableStateOf(CharacterDataSerializers.deserializeHistory(character.historyDataJson))
    }
    var goalsData by remember {
        mutableStateOf(CharacterDataSerializers.deserializeGoals(character.goalsDataJson))
    }
    var arcData by remember {
        mutableStateOf(CharacterDataSerializers.deserializeArc(character.arcDataJson))
    }
    var voiceData by remember {
        mutableStateOf(CharacterDataSerializers.deserializeVoice(character.voiceDataJson))
    }
    var knowledgeData by remember {
        mutableStateOf(CharacterDataSerializers.deserializeKnowledge(character.knowledgeDataJson))
    }
    var inventoryData by remember {
        mutableStateOf(CharacterDataSerializers.deserializeInventory(character.inventoryDataJson))
    }
    var locationsData by remember {
        mutableStateOf(CharacterDataSerializers.deserializeLocations(character.locationsDataJson))
    }
    var customNotes by remember {
        mutableStateOf(character.customNotesJson.ifBlank { character.notes })
    }
    var customFieldsJson by remember {
        mutableStateOf(character.customFields)
    }

    // --- Expanded Sections Tracking ---
    val expandedSections = remember {
        mutableStateMapOf(
            "profile" to true,
            "images" to true,
            "appearance" to true,
            "personality" to true,
            "psychology" to true,
            "history" to false,
            "goals" to true,
            "arc" to false,
            "relationships" to true,
            "voice" to false,
            "knowledge" to false,
            "inventory" to false,
            "locations" to false,
            "scenes" to false,
            "notes" to false,
            "custom_fields" to false
        )
    }

    var showTemplatesDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var hasUnsavedChanges by remember { mutableStateOf(false) }

    // Auto-construct the current entity for saving
    fun buildCurrentEntity(): CharacterEntity {
        return character.copy(
            name = name.ifBlank { "Untitled Character" },
            nickname = nickname,
            aliases = aliases,
            age = age,
            dateOfBirth = dateOfBirth,
            gender = gender,
            pronouns = pronouns,
            occupation = occupation,
            roleInStory = roleInStory,
            species = species,
            nationality = nationality,
            currentLocation = currentLocation,
            status = status,
            shortDescription = shortDescription,
            biography = biography,
            color = color,
            tags = tags,
            templateName = templateName,
            imageUri = imageUri,
            imageDisplayMode = imageDisplayMode,
            galleryJson = CharacterDataSerializers.serializeGallery(gallery),
            appearanceDataJson = CharacterDataSerializers.serializeAppearance(appearanceData),
            personalityDataJson = CharacterDataSerializers.serializePersonality(personalityData),
            psychologyDataJson = CharacterDataSerializers.serializePsychology(psychologyData),
            historyDataJson = CharacterDataSerializers.serializeHistory(historyData),
            goalsDataJson = CharacterDataSerializers.serializeGoals(goalsData),
            arcDataJson = CharacterDataSerializers.serializeArc(arcData),
            voiceDataJson = CharacterDataSerializers.serializeVoice(voiceData),
            knowledgeDataJson = CharacterDataSerializers.serializeKnowledge(knowledgeData),
            inventoryDataJson = CharacterDataSerializers.serializeInventory(inventoryData),
            locationsDataJson = CharacterDataSerializers.serializeLocations(locationsData),
            customNotesJson = customNotes,
            notes = customNotes,
            customFields = customFieldsJson
        )
    }

    // Persist changes
    fun persistNow() {
        val updated = buildCurrentEntity()
        viewModel.saveCharacter(updated)
        hasUnsavedChanges = false
    }

    // Auto-save on back or disposal
    DisposableEffect(Unit) {
        onDispose {
            persistNow()
        }
    }

    // Calculate completion score non-invasively
    val completionScore by remember {
        derivedStateOf {
            var filled = 0
            val total = 20
            if (name.isNotBlank() && name != "New Character") filled++
            if (age.isNotBlank()) filled++
            if (gender.isNotBlank()) filled++
            if (occupation.isNotBlank()) filled++
            if (roleInStory.isNotBlank()) filled++
            if (biography.isNotBlank()) filled++
            if (imageUri != null) filled++
            if (appearanceData.height.isNotBlank() || appearanceData.hairColor.isNotBlank() || appearanceData.eyeColor.isNotBlank()) filled++
            if (appearanceData.distinguishingFeatures.isNotBlank()) filled++
            if (personalityData.temperament.isNotBlank() || personalityData.positiveTraits.isNotBlank()) filled++
            if (personalityData.negativeTraits.isNotBlank() || personalityData.weaknesses.isNotBlank()) filled++
            if (psychologyData.coreDesire.isNotBlank()) filled++
            if (psychologyData.primaryMotivation.isNotBlank()) filled++
            if (psychologyData.greatestFear.isNotBlank()) filled++
            if (psychologyData.internalConflict.isNotBlank()) filled++
            if (historyData.childhood.isNotBlank() || historyData.timeline.isNotEmpty()) filled++
            if (goalsData.isNotEmpty()) filled++
            if (arcData.startingPersonality.isNotBlank() || arcData.lessonsLearned.isNotBlank()) filled++
            if (voiceData.speakingStyle.isNotBlank() || voiceData.accent.isNotBlank()) filled++
            if (knowledgeData.factsKnown.isNotBlank() || knowledgeData.secretsKnown.isNotBlank()) filled++
            Pair(filled, total)
        }
    }

    val (filledCount, totalCount) = completionScore
    val completionPercent = ((filledCount.toFloat() / totalCount.toFloat()) * 100).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = name.ifBlank { "Character Profile" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = if (roleInStory.isNotBlank()) "$roleInStory • $status" else status,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        persistNow()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        persistNow()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Saved",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { showTemplatesDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Templates",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Save as Project Template") },
                            leadingIcon = { Icon(Icons.Default.Bookmark, null) },
                            onClick = {
                                showMenu = false
                                showTemplatesDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Expand All Sections") },
                            leadingIcon = { Icon(Icons.Default.Visibility, null) },
                            onClick = {
                                expandedSections.keys.forEach { expandedSections[it] = true }
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Collapse All Sections") },
                            leadingIcon = { Icon(Icons.Default.Category, null) },
                            onClick = {
                                expandedSections.keys.forEach { expandedSections[it] = false }
                                showMenu = false
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Delete Character", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                showDeleteConfirmDialog = true
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HERO SUMMARY CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar / Portrait Circle
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(Color(color).copy(alpha = 0.25f))
                                    .clickable {
                                        expandedSections["images"] = true
                                        coroutineScope.launch { listState.animateScrollToItem(2) }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageUri != null) {
                                    AsyncImage(
                                        model = CharacterImageStorage.getFileForPath(context, imageUri!!),
                                        contentDescription = name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = name.take(1).uppercase().ifBlank { "?" },
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name.ifBlank { "New Character" },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (nickname.isNotBlank()) {
                                    Text(
                                        text = "\"$nickname\"",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (occupation.isNotBlank()) {
                                    Text(
                                        text = occupation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    modifier = Modifier.padding(top = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = roleInStory.ifBlank { "Character" },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (status == "Alive") MaterialTheme.colorScheme.secondaryContainer
                                        else MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            text = status,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (status == "Alive") MaterialTheme.colorScheme.onSecondaryContainer
                                            else MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Non-invasive completion progress indicator
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$completionPercent% developed",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "$filledCount of $totalCount aspects recorded",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            LinearProgressIndicator(
                                progress = { filledCount.toFloat() / totalCount.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }

            // QUICK JUMP NAVIGATION PILLS
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val sections = listOf(
                        "profile" to "Profile",
                        "images" to "Portrait & Gallery",
                        "appearance" to "Appearance",
                        "personality" to "Personality",
                        "psychology" to "Psychology",
                        "history" to "Background",
                        "goals" to "Goals",
                        "arc" to "Arc",
                        "relationships" to "Relationships",
                        "voice" to "Voice",
                        "knowledge" to "What They Know",
                        "inventory" to "Inventory",
                        "locations" to "Locations",
                        "scenes" to "Scenes",
                        "notes" to "Notes",
                        "custom_fields" to "Custom Fields"
                    )

                    items(sections) { (key, label) ->
                        val isExpanded = expandedSections[key] == true
                        FilterChip(
                            selected = isExpanded,
                            onClick = {
                                expandedSections[key] = !(expandedSections[key] ?: false)
                            },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // SECTION 1: PROFILE
            item {
                SectionContainer(
                    title = "Profile & Identity",
                    subtitle = "Core identifiers, aliases, demographic details, and biography",
                    icon = Icons.Default.Person,
                    expanded = expandedSections["profile"] == true,
                    onToggleExpand = { expandedSections["profile"] = !(expandedSections["profile"] ?: false) }
                ) {
                    ProfileSection(
                        name = name, onNameChange = { name = it; persistNow() },
                        nickname = nickname, onNicknameChange = { nickname = it; persistNow() },
                        aliases = aliases, onAliasesChange = { aliases = it; persistNow() },
                        age = age, onAgeChange = { age = it; persistNow() },
                        dateOfBirth = dateOfBirth, onDateOfBirthChange = { dateOfBirth = it; persistNow() },
                        gender = gender, onGenderChange = { gender = it; persistNow() },
                        pronouns = pronouns, onPronounsChange = { pronouns = it; persistNow() },
                        occupation = occupation, onOccupationChange = { occupation = it; persistNow() },
                        roleInStory = roleInStory, onRoleInStoryChange = { roleInStory = it; persistNow() },
                        species = species, onSpeciesChange = { species = it; persistNow() },
                        nationality = nationality, onNationalityChange = { nationality = it; persistNow() },
                        currentLocation = currentLocation, onCurrentLocationChange = { currentLocation = it; persistNow() },
                        status = status, onStatusChange = { status = it; persistNow() },
                        shortDescription = shortDescription, onShortDescriptionChange = { shortDescription = it; persistNow() },
                        biography = biography, onBiographyChange = { biography = it; persistNow() }
                    )
                }
            }

            // SECTION 2: PORTRAIT & GALLERY
            item {
                SectionContainer(
                    title = "Portrait & Reference Photos",
                    subtitle = "Primary visual representation and aesthetic reference gallery",
                    icon = Icons.Default.PhotoLibrary,
                    expanded = expandedSections["images"] == true,
                    onToggleExpand = { expandedSections["images"] = !(expandedSections["images"] ?: false) },
                    badgeCount = gallery.size + if (imageUri != null) 1 else 0
                ) {
                    ImageGallerySection(
                        characterName = name,
                        primaryImageUri = imageUri,
                        imageDisplayMode = imageDisplayMode,
                        gallery = gallery,
                        onPrimaryImageChange = { newUri, mode ->
                            imageUri = newUri
                            imageDisplayMode = mode
                            persistNow()
                        },
                        onGalleryChange = {
                            gallery = it
                            persistNow()
                        }
                    )
                }
            }

            // SECTION 3: PHYSICAL APPEARANCE
            item {
                SectionContainer(
                    title = "Physical Appearance",
                    subtitle = "Build, coloring, facial features, clothing, style, and mannerisms",
                    icon = Icons.Default.Face,
                    expanded = expandedSections["appearance"] == true,
                    onToggleExpand = { expandedSections["appearance"] = !(expandedSections["appearance"] ?: false) }
                ) {
                    AppearanceSection(
                        data = appearanceData,
                        onDataChange = {
                            appearanceData = it
                            persistNow()
                        }
                    )
                }
            }

            // SECTION 4: PERSONALITY
            item {
                SectionContainer(
                    title = "Personality & Traits",
                    subtitle = "Temperament, virtues, flaws, habits, likes, dislikes, and values",
                    icon = Icons.Default.AutoAwesome,
                    expanded = expandedSections["personality"] == true,
                    onToggleExpand = { expandedSections["personality"] = !(expandedSections["personality"] ?: false) }
                ) {
                    PersonalitySection(
                        data = personalityData,
                        onDataChange = {
                            personalityData = it
                            persistNow()
                        }
                    )
                }
            }

            // SECTION 5: PSYCHOLOGY & INNER WORLD
            item {
                SectionContainer(
                    title = "Psychology & Inner World",
                    subtitle = "Core desires, motivations, secrets, internal conflict, and the Lie",
                    icon = Icons.Default.Psychology,
                    expanded = expandedSections["psychology"] == true,
                    onToggleExpand = { expandedSections["psychology"] = !(expandedSections["psychology"] ?: false) }
                ) {
                    PsychologySection(
                        data = psychologyData,
                        onDataChange = {
                            psychologyData = it
                            persistNow()
                        }
                    )
                }
            }

            // SECTION 6: BACKGROUND & TIMELINE
            item {
                SectionContainer(
                    title = "Background & Life Timeline",
                    subtitle = "Childhood, formative memories, turning points, and chronological events",
                    icon = Icons.Default.History,
                    expanded = expandedSections["history"] == true,
                    onToggleExpand = { expandedSections["history"] = !(expandedSections["history"] ?: false) },
                    badgeCount = historyData.timeline.size
                ) {
                    HistorySection(
                        data = historyData,
                        onDataChange = {
                            historyData = it
                            persistNow()
                        }
                    )
                }
            }

            // SECTION 7: GOALS & MOTIVATIONS
            item {
                SectionContainer(
                    title = "Character Goals",
                    subtitle = "Short-term, long-term, internal, and secret objectives",
                    icon = Icons.Default.CheckCircle,
                    expanded = expandedSections["goals"] == true,
                    onToggleExpand = { expandedSections["goals"] = !(expandedSections["goals"] ?: false) },
                    badgeCount = goalsData.size
                ) {
                    GoalsSection(
                        goals = goalsData,
                        onGoalsChange = {
                            goalsData = it
                            persistNow()
                        }
                    )
                }
            }

            // SECTION 8: CHARACTER ARC
            item {
                SectionContainer(
                    title = "Character Arc & Transformation",
                    subtitle = "Beginning state, trials, crucibles, and resolution evolution",
                    icon = Icons.Default.Timeline,
                    expanded = expandedSections["arc"] == true,
                    onToggleExpand = { expandedSections["arc"] = !(expandedSections["arc"] ?: false) }
                ) {
                    ArcSection(
                        data = arcData,
                        onDataChange = {
                            arcData = it
                            persistNow()
                        }
                    )
                }
            }

            // SECTION 9: RELATIONSHIPS
            item {
                val relCount = relationships.count { it.sourceId == character.id || it.targetId == character.id }
                SectionContainer(
                    title = "Relationships & Dynamics",
                    subtitle = "Bonds, alliances, rivalries, family, and conflict with other cast members",
                    icon = Icons.Default.Person,
                    expanded = expandedSections["relationships"] == true,
                    onToggleExpand = { expandedSections["relationships"] = !(expandedSections["relationships"] ?: false) },
                    badgeCount = relCount
                ) {
                    RelationshipsSection(
                        character = character,
                        allCharacters = allCharacters,
                        relationships = relationships,
                        onSaveRelationship = { viewModel.saveRelationship(it) },
                        onDeleteRelationship = { viewModel.deleteRelationship(it) }
                    )
                }
            }

            // SECTION 10: VOICE & DIALOGUE
            item {
                SectionContainer(
                    title = "Voice & Speech Patterns",
                    subtitle = "Formality, vocabulary, cadence, idioms, and emotional dialogue shifts",
                    icon = Icons.Default.RecordVoiceOver,
                    expanded = expandedSections["voice"] == true,
                    onToggleExpand = { expandedSections["voice"] = !(expandedSections["voice"] ?: false) }
                ) {
                    VoiceSection(
                        data = voiceData,
                        onDataChange = {
                            voiceData = it
                            persistNow()
                        }
                    )
                }
            }

            // SECTION 11: WHAT THEY KNOW
            item {
                SectionContainer(
                    title = "What They Know (Knowledge & Secrets)",
                    subtitle = "Facts known, secrets guarded, and secrets NOT known (dramatic irony)",
                    icon = Icons.Default.Notes,
                    expanded = expandedSections["knowledge"] == true,
                    onToggleExpand = { expandedSections["knowledge"] = !(expandedSections["knowledge"] ?: false) }
                ) {
                    KnowledgeSection(
                        data = knowledgeData,
                        onDataChange = {
                            knowledgeData = it
                            persistNow()
                        }
                    )
                }
            }

            // SECTION 12: INVENTORY & POSSESSIONS
            item {
                SectionContainer(
                    title = "Inventory & Possessions",
                    subtitle = "Signature weapons, heirlooms, tools, garments, and story props",
                    icon = Icons.Default.Inventory2,
                    expanded = expandedSections["inventory"] == true,
                    onToggleExpand = { expandedSections["inventory"] = !(expandedSections["inventory"] ?: false) },
                    badgeCount = inventoryData.size
                ) {
                    InventorySection(
                        inventory = inventoryData,
                        onInventoryChange = {
                            inventoryData = it
                            persistNow()
                        }
                    )
                }
            }

            // SECTION 13: LOCATIONS
            item {
                SectionContainer(
                    title = "Locations & Geography",
                    subtitle = "Current residence, previous dwellings, and places of emotional resonance",
                    icon = Icons.Default.MyLocation,
                    expanded = expandedSections["locations"] == true,
                    onToggleExpand = { expandedSections["locations"] = !(expandedSections["locations"] ?: false) }
                ) {
                    LocationsSection(
                        data = locationsData,
                        onDataChange = {
                            locationsData = it
                            persistNow()
                        }
                    )
                }
            }

            // SECTION 14: SCENES
            item {
                val sceneCount = scenes.count {
                    it.povCharacterName?.equals(name, ignoreCase = true) == true ||
                            it.title.contains(name, ignoreCase = true) ||
                            it.body.contains(name, ignoreCase = true)
                }
                SectionContainer(
                    title = "Story Scenes & Appearances",
                    subtitle = "Scenes featuring this character as POV lead or mentioned in manuscript",
                    icon = Icons.Default.Movie,
                    expanded = expandedSections["scenes"] == true,
                    onToggleExpand = { expandedSections["scenes"] = !(expandedSections["scenes"] ?: false) },
                    badgeCount = sceneCount
                ) {
                    ScenesSection(
                        characterName = name,
                        scenes = scenes,
                        onOpenScene = onOpenScene
                    )
                }
            }

            // SECTION 15: CUSTOM NOTES (UNRESTRICTED WRITING)
            item {
                SectionContainer(
                    title = "Unrestricted Writing Notes",
                    subtitle = "Rich freeform notes with Markdown headings, lists, and bold cues",
                    icon = Icons.Default.EditNote,
                    expanded = expandedSections["notes"] == true,
                    onToggleExpand = { expandedSections["notes"] = !(expandedSections["notes"] ?: false) }
                ) {
                    CustomNotesSection(
                        notes = customNotes,
                        onNotesChange = {
                            customNotes = it
                            persistNow()
                        }
                    )
                }
            }

            // SECTION 16: CUSTOM FIELDS
            item {
                SectionContainer(
                    title = "Custom Fields & Lore",
                    subtitle = "Unlimited writer-defined key-value attributes for your story",
                    icon = Icons.Default.Category,
                    expanded = expandedSections["custom_fields"] == true,
                    onToggleExpand = { expandedSections["custom_fields"] = !(expandedSections["custom_fields"] ?: false) }
                ) {
                    CustomFieldsSection(
                        customFieldsJson = customFieldsJson,
                        onCustomFieldsJsonChange = {
                            customFieldsJson = it
                            persistNow()
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    // --- Template Application Dialog ---
    if (showTemplatesDialog) {
        CharacterTemplatesDialog(
            projectTemplates = templates,
            onDismiss = { showTemplatesDialog = false },
            onApplyTemplate = { tmplTitle, tmplJson ->
                try {
                    val obj = JSONObject(tmplJson)
                    if (obj.has("roleInStory")) roleInStory = obj.getString("roleInStory")
                    if (obj.has("species")) species = obj.getString("species")
                    if (obj.has("occupation")) occupation = obj.getString("occupation")
                    if (obj.has("coreDesire")) psychologyData = psychologyData.copy(coreDesire = obj.getString("coreDesire"))
                    if (obj.has("primaryMotivation")) psychologyData = psychologyData.copy(primaryMotivation = obj.getString("primaryMotivation"))
                    if (obj.has("greatestFear")) psychologyData = psychologyData.copy(greatestFear = obj.getString("greatestFear"))
                    if (obj.has("temperament")) personalityData = personalityData.copy(temperament = obj.getString("temperament"))
                    if (obj.has("values")) personalityData = personalityData.copy(values = obj.getString("values"))
                    if (obj.has("speakingStyle")) voiceData = voiceData.copy(speakingStyle = obj.getString("speakingStyle"))
                    if (obj.has("emotionalWounds")) psychologyData = psychologyData.copy(emotionalWounds = obj.getString("emotionalWounds"))
                    if (obj.has("internalConflict")) psychologyData = psychologyData.copy(internalConflict = obj.getString("internalConflict"))
                    if (obj.has("misconceptions")) psychologyData = psychologyData.copy(misconceptions = obj.getString("misconceptions"))
                    if (obj.has("secrets")) psychologyData = psychologyData.copy(secrets = obj.getString("secrets"))
                    if (obj.has("breakingPoint")) psychologyData = psychologyData.copy(breakingPoint = obj.getString("breakingPoint"))
                    if (obj.has("wantOthersToThink")) psychologyData = psychologyData.copy(wantOthersToThink = obj.getString("wantOthersToThink"))
                    if (obj.has("actuallyThinkOfSelf")) psychologyData = psychologyData.copy(actuallyThinkOfSelf = obj.getString("actuallyThinkOfSelf"))
                    templateName = tmplTitle
                    persistNow()
                } catch (e: Exception) {
                    // Ignore parsing error
                }
                showTemplatesDialog = false
            },
            onSaveAsTemplate = { tName, tDesc ->
                val templateContent = JSONObject().apply {
                    put("roleInStory", roleInStory)
                    put("species", species)
                    put("coreDesire", psychologyData.coreDesire)
                    put("primaryMotivation", psychologyData.primaryMotivation)
                    put("greatestFear", psychologyData.greatestFear)
                    put("temperament", personalityData.temperament)
                    put("values", personalityData.values)
                    put("speakingStyle", voiceData.speakingStyle)
                }.toString()

                val newTmpl = CharacterTemplateEntity(
                    id = UUID.randomUUID().toString(),
                    projectId = character.projectId,
                    name = tName,
                    description = tDesc,
                    templateJson = templateContent
                )
                viewModel.saveCharacterTemplate(newTmpl)
            }
        )
    }

    // --- Delete Confirmation Dialog ---
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Character?") },
            text = {
                Text("Are you sure you want to permanently delete \"$name\"? All character profile information, notes, and goals will be removed.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCharacter(character)
                        showDeleteConfirmDialog = false
                        onBack()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
