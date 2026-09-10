package com.example.ui.assistant

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AssistantService
import com.example.data.model.StoryNoteEntity
import com.example.ui.viewmodel.NovelViewModel
import kotlinx.coroutines.launch

data class ChatMessage(
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    viewModel: NovelViewModel
) {
    val project by viewModel.currentProject.collectAsState()
    val activeScene by viewModel.activeScene.collectAsState()
    val characters by viewModel.characters.collectAsState()
    val subplots by viewModel.subplots.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                isUser = false,
                text = "Welcome to your personal literary studio assistant! I can help you brainstorm high-stakes plot twists, overcome writer's block, refine dialogue subtext, or diagnose scene pacing. How can I assist your story today?"
            )
        )
    }

    var inputPrompt by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    val quickPrompts = listOf(
        "Brainstorm Plot Twist",
        "Overcome Writer's Block",
        "Deepen Character Conflict",
        "Critique Scene Pacing",
        "Enhance Dialogue Subtext",
        "Sensory Setting Ideas"
    )

    fun sendUserPrompt(promptText: String) {
        if (promptText.isBlank() || isThinking) return
        messages.add(ChatMessage(isUser = true, text = promptText))
        inputPrompt = ""
        isThinking = true

        coroutineScope.launch {
            val response = AssistantService.generateAdvice(
                prompt = promptText,
                project = project,
                activeScene = activeScene,
                characters = characters,
                subplots = subplots
            )
            messages.add(ChatMessage(isUser = false, text = response))
            isThinking = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Literary Muse & Advisor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                if (AssistantService.isCloudEnabled()) "Connected to Gemini Literary Model" else "Offline Literary Engine active",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            // Quick suggestions chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickPrompts) { p ->
                    FilterChip(
                        selected = false,
                        onClick = { sendUserPrompt(p) },
                        label = { Text(p, fontSize = 12.sp) }
                    )
                }
            }

            // Message history
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (msg.isUser) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.widthIn(max = 320.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (msg.isUser) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (!msg.isUser) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(
                                            onClick = { clipboardManager.setText(AnnotatedString(msg.text)) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.ContentCopy,
                                                contentDescription = "Copy suggestion",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = {
                                                if (project != null) {
                                                    viewModel.saveNote(
                                                        StoryNoteEntity(
                                                            projectId = project!!.id,
                                                            title = "Advice: ${msg.text.take(24)}...",
                                                            category = "Idea",
                                                            content = msg.text
                                                        )
                                                    )
                                                }
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.NoteAdd,
                                                contentDescription = "Save as note",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (isThinking) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Formulating literary guidance...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Input prompt bar
            Surface(
                tonalElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        placeholder = { Text("Ask for advice, dialogue, or beats...") },
                        maxLines = 3,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("assistant_prompt_input"),
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { sendUserPrompt(inputPrompt) },
                        enabled = inputPrompt.isNotBlank() && !isThinking,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (inputPrompt.isNotBlank() && !isThinking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("assistant_send_button")
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (inputPrompt.isNotBlank() && !isThinking) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
