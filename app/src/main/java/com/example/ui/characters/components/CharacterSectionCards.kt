package com.example.ui.characters.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.characters.model.CharacterAppearanceData
import com.example.ui.characters.model.CharacterArcData
import com.example.ui.characters.model.CharacterHistoryData
import com.example.ui.characters.model.CharacterKnowledgeData
import com.example.ui.characters.model.CharacterLocationsData
import com.example.ui.characters.model.CharacterPersonalityData
import com.example.ui.characters.model.CharacterPsychologyData
import com.example.ui.characters.model.CharacterTimelineEvent
import com.example.ui.characters.model.CharacterVoiceData

@Composable
fun SectionContainer(
    title: String,
    subtitle: String,
    icon: ImageVector,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    badgeCount: Int? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
            )
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (badgeCount != null && badgeCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "$badgeCount",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                    )
                    content()
                }
            }
        }
    }
}

// ----------------------------------------------------
// SECTION 1: PROFILE SECTION
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSection(
    name: String, onNameChange: (String) -> Unit,
    nickname: String, onNicknameChange: (String) -> Unit,
    aliases: String, onAliasesChange: (String) -> Unit,
    age: String, onAgeChange: (String) -> Unit,
    dateOfBirth: String, onDateOfBirthChange: (String) -> Unit,
    gender: String, onGenderChange: (String) -> Unit,
    pronouns: String, onPronounsChange: (String) -> Unit,
    occupation: String, onOccupationChange: (String) -> Unit,
    roleInStory: String, onRoleInStoryChange: (String) -> Unit,
    species: String, onSpeciesChange: (String) -> Unit,
    nationality: String, onNationalityChange: (String) -> Unit,
    currentLocation: String, onCurrentLocationChange: (String) -> Unit,
    status: String, onStatusChange: (String) -> Unit,
    shortDescription: String, onShortDescriptionChange: (String) -> Unit,
    biography: String, onBiographyChange: (String) -> Unit
) {
    val roles = listOf("Protagonist", "Antagonist", "Deuteragonist", "Mentor", "Foil", "Love Interest", "Sidekick", "Comic Relief", "Supporting", "POV Character")
    val statuses = listOf("Alive", "Deceased", "Missing", "Unknown", "Inactive", "Transformed")
    val pronounOptions = listOf("he/him", "she/her", "they/them", "it/its", "Custom")

    var roleExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Character Name *") },
            placeholder = { Text("e.g. Captain Eleanor Vance") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = nickname,
                onValueChange = onNicknameChange,
                label = { Text("Nickname") },
                placeholder = { Text("e.g. Nell") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = aliases,
                onValueChange = onAliasesChange,
                label = { Text("Aliases / Titles") },
                placeholder = { Text("e.g. The Silver Raven") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = age,
                onValueChange = onAgeChange,
                label = { Text("Age") },
                placeholder = { Text("e.g. 34 (looks 28)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = onDateOfBirthChange,
                label = { Text("Date of Birth") },
                placeholder = { Text("e.g. 14th of Frostfall, 842") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = gender,
                onValueChange = onGenderChange,
                label = { Text("Gender") },
                placeholder = { Text("e.g. Female") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = pronouns,
                onValueChange = onPronounsChange,
                label = { Text("Pronouns") },
                placeholder = { Text("e.g. she/her") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Role dropdown
            ExposedDropdownMenuBox(
                expanded = roleExpanded,
                onExpandedChange = { roleExpanded = !roleExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = roleInStory,
                    onValueChange = onRoleInStoryChange,
                    label = { Text("Role in Story") },
                    placeholder = { Text("Select or type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = roleExpanded,
                    onDismissRequest = { roleExpanded = false }
                ) {
                    roles.forEach { r ->
                        DropdownMenuItem(
                            text = { Text(r) },
                            onClick = {
                                onRoleInStoryChange(r)
                                roleExpanded = false
                            }
                        )
                    }
                }
            }

            // Status dropdown
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = status,
                    onValueChange = onStatusChange,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    statuses.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s) },
                            onClick = {
                                onStatusChange(s)
                                statusExpanded = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = occupation,
            onValueChange = onOccupationChange,
            label = { Text("Occupation / Rank / Social Position") },
            placeholder = { Text("e.g. Commander of the High Seas Fleet, Former Blacksmith") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = species,
                onValueChange = onSpeciesChange,
                label = { Text("Species / Race") },
                placeholder = { Text("e.g. Human, Half-Elf") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = nationality,
                onValueChange = onNationalityChange,
                label = { Text("Nationality / Culture / Realm") },
                placeholder = { Text("e.g. Valerian Empire") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = currentLocation,
            onValueChange = onCurrentLocationChange,
            label = { Text("Current Location (Story Beginning)") },
            placeholder = { Text("e.g. The Rusty Anchor Tavern, Harbor District") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = shortDescription,
            onValueChange = onShortDescriptionChange,
            label = { Text("Short Description / Logline Hook") },
            placeholder = { Text("1-2 sentences summarizing who they are and their core contradiction") },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = biography,
            onValueChange = onBiographyChange,
            label = { Text("Full Biography / Background Narrative") },
            placeholder = { Text("Write the character's life story leading up to the start of the book...") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ----------------------------------------------------
// SECTION 3: PHYSICAL APPEARANCE
// ----------------------------------------------------
@Composable
fun AppearanceSection(
    data: CharacterAppearanceData,
    onDataChange: (CharacterAppearanceData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.height,
                onValueChange = { onDataChange(data.copy(height = it)) },
                label = { Text("Height") },
                placeholder = { Text("e.g. 5'11\" (180 cm)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.weight,
                onValueChange = { onDataChange(data.copy(weight = it)) },
                label = { Text("Weight / Frame") },
                placeholder = { Text("e.g. 175 lbs") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.build,
                onValueChange = { onDataChange(data.copy(build = it)) },
                label = { Text("Build / Body Type") },
                placeholder = { Text("e.g. Athletic, lean, wiry") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.skinTone,
                onValueChange = { onDataChange(data.copy(skinTone = it)) },
                label = { Text("Skin Tone / Complexion") },
                placeholder = { Text("e.g. Sun-weathered bronze") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.hairColor,
                onValueChange = { onDataChange(data.copy(hairColor = it)) },
                label = { Text("Hair Color") },
                placeholder = { Text("e.g. Raven black") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.hairStyle,
                onValueChange = { onDataChange(data.copy(hairStyle = it)) },
                label = { Text("Hair Style & Length") },
                placeholder = { Text("e.g. Shoulder-length braid") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.eyeColor,
                onValueChange = { onDataChange(data.copy(eyeColor = it)) },
                label = { Text("Eye Color & Gaze") },
                placeholder = { Text("e.g. Piercing hazel") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.faceShape,
                onValueChange = { onDataChange(data.copy(faceShape = it)) },
                label = { Text("Face Shape & Jaw") },
                placeholder = { Text("e.g. High cheekbones, angular") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = data.distinguishingFeatures,
            onValueChange = { onDataChange(data.copy(distinguishingFeatures = it)) },
            label = { Text("Distinguishing Features") },
            placeholder = { Text("e.g. Scar over left eyebrow, silver ring in right ear, serpentine tattoo on forearm") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.clothingStyle,
                onValueChange = { onDataChange(data.copy(clothingStyle = it)) },
                label = { Text("Clothing Style & Colors") },
                placeholder = { Text("e.g. Heavy dark wool duster, leather boots") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.accessories,
                onValueChange = { onDataChange(data.copy(accessories = it)) },
                label = { Text("Typical Accessories & Jewelry") },
                placeholder = { Text("e.g. Father's cracked pocket watch") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.typicalExpression,
                onValueChange = { onDataChange(data.copy(typicalExpression = it)) },
                label = { Text("Typical Facial Expression") },
                placeholder = { Text("e.g. Skeptical squint, guarded smirk") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.bodyLanguage,
                onValueChange = { onDataChange(data.copy(bodyLanguage = it)) },
                label = { Text("Body Language & Posture") },
                placeholder = { Text("e.g. Always sits facing the door, rigid posture") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = data.mannerisms,
            onValueChange = { onDataChange(data.copy(mannerisms = it)) },
            label = { Text("Mannerisms, Tics & Unconscious Habits") },
            placeholder = { Text("e.g. Plays with coin when calculating risks; clears throat before lying") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = data.detailedNotes,
            onValueChange = { onDataChange(data.copy(detailedNotes = it)) },
            label = { Text("Detailed Appearance & Sensory Notes") },
            placeholder = { Text("Describe how they smell (smoke, sea salt), how they walk, how they command a room...") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ----------------------------------------------------
// SECTION 4: PERSONALITY
// ----------------------------------------------------
@Composable
fun PersonalitySection(
    data: CharacterPersonalityData,
    onDataChange: (CharacterPersonalityData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = data.temperament,
            onValueChange = { onDataChange(data.copy(temperament = it)) },
            label = { Text("Temperament / Core Disposition") },
            placeholder = { Text("e.g. Introverted, choleric, melancholic with sudden bouts of dry wit") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.positiveTraits,
                onValueChange = { onDataChange(data.copy(positiveTraits = it)) },
                label = { Text("Positive Traits (Virtues)") },
                placeholder = { Text("e.g. Fiercely loyal, resourceful, perceptive") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.negativeTraits,
                onValueChange = { onDataChange(data.copy(negativeTraits = it)) },
                label = { Text("Negative Traits (Flaws)") },
                placeholder = { Text("e.g. Cynical, stubborn, vengeful, paranoid") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.strengths,
                onValueChange = { onDataChange(data.copy(strengths = it)) },
                label = { Text("Key Strengths") },
                placeholder = { Text("e.g. Master tactician, high pain tolerance") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.weaknesses,
                onValueChange = { onDataChange(data.copy(weaknesses = it)) },
                label = { Text("Key Weaknesses / Vulnerabilities") },
                placeholder = { Text("e.g. Cannot refuse a cry for help; poor swimmer") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.likes,
                onValueChange = { onDataChange(data.copy(likes = it)) },
                label = { Text("Likes & Passions") },
                placeholder = { Text("e.g. Rainstorms, bitter tea, rare maps") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.dislikes,
                onValueChange = { onDataChange(data.copy(dislikes = it)) },
                label = { Text("Dislikes & Pet Peeves") },
                placeholder = { Text("e.g. Pomposity, wasted food, narrow spaces") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.hobbies,
                onValueChange = { onDataChange(data.copy(hobbies = it)) },
                label = { Text("Hobbies & Pastimes") },
                placeholder = { Text("e.g. Whittling figurines, stargazing, chess") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.fears,
                onValueChange = { onDataChange(data.copy(fears = it)) },
                label = { Text("Fears & Insecurities") },
                placeholder = { Text("e.g. Being forgotten; betrayal by loved ones") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = data.values,
            onValueChange = { onDataChange(data.copy(values = it)) },
            label = { Text("Values, Moral Code & Lines They Won't Cross") },
            placeholder = { Text("e.g. Will never harm an innocent child; believes debts must always be repaid") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.senseOfHumor,
                onValueChange = { onDataChange(data.copy(senseOfHumor = it)) },
                label = { Text("Sense of Humor") },
                placeholder = { Text("e.g. Sarcastic, dark gallows humor") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.communicationStyle,
                onValueChange = { onDataChange(data.copy(communicationStyle = it)) },
                label = { Text("Social & Communication Style") },
                placeholder = { Text("e.g. Blunt, concise, speaks only when necessary") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = data.notes,
            onValueChange = { onDataChange(data.copy(notes = it)) },
            label = { Text("Personality Freeform Notes") },
            placeholder = { Text("Additional notes on psychology, quirks, or contradictions in their personality...") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ----------------------------------------------------
// SECTION 5: PSYCHOLOGY & INNER WORLD
// ----------------------------------------------------
@Composable
fun PsychologySection(
    data: CharacterPsychologyData,
    onDataChange: (CharacterPsychologyData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = data.coreDesire,
            onValueChange = { onDataChange(data.copy(coreDesire = it)) },
            label = { Text("Core Yearning / Deepest Desire (What do they want most?)") },
            placeholder = { Text("e.g. To earn back their family's honor; to be truly understood and safe") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.primaryMotivation,
                onValueChange = { onDataChange(data.copy(primaryMotivation = it)) },
                label = { Text("Primary Motivation") },
                placeholder = { Text("e.g. Avenge fallen mentor") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.secondaryMotivations,
                onValueChange = { onDataChange(data.copy(secondaryMotivations = it)) },
                label = { Text("Secondary Motivations") },
                placeholder = { Text("e.g. Protect younger sister, amass wealth") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.greatestFear,
                onValueChange = { onDataChange(data.copy(greatestFear = it)) },
                label = { Text("Greatest Fear") },
                placeholder = { Text("e.g. Becoming the monster they hunt") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.greatestRegret,
                onValueChange = { onDataChange(data.copy(greatestRegret = it)) },
                label = { Text("Greatest Regret") },
                placeholder = { Text("e.g. Not speaking the truth at the trial") },
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = data.emotionalWounds,
            onValueChange = { onDataChange(data.copy(emotionalWounds = it)) },
            label = { Text("The Ghost / Emotional Wound (Origin of their trauma)") },
            placeholder = { Text("The foundational event in their past that broke their worldview and made them who they are") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.internalConflict,
                onValueChange = { onDataChange(data.copy(internalConflict = it)) },
                label = { Text("Internal Conflict (Heart at war with itself)") },
                placeholder = { Text("e.g. Duty to the crown vs. love for a rebel") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.externalConflict,
                onValueChange = { onDataChange(data.copy(externalConflict = it)) },
                label = { Text("External Conflict (Opposition from the world)") },
                placeholder = { Text("e.g. Outnumbered by the inquisitor's forces") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.misconceptions,
                onValueChange = { onDataChange(data.copy(misconceptions = it)) },
                label = { Text("The Lie They Believe (False Assumption)") },
                placeholder = { Text("e.g. \"I must walk alone because anyone close to me dies\"") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.beliefs,
                onValueChange = { onDataChange(data.copy(beliefs = it)) },
                label = { Text("The Truth They Must Learn (Character Arc Key)") },
                placeholder = { Text("e.g. \"Vulnerability is strength, not weakness\"") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.secrets,
                onValueChange = { onDataChange(data.copy(secrets = it)) },
                label = { Text("Deepest Secret (Guilt & Shame)") },
                placeholder = { Text("e.g. Caused the fire that destroyed the academy") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.breakingPoint,
                onValueChange = { onDataChange(data.copy(breakingPoint = it)) },
                label = { Text("Breaking Point (What causes them to snap?)") },
                placeholder = { Text("e.g. Seeing a friend subjected to torture") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.wantOthersToThink,
                onValueChange = { onDataChange(data.copy(wantOthersToThink = it)) },
                label = { Text("Public Mask (What they want others to see)") },
                placeholder = { Text("e.g. An unshakeable, emotionless mercenary") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.actuallyThinkOfSelf,
                onValueChange = { onDataChange(data.copy(actuallyThinkOfSelf = it)) },
                label = { Text("Private Reality (What they think of themselves)") },
                placeholder = { Text("e.g. A frightened fraud waiting to be unmasked") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = data.refuseToAdmit,
            onValueChange = { onDataChange(data.copy(refuseToAdmit = it)) },
            label = { Text("What they refuse to admit even to themselves") },
            placeholder = { Text("e.g. That they actually enjoyed holding power over their rivals") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = data.notes,
            onValueChange = { onDataChange(data.copy(notes = it)) },
            label = { Text("Psychology & Subconscious Notes") },
            placeholder = { Text("Symbolic motifs, psychological complexes, recurring nightmares...") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ----------------------------------------------------
// SECTION 6: BACKGROUND & TIMELINE
// ----------------------------------------------------
@Composable
fun HistorySection(
    data: CharacterHistoryData,
    onDataChange: (CharacterHistoryData) -> Unit
) {
    var showAddEventDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.childhood,
                onValueChange = { onDataChange(data.copy(childhood = it)) },
                label = { Text("Childhood & Upbringing") },
                placeholder = { Text("e.g. Raised in the mining colonies under harsh conditions") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.family,
                onValueChange = { onDataChange(data.copy(family = it)) },
                label = { Text("Family & Lineage") },
                placeholder = { Text("e.g. Eldest of four siblings; father was an exiled scholar") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.education,
                onValueChange = { onDataChange(data.copy(education = it)) },
                label = { Text("Education, Training & Mentors") },
                placeholder = { Text("e.g. Self-taught reader, apprenticed to Master Kael") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.previousOccupations,
                onValueChange = { onDataChange(data.copy(previousOccupations = it)) },
                label = { Text("Previous Occupations & Roles") },
                placeholder = { Text("e.g. City watchman, dock loader, courier") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.majorAchievements,
                onValueChange = { onDataChange(data.copy(majorAchievements = it)) },
                label = { Text("Major Life Achievements") },
                placeholder = { Text("e.g. Saved the flagship during the Great Tempest") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.majorFailures,
                onValueChange = { onDataChange(data.copy(majorFailures = it)) },
                label = { Text("Major Life Failures & Losses") },
                placeholder = { Text("e.g. Lost the southern garrison to the siege") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = data.turningPoints,
            onValueChange = { onDataChange(data.copy(turningPoints = it)) },
            label = { Text("Formative Turning Points & Critical Memories") },
            placeholder = { Text("Key milestones that transformed their path prior to page one") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        // CHRONOLOGICAL TIMELINE
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Chronological Life Timeline",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${data.timeline.size} events recorded",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedButton(
                onClick = { showAddEventDialog = true },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Life Event", style = MaterialTheme.typography.labelMedium)
            }
        }

        if (data.timeline.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "No life events plotted yet. Add events (e.g. \"Age 8: Parents exiled\", \"Age 19: Joined the Royal Guard\") to establish a clear character history.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                data.timeline.forEachIndexed { index, event ->
                    TimelineEventCard(
                        event = event,
                        onDelete = {
                            val updated = data.timeline.toMutableList().apply { removeAt(index) }
                            onDataChange(data.copy(timeline = updated))
                        }
                    )
                }
            }
        }
    }

    if (showAddEventDialog) {
        AddTimelineEventDialog(
            onDismiss = { showAddEventDialog = false },
            onAdd = { newEvent ->
                onDataChange(data.copy(timeline = data.timeline + newEvent))
                showAddEventDialog = false
            }
        )
    }
}

@Composable
fun TimelineEventCard(
    event: CharacterTimelineEvent,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = event.ageOrYear.ifBlank { "Event" },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                if (event.description.isNotBlank()) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Event",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AddTimelineEventDialog(
    onDismiss: () -> Unit,
    onAdd: (CharacterTimelineEvent) -> Unit
) {
    var ageOrYear by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var significance by remember { mutableStateOf("Major") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Timeline Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = ageOrYear,
                    onValueChange = { ageOrYear = it },
                    label = { Text("Age or Year") },
                    placeholder = { Text("e.g. Age 12 or Year 842") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Title *") },
                    placeholder = { Text("e.g. Discovered the Cursed Amulet") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Significance") },
                    placeholder = { Text("What happened and how it shaped the character...") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(
                            CharacterTimelineEvent(
                                ageOrYear = ageOrYear.trim(),
                                title = title.trim(),
                                description = description.trim(),
                                significance = significance
                            )
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Add Event")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ----------------------------------------------------
// SECTION 7: CHARACTER ARC
// ----------------------------------------------------
@Composable
fun ArcSection(
    data: CharacterArcData,
    onDataChange: (CharacterArcData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Visual Roadmap Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Character Transformation Engine",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Track the character's internal metamorphosis from who they are at the beginning to who they become by the story's end.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Beginning State
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. Beginning State (The Ordinary World)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                OutlinedTextField(
                    value = data.startingPersonality,
                    onValueChange = { onDataChange(data.copy(startingPersonality = it)) },
                    label = { Text("Starting Personality & Attitude") },
                    placeholder = { Text("e.g. Bitter, reclusive, refuses attachment") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = data.startingBeliefs,
                    onValueChange = { onDataChange(data.copy(startingBeliefs = it)) },
                    label = { Text("Starting Worldview & Flaws") },
                    placeholder = { Text("e.g. Believes everyone is inherently corrupt and selfish") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = data.initialGoals,
                    onValueChange = { onDataChange(data.copy(initialGoals = it)) },
                    label = { Text("Initial Goals (What they think they want)") },
                    placeholder = { Text("e.g. Save enough coin to buy passage far away and hide") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // The Crucible / Turning Points
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "2. The Crucible & Turning Points (The Transformation)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                OutlinedTextField(
                    value = data.majorTurningPoints,
                    onValueChange = { onDataChange(data.copy(majorTurningPoints = it)) },
                    label = { Text("Major Turning Points / Catalysts") },
                    placeholder = { Text("Key moments that force them to reconsider their life and methods") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = data.characterChallenges,
                    onValueChange = { onDataChange(data.copy(characterChallenges = it)) },
                    label = { Text("Crucial Challenges & The Dark Night of the Soul") },
                    placeholder = { Text("When their old worldview completely fails and they face destruction") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = data.lessonsLearned,
                    onValueChange = { onDataChange(data.copy(lessonsLearned = it)) },
                    label = { Text("Lessons Learned & Hard Truths Embraced") },
                    placeholder = { Text("What epiphany allows them to transcend their flaw?") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Resolution / End State
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "3. End State (The New Reality)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                OutlinedTextField(
                    value = data.finalBeliefs,
                    onValueChange = { onDataChange(data.copy(finalBeliefs = it)) },
                    label = { Text("Final Beliefs & True Worldview") },
                    placeholder = { Text("e.g. Understands that community and loyalty are worth the risk of betrayal") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = data.finalPersonality,
                    onValueChange = { onDataChange(data.copy(finalPersonality = it)) },
                    label = { Text("Final Personality & Maturity") },
                    placeholder = { Text("e.g. Grounded, humble, generous leader") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = data.endState,
                    onValueChange = { onDataChange(data.copy(endState = it)) },
                    label = { Text("Ultimate Fate / End Position in Story") },
                    placeholder = { Text("e.g. Becomes Chancellor of the rebuilding republic") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ----------------------------------------------------
// SECTION 8: CHARACTER VOICE & SPEECH
// ----------------------------------------------------
@Composable
fun VoiceSection(
    data: CharacterVoiceData,
    onDataChange: (CharacterVoiceData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.speakingStyle,
                onValueChange = { onDataChange(data.copy(speakingStyle = it)) },
                label = { Text("Speaking Style / Cadence") },
                placeholder = { Text("e.g. Clipped, poetic, deliberate, rapid") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.formality,
                onValueChange = { onDataChange(data.copy(formality = it)) },
                label = { Text("Formality Level") },
                placeholder = { Text("e.g. Highly formal, street slang, archaic") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.vocabulary,
                onValueChange = { onDataChange(data.copy(vocabulary = it)) },
                label = { Text("Vocabulary Level") },
                placeholder = { Text("e.g. Academic, sailor slang, colloquial") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.accent,
                onValueChange = { onDataChange(data.copy(accent = it)) },
                label = { Text("Accent & Dialect") },
                placeholder = { Text("e.g. Northern highlands lilt, gravelly baritone") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = data.favoriteExpressions,
            onValueChange = { onDataChange(data.copy(favoriteExpressions = it)) },
            label = { Text("Favorite Expressions, Idioms & Catchphrases") },
            placeholder = { Text("e.g. \"By the salt in the wound...\", \"Let's weigh the anchors\"") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.wordsFrequentlyUsed,
                onValueChange = { onDataChange(data.copy(wordsFrequentlyUsed = it)) },
                label = { Text("Words Frequently Used") },
                placeholder = { Text("e.g. precise, reckoning, nonsense") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.wordsNeverUsed,
                onValueChange = { onDataChange(data.copy(wordsNeverUsed = it)) },
                label = { Text("Words They Never Say") },
                placeholder = { Text("e.g. sorry, fear, impossible") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = data.emotionalSpeakingPatterns,
            onValueChange = { onDataChange(data.copy(emotionalSpeakingPatterns = it)) },
            label = { Text("Emotional Speech Shifts (Angry, Nervous, Lying)") },
            placeholder = { Text("e.g. When angry, voice becomes dangerously quiet; when lying, over-explains details") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = data.notes,
            onValueChange = { onDataChange(data.copy(notes = it)) },
            label = { Text("Dialogue Sample & Voice Notes") },
            placeholder = { Text("Write sample lines of dialogue to nail down their authentic voice...") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ----------------------------------------------------
// SECTION 9: WHAT THEY KNOW (KNOWLEDGE & SECRETS)
// ----------------------------------------------------
@Composable
fun KnowledgeSection(
    data: CharacterKnowledgeData,
    onDataChange: (CharacterKnowledgeData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = data.factsKnown,
            onValueChange = { onDataChange(data.copy(factsKnown = it)) },
            label = { Text("Key Facts They Know") },
            placeholder = { Text("e.g. Knows the true location of the rebel hideout; knows the Duke is poisoned") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = data.secretsKnown,
            onValueChange = { onDataChange(data.copy(secretsKnown = it)) },
            label = { Text("Secrets They Guard") },
            placeholder = { Text("e.g. Knows their true parentage; knows where the cipher key is buried") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = data.secretsNotKnown,
            onValueChange = { onDataChange(data.copy(secretsNotKnown = it)) },
            label = { Text("Secrets They DO NOT Know (Crucial for Dramatic Irony)") },
            placeholder = { Text("e.g. Does not know their lieutenant is an imperial spy; does not know their brother is alive") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = data.misconceptions,
                onValueChange = { onDataChange(data.copy(misconceptions = it)) },
                label = { Text("False Assumptions & Misconceptions") },
                placeholder = { Text("e.g. Thinks the Guild abandoned them") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = data.suspicions,
                onValueChange = { onDataChange(data.copy(suspicions = it)) },
                label = { Text("Suspicions & Working Theories") },
                placeholder = { Text("e.g. Suspects the merchant is laundering coins") },
                minLines = 2,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = data.eventsWitnessed,
            onValueChange = { onDataChange(data.copy(eventsWitnessed = it)) },
            label = { Text("Historical Events Witnessed Firsthand") },
            placeholder = { Text("e.g. Witnessed the Fall of the Citadel; was present when the treaty was signed") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ----------------------------------------------------
// SECTION 10: LOCATIONS
// ----------------------------------------------------
@Composable
fun LocationsSection(
    data: CharacterLocationsData,
    onDataChange: (CharacterLocationsData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = data.currentLocation,
            onValueChange = { onDataChange(data.copy(currentLocation = it)) },
            label = { Text("Current Location") },
            placeholder = { Text("e.g. Royal Archives, High District") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = data.previousLocations,
            onValueChange = { onDataChange(data.copy(previousLocations = it)) },
            label = { Text("Previous Locations / Places Lived") },
            placeholder = { Text("e.g. The Northern Border, Outpost 9, Eastern Port") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = data.importantLocations,
            onValueChange = { onDataChange(data.copy(importantLocations = it)) },
            label = { Text("Locations of Emotional Significance") },
            placeholder = { Text("e.g. Their childhood cottage, the battlefield where they lost their platoon") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = data.associatedScenes,
            onValueChange = { onDataChange(data.copy(associatedScenes = it)) },
            label = { Text("Associated Scene Settings") },
            placeholder = { Text("e.g. Act I Chapter 3 tavern, Act II Chapter 8 mountain pass") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ----------------------------------------------------
// SECTION 11: CUSTOM UNRESTRICTED WRITING NOTES
// ----------------------------------------------------
@Composable
fun CustomNotesSection(
    notes: String,
    onNotesChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Formatting toolbar
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onNotesChange(notes + "\n# ") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Title, contentDescription = "Header 1", modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = { onNotesChange(notes + "\n## ") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text("H2", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                IconButton(
                    onClick = { onNotesChange(notes + "**bold**") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.FormatBold, contentDescription = "Bold", modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = { onNotesChange(notes + "*italic*") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.FormatItalic, contentDescription = "Italic", modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = { onNotesChange(notes + "\n• ") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = "Bullet List", modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = { onNotesChange(notes + "\n[ ] ") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text("[ ]", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = { Text("Write unrestricted long-form development notes, brainstorming thoughts, scene outlines, dialogue ideas, or research notes for this character...") },
            minLines = 8,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
