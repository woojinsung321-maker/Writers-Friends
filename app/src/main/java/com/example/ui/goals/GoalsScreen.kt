package com.example.ui.goals

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.WritingGoalEntity
import com.example.ui.theme.StatusDone
import com.example.ui.viewmodel.NovelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: NovelViewModel
) {
    val project by viewModel.currentProject.collectAsState()
    val totalWords by viewModel.totalWordCount.collectAsState()
    val wordsToday by viewModel.wordsToday.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val sessions by viewModel.sessions.collectAsState()

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var goalToDelete by remember { mutableStateOf<WritingGoalEntity?>(null) }

    val targetWords = project?.targetWordCount ?: 50000
    val progress = if (targetWords > 0) (totalWords.toFloat() / targetWords).coerceIn(0f, 1f) else 0f
    val remainingWords = (targetWords - totalWords).coerceAtLeast(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Writing Goals & Targets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddGoalDialog = true },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("New Goal") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_goal_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Manuscript Progress Banner
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Manuscript Word Count",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$totalWords",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = " / $targetWords words",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 3.dp, start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (remainingWords == 0) "Congratulations! You've achieved your target manuscript length!"
                            else "$remainingWords words left to reach your novel milestone.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Daily Writing & Pace Card
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ElevatedCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingUp, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Today's Writing", style = MaterialTheme.typography.labelMedium)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$wordsToday",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text("words written", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    ElevatedCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalFireDepartment, null, tint = Color(0xFFEA580C), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Total Sessions", style = MaterialTheme.typography.labelMedium)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${sessions.size}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text("recorded days", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Custom Goals Header
            item {
                Text(
                    text = "Milestones & Writing Goals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            }

            if (goals.isEmpty()) {
                item {
                    Text("No custom milestones set yet. Tap '+ New Goal' to create chapter or daily targets.")
                }
            } else {
                items(goals, key = { it.id }) { goal ->
                    GoalItemCard(
                        goal = goal,
                        currentWords = totalWords,
                        onDelete = { goalToDelete = goal }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // Modal: Add Goal
    if (showAddGoalDialog) {
        var goalTitle by remember { mutableStateOf("") }
        var targetValStr by remember { mutableStateOf("1000") }
        var goalType by remember { mutableStateOf("DAILY_WORDS") }

        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            title = { Text("Set New Writing Goal") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Goal Title (e.g. 'Daily 500 words')") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = goalType == "DAILY_WORDS",
                            onClick = { goalType = "DAILY_WORDS" },
                            label = { Text("Daily Words") }
                        )
                        FilterChip(
                            selected = goalType == "TOTAL_WORDS",
                            onClick = { goalType = "TOTAL_WORDS" },
                            label = { Text("Total Words") }
                        )
                    }

                    OutlinedTextField(
                        value = targetValStr,
                        onValueChange = { targetValStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Target Words") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (goalTitle.isNotBlank()) {
                            viewModel.saveGoal(
                                WritingGoalEntity(
                                    projectId = viewModel.currentProjectId.value ?: "",
                                    title = goalTitle,
                                    type = goalType,
                                    targetValue = targetValStr.toIntOrNull() ?: 1000
                                )
                            )
                            showAddGoalDialog = false
                        }
                    }
                ) {
                    Text("Create Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGoalDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog: Delete Goal
    goalToDelete?.let { goal ->
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text("Remove Goal?") },
            text = { Text("Remove '${goal.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGoal(goal)
                        goalToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun GoalItemCard(
    goal: WritingGoalEntity,
    currentWords: Int,
    onDelete: () -> Unit
) {
    val progress = (currentWords.toFloat() / goal.targetValue).coerceIn(0f, 1f)

    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Flag, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(goal.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Target: ${goal.targetValue} words", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
            )
        }
    }
}
