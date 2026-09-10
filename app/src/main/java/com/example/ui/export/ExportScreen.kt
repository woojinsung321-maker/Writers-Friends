package com.example.ui.export

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.ui.viewmodel.NovelViewModel
import kotlinx.coroutines.launch
import java.io.File

data class ExportFormatItem(
    val name: String,
    val ext: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val mimeType: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    viewModel: NovelViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val project by viewModel.currentProject.collectAsState()
    val settings by viewModel.projectSettings.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val scenes by viewModel.scenes.collectAsState()
    val totalWords by viewModel.totalWordCount.collectAsState()

    var includeChapterNumbers by remember(settings?.exportChapterNumbering) {
        mutableStateOf(settings?.exportChapterNumbering ?: true)
    }
    var sceneBreakSeparator by remember(settings?.exportSceneSeparator) {
        mutableStateOf(settings?.exportSceneSeparator ?: "* * *")
    }
    var authorName by remember(project?.author) {
        mutableStateOf(project?.author ?: "Author")
    }

    var isExporting by remember { mutableStateOf(false) }

    val exportFormats = listOf(
        ExportFormatItem("EPUB Publication", "epub", "Standard eBook for Apple Books, Kindle, Kobo, and e-readers", Icons.Default.Book, "application/epub+zip"),
        ExportFormatItem("PDF Document", "pdf", "Print-ready formatted manuscript with margins and page breaks", Icons.Default.PictureAsPdf, "application/pdf"),
        ExportFormatItem("Clean HTML", "html", "Styled single-page web manuscript", Icons.Default.Code, "text/html"),
        ExportFormatItem("Rich Text Format (RTF)", "rtf", "Universal format for Microsoft Word, Scrivener, and LibreOffice", Icons.Default.Description, "application/rtf"),
        ExportFormatItem("Plain Text (TXT)", "txt", "Clean UTF-8 text with scene dividers", Icons.Default.Description, "text/plain"),
        ExportFormatItem("Complete JSON Backup", "json", "Full project archive with all characters, world lore, and scene versions", Icons.Default.SaveAlt, "application/json")
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Compile & Export Manuscript", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
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
            // Manuscript Compile Summary Card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = project?.title ?: "Novel Manuscript",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "by $authorName • $totalWords words • ${chapters.size} chapters • ${scenes.size} scenes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Compilation Options
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Formatting & Compilation Options", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = authorName,
                            onValueChange = {
                                authorName = it
                                project?.let { p -> viewModel.saveProject(p.copy(author = it)) }
                            },
                            label = { Text("Author Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Include Chapter Numbering")
                            Switch(
                                checked = includeChapterNumbers,
                                onCheckedChange = {
                                    includeChapterNumbers = it
                                    settings?.let { s -> viewModel.updateSettings(s.copy(exportChapterNumbering = it)) }
                                }
                            )
                        }

                        OutlinedTextField(
                            value = sceneBreakSeparator,
                            onValueChange = {
                                sceneBreakSeparator = it
                                settings?.let { s -> viewModel.updateSettings(s.copy(exportSceneSeparator = it)) }
                            },
                            label = { Text("Scene Break Symbol (e.g. '* * *')") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                Text("Select Export Format", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
            }

            // Formats List
            items(exportFormats) { format ->
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            format.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(format.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            Text(format.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (project == null) return@Button
                                isExporting = true
                                if (format.ext == "json") {
                                    viewModel.backupProject(context) { file ->
                                        isExporting = false
                                        shareFile(context, file, format.mimeType)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Backup created: ${file.name}")
                                        }
                                    }
                                } else {
                                    viewModel.exportManuscript(context, format.ext) { file ->
                                        isExporting = false
                                        shareFile(context, file, format.mimeType)
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Exported successfully: ${file.name}")
                                        }
                                    }
                                }
                            },
                            enabled = !isExporting,
                            modifier = Modifier.testTag("export_${format.ext}_button")
                        ) {
                            Text("Export")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

private fun shareFile(context: Context, file: File, mimeType: String) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, file.name)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share Manuscript via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open share chooser: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
