package com.example.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import com.example.ui.info.DEVELOPER_EMAIL
import com.example.ui.info.DEVELOPER_NAME
import com.example.ui.info.DeveloperInfoDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.NovelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: NovelViewModel
) {
    val project by viewModel.currentProject.collectAsState()
    val settings by viewModel.projectSettings.collectAsState()

    var author by remember(project?.author) { mutableStateOf(project?.author ?: "Author") }
    var targetWordsStr by remember(project?.targetWordCount) { mutableStateOf((project?.targetWordCount ?: 50000).toString()) }

    var selectedFont by remember(settings?.defaultFontFamily) { mutableStateOf(settings?.defaultFontFamily ?: "Serif") }
    var fontSize by remember(settings?.fontSizeSp) { mutableFloatStateOf(settings?.fontSizeSp ?: 17f) }
    var lineSpacing by remember(settings?.lineSpacing) { mutableFloatStateOf(settings?.lineSpacing ?: 1.6f) }

    val fonts = listOf("Serif", "Sans", "Monospace")
    var showDeveloperInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
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
            // Manuscript Profile Settings
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Project Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)

                        OutlinedTextField(
                            value = author,
                            onValueChange = {
                                author = it
                                project?.let { p -> viewModel.saveProject(p.copy(author = it)) }
                            },
                            label = { Text("Default Author Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = targetWordsStr,
                            onValueChange = {
                                targetWordsStr = it.filter { c -> c.isDigit() }
                                val words = targetWordsStr.toIntOrNull() ?: 50000
                                project?.let { p -> viewModel.saveProject(p.copy(targetWordCount = words)) }
                            },
                            label = { Text("Novel Target Word Count") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Typography & Editor Preferences
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Writing Canvas Typography", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)

                        Text("Typeface Family", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            fonts.forEach { fontName ->
                                FilterChip(
                                    selected = selectedFont == fontName,
                                    onClick = {
                                        selectedFont = fontName
                                        settings?.let { s -> viewModel.updateSettings(s.copy(defaultFontFamily = fontName)) }
                                    },
                                    label = { Text(fontName) }
                                )
                            }
                        }

                        Text("Font Size: ${fontSize.toInt()} sp", style = MaterialTheme.typography.labelMedium)
                        Slider(
                            value = fontSize,
                            onValueChange = {
                                fontSize = it
                                settings?.let { s -> viewModel.updateSettings(s.copy(fontSizeSp = it)) }
                            },
                            valueRange = 14f..26f,
                            steps = 11
                        )

                        Text("Line Spacing: ${(lineSpacing * 10).toInt() / 10f}x", style = MaterialTheme.typography.labelMedium)
                        Slider(
                            value = lineSpacing,
                            onValueChange = {
                                lineSpacing = it
                                settings?.let { s -> viewModel.updateSettings(s.copy(lineSpacing = it)) }
                            },
                            valueRange = 1.2f..2.4f
                        )
                    }
                }
            }

            // Data Protection & Safeguards Card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Safety & Local Storage", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                        Text(
                            "• Real-time 1.5s debounced autosave prevents loss of text.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "• 100% offline-first: Your manuscripts and story lore never leave your local device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "• Point-in-time scene snapshots protect against accidental deletions or unwanted rewrites.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Developer Information Card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Developer Information",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        }

                        Text(
                            text = "Developer: $DEVELOPER_NAME",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Email: $DEVELOPER_EMAIL",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = { showDeveloperInfoDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Full Developer Profile & Contact")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showDeveloperInfoDialog) {
        DeveloperInfoDialog(
            onDismiss = { showDeveloperInfoDialog = false }
        )
    }
}
