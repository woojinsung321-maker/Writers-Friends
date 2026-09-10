package com.example.ui.characters.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CharacterEntity
import com.example.data.model.CharacterTemplateEntity
import com.example.data.model.RelationshipEntity
import com.example.data.model.SceneEntity
import com.example.ui.characters.model.CharacterGoalItem
import com.example.ui.characters.model.CharacterInventoryItem
import com.example.ui.characters.model.CharacterReferenceImage
import com.example.util.CharacterImageStorage
import java.util.UUID

// ----------------------------------------------------
// GOALS SECTION
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoalsSection(
    goals: List<CharacterGoalItem>,
    onGoalsChange: (List<CharacterGoalItem>) -> Unit
) {
    var filterType by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<Pair<Int, CharacterGoalItem>?>(null) }

    val filterOptions = listOf("All", "External", "Internal", "Short-term", "Long-term", "Secret")
    val filteredGoals = goals.filter {
        if (filterType == "All") true
        else it.goalType.equals(filterType, ignoreCase = true)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filterOptions.forEach { type ->
                FilterChip(
                    selected = filterType == type,
                    onClick = { filterType = type },
                    label = { Text(type, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${goals.size} Total Goals",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedButton(
                onClick = { showAddDialog = true },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Goal", style = MaterialTheme.typography.labelMedium)
            }
        }

        if (goals.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "No goals added yet. Stories move forward when characters desire something and face obstacles. Add an external or internal goal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        } else {
            filteredGoals.forEach { goal ->
                val originalIndex = goals.indexOf(goal)
                GoalCardItem(
                    goal = goal,
                    onEdit = { goalToEdit = Pair(originalIndex, goal) },
                    onDelete = {
                        val updated = goals.toMutableList().apply { removeAt(originalIndex) }
                        onGoalsChange(updated)
                    },
                    onToggleComplete = {
                        val updated = goals.toMutableList().apply {
                            val nextStatus = if (goal.status == "Completed") "Active" else "Completed"
                            this[originalIndex] = goal.copy(status = nextStatus)
                        }
                        onGoalsChange(updated)
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        GoalEditDialog(
            goal = null,
            onDismiss = { showAddDialog = false },
            onSave = { newGoal ->
                onGoalsChange(goals + newGoal)
                showAddDialog = false
            }
        )
    }

    goalToEdit?.let { (idx, goal) ->
        GoalEditDialog(
            goal = goal,
            onDismiss = { goalToEdit = null },
            onSave = { updatedGoal ->
                val updated = goals.toMutableList().apply { this[idx] = updatedGoal }
                onGoalsChange(updated)
                goalToEdit = null
            }
        )
    }
}

@Composable
fun GoalCardItem(
    goal: CharacterGoalItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    val isCompleted = goal.status == "Completed"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleComplete, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Toggle Complete",
                    tint = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = goal.description,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = goal.goalType,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = goal.importance,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (goal.motivation.isNotBlank()) {
                    Text(
                        text = "Why: ${goal.motivation}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (goal.obstacle.isNotBlank()) {
                    Text(
                        text = "Obstacle: ${goal.obstacle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalEditDialog(
    goal: CharacterGoalItem?,
    onDismiss: () -> Unit,
    onSave: (CharacterGoalItem) -> Unit
) {
    var description by remember { mutableStateOf(goal?.description ?: "") }
    var goalType by remember { mutableStateOf(goal?.goalType ?: "External") }
    var importance by remember { mutableStateOf(goal?.importance ?: "Crucial") }
    var status by remember { mutableStateOf(goal?.status ?: "Active") }
    var motivation by remember { mutableStateOf(goal?.motivation ?: "") }
    var obstacle by remember { mutableStateOf(goal?.obstacle ?: "") }

    val typeOptions = listOf("External", "Internal", "Short-term", "Long-term", "Secret")
    val importanceOptions = listOf("Crucial", "High", "Medium", "Low")
    val statusOptions = listOf("Active", "Completed", "Failed", "Abandoned")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (goal == null) "Add Character Goal" else "Edit Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("What do they want to achieve? *") },
                    placeholder = { Text("e.g. Uncover the secret ledger before dawn") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    var typeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = goalType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                            typeOptions.forEach { t ->
                                DropdownMenuItem(text = { Text(t) }, onClick = { goalType = t; typeExpanded = false })
                            }
                        }
                    }

                    var impExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = impExpanded,
                        onExpandedChange = { impExpanded = !impExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = importance,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Importance") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = impExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = impExpanded, onDismissRequest = { impExpanded = false }) {
                            importanceOptions.forEach { i ->
                                DropdownMenuItem(text = { Text(i) }, onClick = { importance = i; impExpanded = false })
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = motivation,
                    onValueChange = { motivation = it },
                    label = { Text("Why do they want this? (Stakes)") },
                    placeholder = { Text("What happens if they fail?") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = obstacle,
                    onValueChange = { obstacle = it },
                    label = { Text("What stands in their way?") },
                    placeholder = { Text("The antagonist, environment, or their own flaw") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (description.isNotBlank()) {
                        onSave(
                            CharacterGoalItem(
                                id = goal?.id ?: UUID.randomUUID().toString(),
                                description = description.trim(),
                                goalType = goalType,
                                importance = importance,
                                status = status,
                                motivation = motivation.trim(),
                                obstacle = obstacle.trim()
                            )
                        )
                    }
                },
                enabled = description.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ----------------------------------------------------
// INVENTORY SECTION
// ----------------------------------------------------
@Composable
fun InventorySection(
    inventory: List<CharacterInventoryItem>,
    onInventoryChange: (List<CharacterInventoryItem>) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<Pair<Int, CharacterInventoryItem>?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${inventory.size} Possessions & Items",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedButton(
                onClick = { showAddDialog = true },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Item", style = MaterialTheme.typography.labelMedium)
            }
        }

        if (inventory.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "No items recorded. Add signature weapons, family heirlooms, letters, or story props carried by this character.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        } else {
            inventory.forEachIndexed { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = item.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            if (item.description.isNotBlank()) {
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            if (item.significance.isNotBlank()) {
                                Text(
                                    text = "Story Significance: ${item.significance}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        IconButton(onClick = { itemToEdit = Pair(index, item) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = {
                                val updated = inventory.toMutableList().apply { removeAt(index) }
                                onInventoryChange(updated)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        InventoryEditDialog(
            item = null,
            onDismiss = { showAddDialog = false },
            onSave = { newItem ->
                onInventoryChange(inventory + newItem)
                showAddDialog = false
            }
        )
    }

    itemToEdit?.let { (idx, item) ->
        InventoryEditDialog(
            item = item,
            onDismiss = { itemToEdit = null },
            onSave = { updatedItem ->
                val updated = inventory.toMutableList().apply { this[idx] = updatedItem }
                onInventoryChange(updated)
                itemToEdit = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryEditDialog(
    item: CharacterInventoryItem?,
    onDismiss: () -> Unit,
    onSave: (CharacterInventoryItem) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var category by remember { mutableStateOf(item?.category ?: "Signature Weapon") }
    var description by remember { mutableStateOf(item?.description ?: "") }
    var significance by remember { mutableStateOf(item?.significance ?: "") }

    val categories = listOf("Signature Weapon", "Armor & Clothing", "Keepsake & Heirloom", "Story Prop / Artifact", "Tool / Equipment", "Vehicle", "Document / Book", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Add Inventory Item" else "Edit Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name *") },
                    placeholder = { Text("e.g. Frostbite (Dagger)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                var catExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                        categories.forEach { c ->
                            DropdownMenuItem(text = { Text(c) }, onClick = { category = c; catExpanded = false })
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Appearance & Capabilities") },
                    placeholder = { Text("Physical details, magical traits, or materials...") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = significance,
                    onValueChange = { significance = it },
                    label = { Text("Emotional / Plot Significance") },
                    placeholder = { Text("Gift from father before he died; needed to unlock the tomb") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            CharacterInventoryItem(
                                id = item?.id ?: UUID.randomUUID().toString(),
                                name = name.trim(),
                                category = category,
                                description = description.trim(),
                                significance = significance.trim()
                            )
                        )
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

// ----------------------------------------------------
// PORTRAIT & REFERENCE GALLERY SECTION
// ----------------------------------------------------
@Composable
fun ImageGallerySection(
    characterName: String,
    primaryImageUri: String?,
    imageDisplayMode: String,
    gallery: List<CharacterReferenceImage>,
    onPrimaryImageChange: (String?, String) -> Unit,
    onGalleryChange: (List<CharacterReferenceImage>) -> Unit
) {
    val context = LocalContext.current
    var showAddReferenceDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Primary Image Pickers
    val primaryGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = CharacterImageStorage.saveImageFromUri(context, it)
            if (savedPath != null) {
                onPrimaryImageChange(savedPath, imageDisplayMode)
            }
        }
    }

    val primaryCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            val savedPath = CharacterImageStorage.saveImageFromUri(context, tempCameraUri!!)
            if (savedPath != null) {
                onPrimaryImageChange(savedPath, imageDisplayMode)
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Primary Portrait Display
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Primary Character Portrait",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                if (primaryImageUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        AsyncImage(
                            model = CharacterImageStorage.getFileForPath(context, primaryImageUri),
                            contentDescription = "Character Portrait",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = if (imageDisplayMode == "fit") ContentScale.Fit else ContentScale.Crop
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val nextMode = if (imageDisplayMode == "fit") "cover" else "fit"
                                    onPrimaryImageChange(primaryImageUri, nextMode)
                                },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.FitScreen, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (imageDisplayMode == "fit") "Mode: Fit" else "Mode: Fill", style = MaterialTheme.typography.labelSmall)
                            }

                            OutlinedButton(
                                onClick = { primaryGalleryLauncher.launch("image/*") },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Replace", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        TextButton(
                            onClick = { onPrimaryImageChange(null, imageDisplayMode) },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "No portrait selected for $characterName",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { primaryGalleryLauncher.launch("image/*") },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Device Photo")
                                }
                                OutlinedButton(
                                    onClick = {
                                        val uri = CharacterImageStorage.createTempCameraUri(context)
                                        tempCameraUri = uri
                                        uri?.let { primaryCameraLauncher.launch(it) }
                                    },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Camera")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Reference Images Gallery
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Visual Reference Gallery (${gallery.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Outfits, weapons, hairstyles, expressions, and aesthetic moodboards",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedButton(
                onClick = { showAddReferenceDialog = true },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Photo", style = MaterialTheme.typography.labelMedium)
            }
        }

        if (gallery.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "No reference photos added yet. Upload character visual inspirations, clothing references, or face studies.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                gallery.forEachIndexed { index, ref ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                AsyncImage(
                                    model = CharacterImageStorage.getFileForPath(context, ref.uri),
                                    contentDescription = ref.caption,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = ref.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                if (ref.caption.isNotBlank()) {
                                    Text(
                                        text = ref.caption,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }

                            // Action: Set as primary portrait
                            IconButton(
                                onClick = { onPrimaryImageChange(ref.uri, "cover") },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Bookmark, contentDescription = "Set as Primary", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = {
                                    val updated = gallery.toMutableList().apply { removeAt(index) }
                                    onGalleryChange(updated)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddReferenceDialog) {
        AddReferencePhotoDialog(
            onDismiss = { showAddReferenceDialog = false },
            onSave = { newRef ->
                onGalleryChange(gallery + newRef)
                showAddReferenceDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReferencePhotoDialog(
    onDismiss: () -> Unit,
    onSave: (CharacterReferenceImage) -> Unit
) {
    val context = LocalContext.current
    var selectedPath by remember { mutableStateOf<String?>(null) }
    var caption by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Face & Portrait") }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val categories = listOf("Face & Portrait", "Full-body", "Clothing & Wardrobe", "Hairstyle", "Weapons & Armor", "Accessories", "Different Ages", "Expressions", "Aesthetic / Vibe")

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val saved = CharacterImageStorage.saveImageFromUri(context, it)
            if (saved != null) selectedPath = saved
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            val saved = CharacterImageStorage.saveImageFromUri(context, tempCameraUri!!)
            if (saved != null) selectedPath = saved
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Reference Image") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (selectedPath != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        AsyncImage(
                            model = CharacterImageStorage.getFileForPath(context, selectedPath!!),
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pick Image")
                        }
                        OutlinedButton(
                            onClick = {
                                val uri = CharacterImageStorage.createTempCameraUri(context)
                                tempCameraUri = uri
                                uri?.let { cameraLauncher.launch(it) }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CameraAlt, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Camera")
                        }
                    }
                }

                var catExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = catExpanded,
                    onExpandedChange = { catExpanded = !catExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                        categories.forEach { c ->
                            DropdownMenuItem(text = { Text(c) }, onClick = { category = c; catExpanded = false })
                        }
                    }
                }

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Caption / Description") },
                    placeholder = { Text("e.g. Winter cloak worn during Act II") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedPath != null) {
                        onSave(
                            CharacterReferenceImage(
                                id = UUID.randomUUID().toString(),
                                uri = selectedPath!!,
                                category = category,
                                caption = caption.trim()
                            )
                        )
                    }
                },
                enabled = selectedPath != null
            ) {
                Text("Add Photo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ----------------------------------------------------
// RELATIONSHIPS SECTION
// ----------------------------------------------------
@Composable
fun RelationshipsSection(
    character: CharacterEntity,
    allCharacters: List<CharacterEntity>,
    relationships: List<RelationshipEntity>,
    onSaveRelationship: (RelationshipEntity) -> Unit,
    onDeleteRelationship: (RelationshipEntity) -> Unit
) {
    var showMapDialog by remember { mutableStateOf(false) }

    val relevantRelationships = relationships.filter {
        it.sourceId == character.id || it.targetId == character.id
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${relevantRelationships.size} Mapped Relationships",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedButton(
                onClick = { showMapDialog = true },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Map Relationship", style = MaterialTheme.typography.labelMedium)
            }
        }

        if (relevantRelationships.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "No relationships mapped for ${character.name}. Connect them to allies, rivals, family, or mentors in your project.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        } else {
            relevantRelationships.forEach { rel ->
                val targetName = if (rel.sourceId == character.id) rel.targetName else rel.sourceName

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = targetName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = rel.relationType,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (rel.description.isNotBlank()) {
                                Text(
                                    text = rel.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            if (rel.notes.isNotBlank()) {
                                Text(
                                    text = "Dynamic Notes / Conflict: ${rel.notes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        IconButton(onClick = { onDeleteRelationship(rel) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    if (showMapDialog) {
        MapRelationshipDialog(
            currentCharacter = character,
            otherCharacters = allCharacters.filter { it.id != character.id },
            onDismiss = { showMapDialog = false },
            onSave = { newRel ->
                onSaveRelationship(newRel)
                showMapDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapRelationshipDialog(
    currentCharacter: CharacterEntity,
    otherCharacters: List<CharacterEntity>,
    onDismiss: () -> Unit,
    onSave: (RelationshipEntity) -> Unit
) {
    var selectedTargetId by remember { mutableStateOf(otherCharacters.firstOrNull()?.id ?: "") }
    var relationType by remember { mutableStateOf("Friend") }
    var description by remember { mutableStateOf("") }
    var conflict by remember { mutableStateOf("") }

    val relationTypes = listOf("Friend", "Allied Rival", "Enemy / Nemesis", "Love Interest", "Spouse / Partner", "Parent", "Child", "Sibling", "Mentor", "Student / Apprentice", "Employer", "Subordinate", "Estranged", "Secret Confidant")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Map Relationship for ${currentCharacter.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (otherCharacters.isEmpty()) {
                    Text(
                        "No other characters exist in this project yet. Create additional characters to map relationships between them.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    var targetExpanded by remember { mutableStateOf(false) }
                    val selectedTarget = otherCharacters.firstOrNull { it.id == selectedTargetId }
                    ExposedDropdownMenuBox(
                        expanded = targetExpanded,
                        onExpandedChange = { targetExpanded = !targetExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedTarget?.name ?: "Select Character",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Related Character *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = targetExpanded, onDismissRequest = { targetExpanded = false }) {
                            otherCharacters.forEach { c ->
                                DropdownMenuItem(text = { Text(c.name) }, onClick = { selectedTargetId = c.id; targetExpanded = false })
                            }
                        }
                    }

                    var typeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = relationType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Relationship Type *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                            relationTypes.forEach { t ->
                                DropdownMenuItem(text = { Text(t) }, onClick = { relationType = t; typeExpanded = false })
                            }
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Relationship Dynamic & History") },
                        placeholder = { Text("How do they interact? How did they meet?") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = conflict,
                        onValueChange = { conflict = it },
                        label = { Text("Core Conflict / Tension") },
                        placeholder = { Text("What drives a wedge between them in this story?") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val targetChar = otherCharacters.firstOrNull { it.id == selectedTargetId }
                    if (targetChar != null) {
                        onSave(
                            RelationshipEntity(
                                id = UUID.randomUUID().toString(),
                                projectId = currentCharacter.projectId,
                                sourceId = currentCharacter.id,
                                sourceName = currentCharacter.name,
                                sourceType = "Character",
                                targetId = targetChar.id,
                                targetName = targetChar.name,
                                targetType = "Character",
                                relationType = relationType,
                                description = description.trim(),
                                strength = "Moderate",
                                notes = conflict.trim()
                            )
                        )
                    }
                },
                enabled = selectedTargetId.isNotBlank() && otherCharacters.isNotEmpty()
            ) {
                Text("Map")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ----------------------------------------------------
// SCENES SECTION
// ----------------------------------------------------
@Composable
fun ScenesSection(
    characterName: String,
    scenes: List<SceneEntity>,
    onOpenScene: (SceneEntity) -> Unit
) {
    val appearances = remember(characterName, scenes) {
        scenes.filter { s ->
            s.povCharacterName?.equals(characterName, ignoreCase = true) == true ||
                    s.title.contains(characterName, ignoreCase = true) ||
                    s.summary.contains(characterName, ignoreCase = true) ||
                    s.body.contains(characterName, ignoreCase = true)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${appearances.size} Scenes with $characterName",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (appearances.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Text(
                    text = "No scenes currently feature $characterName. Mention their name in scenes or assign them as the POV character in the scene editor to link them automatically.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }
        } else {
            appearances.forEach { scene ->
                val isPov = scene.povCharacterName?.equals(characterName, ignoreCase = true) == true
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenScene(scene) },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = scene.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (isPov) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "POV",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "${scene.wordCount} words • Status: ${scene.status}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open Scene",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// CUSTOM FIELDS SECTION
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomFieldsSection(
    customFieldsJson: String,
    onCustomFieldsJsonChange: (String) -> Unit
) {
    val fields = remember(customFieldsJson) {
        com.example.ui.characters.model.CharacterDataSerializers.deserializeCustomFields(customFieldsJson).toMutableList()
    }

    var newLabel by remember { mutableStateOf("") }
    var newValue by remember { mutableStateOf("") }

    val suggestions = listOf("Zodiac Sign", "Blood Type", "Alignment", "Myers-Briggs / MBTI", "Enneagram", "Theme Song / Motif", "Hogwarts House", "Spiritual Patron", "Aura Color")

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Create unlimited user-defined attributes tailored to your story or fantasy world.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Suggestion Chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            suggestions.forEach { suggestion ->
                SuggestionChip(
                    onClick = { newLabel = suggestion },
                    label = { Text(suggestion, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        // Add Field Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newLabel,
                onValueChange = { newLabel = it },
                label = { Text("Field Name") },
                placeholder = { Text("e.g. Alignment") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = newValue,
                onValueChange = { newValue = it },
                label = { Text("Value") },
                placeholder = { Text("e.g. Chaotic Good") },
                singleLine = true,
                modifier = Modifier.weight(1.2f)
            )
            IconButton(
                onClick = {
                    if (newLabel.isNotBlank()) {
                        fields.add(com.example.ui.characters.model.CharacterCustomField(label = newLabel.trim(), value = newValue.trim()))
                        onCustomFieldsJsonChange(com.example.ui.characters.model.CharacterDataSerializers.serializeCustomFields(fields))
                        newLabel = ""
                        newValue = ""
                    }
                },
                enabled = newLabel.isNotBlank()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Field", tint = MaterialTheme.colorScheme.primary)
            }
        }

        if (fields.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                fields.forEachIndexed { index, field ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = field.label,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = field.value,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1.5f)
                            )
                            IconButton(
                                onClick = {
                                    fields.removeAt(index)
                                    onCustomFieldsJsonChange(com.example.ui.characters.model.CharacterDataSerializers.serializeCustomFields(fields))
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TEMPLATES DIALOG (BUILT-IN + PROJECT TEMPLATES)
// ----------------------------------------------------
@Composable
fun CharacterTemplatesDialog(
    projectTemplates: List<CharacterTemplateEntity>,
    onDismiss: () -> Unit,
    onApplyTemplate: (templateName: String, templateJson: String) -> Unit,
    onSaveAsTemplate: (name: String, description: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Predefined, 1: Project Custom, 2: Save Current
    var customTemplateName by remember { mutableStateOf("") }
    var customTemplateDesc by remember { mutableStateOf("") }

    val predefined = listOf(
        PredefinedTemplate("Epic Fantasy Protagonist", "Ideal for heroes facing a world-shattering destiny with deep lore, mentors, and oaths.", "fantasy"),
        PredefinedTemplate("Noir Detective / Mystery Lead", "Hard-boiled, observant, burdened with ghosts and cynical worldviews.", "noir"),
        PredefinedTemplate("Contemporary Romance Lead", "Focus on emotional wounds, vulnerabilities, relationship dynamics, and chemistry.", "romance"),
        PredefinedTemplate("Deep Psychological Drama", "Explores identity masks, repressed guilt, unconscious contradictions, and trauma.", "psychological"),
        PredefinedTemplate("Complex Antagonist / Anti-Hero", "Compelling moral code, justified grievance, terrifying breaking point, and tragic flaws.", "villain")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Character Templates") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    FilterChip(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("Archetypes", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    FilterChip(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("Saved (${projectTemplates.size})", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    FilterChip(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        label = { Text("Save As", style = MaterialTheme.typography.labelSmall) }
                    )
                }

                when (selectedTab) {
                    0 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            predefined.forEach { tmpl ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val generatedJson = generateArchetypeJson(tmpl.type)
                                            onApplyTemplate(tmpl.title, generatedJson)
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(tmpl.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text(tmpl.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        if (projectTemplates.isEmpty()) {
                            Text(
                                "No custom templates saved for this project yet. Use 'Save As' to turn the current character structure into a reusable template.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                projectTemplates.forEach { pt ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onApplyTemplate(pt.name, pt.templateJson) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(pt.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                            if (pt.description.isNotBlank()) {
                                                Text(pt.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Save the current character's field configuration and notes layout as a custom template for other characters in this project.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedTextField(
                                value = customTemplateName,
                                onValueChange = { customTemplateName = it },
                                label = { Text("Template Name *") },
                                placeholder = { Text("e.g. Magic Academy Student") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = customTemplateDesc,
                                onValueChange = { customTemplateDesc = it },
                                label = { Text("Description") },
                                placeholder = { Text("For second-year sorcery apprentices...") },
                                minLines = 2,
                                modifier = Modifier.fillMaxWidth()
                            )
                            ButtonDefaults.outlinedButtonColors()
                            OutlinedButton(
                                onClick = {
                                    if (customTemplateName.isNotBlank()) {
                                        onSaveAsTemplate(customTemplateName.trim(), customTemplateDesc.trim())
                                        selectedTab = 1
                                    }
                                },
                                enabled = customTemplateName.isNotBlank(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save Current Character as Template")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

data class PredefinedTemplate(val title: String, val description: String, val type: String)

fun generateArchetypeJson(type: String): String {
    // Returns guided cue content in JSON structure
    val obj = org.json.JSONObject()
    when (type) {
        "fantasy" -> {
            obj.put("roleInStory", "Protagonist")
            obj.put("species", "Human (Touched by Magic)")
            obj.put("coreDesire", "To protect their village and understand the ancient power stirring within them")
            obj.put("primaryMotivation", "Fulfill the oath sworn to their fallen mentor")
            obj.put("greatestFear", "Losing control of their magic and hurting loved ones")
            obj.put("temperament", "Determined, honorable, occasionally reckless")
            obj.put("values", "Loyalty to companions, never betraying a host")
        }
        "noir" -> {
            obj.put("roleInStory", "POV Character / Detective")
            obj.put("occupation", "Private Investigator")
            obj.put("coreDesire", "To find the truth in a corrupt city, no matter who burns")
            obj.put("primaryMotivation", "Redeem themselves for a past case where an innocent took the blame")
            obj.put("temperament", "Cynical, melancholic, razor-sharp observer")
            obj.put("speakingStyle", "Deadpan, laconic, steeped in dry irony")
        }
        "romance" -> {
            obj.put("roleInStory", "Romance Lead")
            obj.put("coreDesire", "To be accepted for who they truly are without having to maintain a facade")
            obj.put("emotionalWounds", "Betrayal in a previous relationship that made them build emotional walls")
            obj.put("internalConflict", "Desire for intimacy vs. overwhelming terror of vulnerability")
            obj.put("misconceptions", "\"If I let someone see my flaws, they will leave\"")
        }
        "psychological" -> {
            obj.put("coreDesire", "To regain a sense of agency and silence the guilt haunting them")
            obj.put("secrets", "A catastrophic decision made in youth that resulted in a friend's downfall")
            obj.put("breakingPoint", "Being trapped in a situation where they have no control")
            obj.put("wantOthersToThink", "Calculated, self-sufficient, completely unbothered")
            obj.put("actuallyThinkOfSelf", "Fragile, running out of time, deeply isolated")
        }
        "villain" -> {
            obj.put("roleInStory", "Antagonist")
            obj.put("coreDesire", "To reshape the realm so that the injustice they suffered can never happen to anyone else")
            obj.put("primaryMotivation", "Right a monumental wrong that the existing system ignored")
            obj.put("temperament", "Passionate, unyielding, charismatic, merciless to hypocrisy")
            obj.put("values", "The end justifies the means; order above comfort")
        }
    }
    return obj.toString()
}
