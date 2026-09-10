package com.example.ui.stats

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusDone
import com.example.ui.viewmodel.NovelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: NovelViewModel
) {
    val project by viewModel.currentProject.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val scenes by viewModel.scenes.collectAsState()
    val totalWords by viewModel.totalWordCount.collectAsState()
    val wordsToday by viewModel.wordsToday.collectAsState()
    val sessions by viewModel.sessions.collectAsState()

    val completedScenes = scenes.count { it.status == "Done" || it.status == "Final Draft" }
    val readTimeMinutes = if (totalWords > 0) (totalWords / 225).coerceAtLeast(1) else 0
    val avgWordsPerChapter = if (chapters.isNotEmpty()) totalWords / chapters.size else 0
    val avgWordsPerScene = if (scenes.isNotEmpty()) totalWords / scenes.size else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Writing Statistics & Analytics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
            // High-Level Overview Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricStatCard(
                        title = "Total Manuscript",
                        value = "$totalWords",
                        subtitle = "words written",
                        icon = Icons.Default.Speed,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Estimated Read Time",
                        value = "${readTimeMinutes / 60}h ${readTimeMinutes % 60}m",
                        subtitle = "at 225 WPM",
                        icon = Icons.Default.AccessTime,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricStatCard(
                        title = "Structure",
                        value = "${chapters.size} ch / ${scenes.size} sc",
                        subtitle = "chapters & scenes",
                        icon = Icons.Default.FormatListNumbered,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Completed Scenes",
                        value = "$completedScenes / ${scenes.size}",
                        subtitle = if (scenes.isNotEmpty()) "${(completedScenes * 100) / scenes.size}% finished" else "0%",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Writing Sessions Activity Chart
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Writing Activity",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                "${sessions.size} logged sessions",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom Bar Chart using Canvas
                        val barColor = MaterialTheme.colorScheme.primary
                        val recentSessions = sessions.take(14).reversed()
                        val maxSessionWords = (recentSessions.maxOfOrNull { it.wordsWritten } ?: 1000).coerceAtLeast(500)

                        if (recentSessions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Start writing in your scenes to record daily progress bars.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            ) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height
                                val barWidth = (canvasWidth / (recentSessions.size * 1.5f)).coerceAtMost(36f)
                                val spacing = (canvasWidth - (barWidth * recentSessions.size)) / (recentSessions.size + 1)

                                recentSessions.forEachIndexed { index, session ->
                                    val barHeight = (session.wordsWritten.toFloat() / maxSessionWords) * (canvasHeight - 30f)
                                    val x = spacing + index * (barWidth + spacing)
                                    val y = canvasHeight - barHeight - 20f

                                    drawRoundRect(
                                        color = barColor,
                                        topLeft = Offset(x, y),
                                        size = Size(barWidth, barHeight.coerceAtLeast(4f)),
                                        cornerRadius = CornerRadius(6f, 6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Detailed Writing Averages
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            "Editorial Metrics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        StatDetailRow(label = "Average Words per Chapter", value = "$avgWordsPerChapter words")
                        StatDetailRow(label = "Average Words per Scene", value = "$avgWordsPerScene words")
                        StatDetailRow(label = "Words Written Today", value = "$wordsToday words")
                        StatDetailRow(label = "Target Completion", value = "${project?.targetWordCount ?: 50000} words")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun MetricStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun StatDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
