package com.example.ai

import com.example.BuildConfig
import com.example.compiler.CompiledManuscript
import com.example.data.model.CharacterEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.SceneEntity
import com.example.data.model.SubplotEntity
import com.example.data.model.WorldEntryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object AssistantService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun isCloudEnabled(): Boolean {
        val apiKey = try {
            BuildConfig::class.java.getField("GEMINI_API_KEY").get(null) as? String ?: ""
        } catch (e: Exception) {
            ""
        }
        return apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"
    }

    suspend fun generateAdvice(
        prompt: String,
        project: ProjectEntity?,
        activeScene: SceneEntity?,
        characters: List<CharacterEntity>,
        subplots: List<SubplotEntity>
    ): String {
        if (project == null) {
            return "Please open a project to receive contextual writing advice."
        }
        return generateAssistance(
            prompt = prompt,
            project = project,
            characters = characters,
            worldEntries = emptyList(),
            subplots = subplots,
            currentScene = activeScene
        )
    }

    suspend fun generateAssistance(
        prompt: String,
        project: ProjectEntity,
        characters: List<CharacterEntity>,
        worldEntries: List<WorldEntryEntity>,
        subplots: List<SubplotEntity>,
        currentScene: SceneEntity? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig::class.java.getField("GEMINI_API_KEY").get(null) as? String ?: ""
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                return@withContext callGemini(apiKey, prompt, project, characters, worldEntries, subplots, currentScene)
            } catch (e: Exception) {
                // Network or quota issue, fall through to offline expert writer knowledge base
            }
        }

        // Offline Writing Assistant logic (always works 100% offline with zero dependencies)
        return@withContext generateOfflineAssistance(prompt, project, characters, worldEntries, subplots, currentScene)
    }

    private fun callGemini(
        apiKey: String,
        userPrompt: String,
        project: ProjectEntity,
        characters: List<CharacterEntity>,
        worldEntries: List<WorldEntryEntity>,
        subplots: List<SubplotEntity>,
        currentScene: SceneEntity?
    ): String {
        val systemContext = buildString {
            append("You are a professional literary editor and novel writing mentor assisting an author with their personal manuscript.\n")
            append("Project Title: '${project.title}'. Author: '${project.author}'.\n")
            if (project.description.isNotBlank()) append("Premise: ${project.description}\n")
            if (characters.isNotEmpty()) {
                append("Key Characters: ")
                append(characters.take(8).joinToString { "${it.name} (${it.occupation.ifBlank { "Character" }}: ${it.goals.ifBlank { "has unknown goal" }})" })
                append("\n")
            }
            if (worldEntries.isNotEmpty()) {
                append("World/Setting: ")
                append(worldEntries.take(6).joinToString { "${it.title} [${it.category}]" })
                append("\n")
            }
            if (subplots.isNotEmpty()) {
                append("Subplots: ")
                append(subplots.joinToString { it.name })
                append("\n")
            }
            if (currentScene != null) {
                append("Active Scene: '${currentScene.title}' (Status: ${currentScene.status}, POV: ${currentScene.povCharacterName ?: "Default"})\n")
                if (currentScene.summary.isNotBlank()) append("Scene Summary: ${currentScene.summary}\n")
                if (currentScene.body.isNotBlank()) append("Current Text Excerpt:\n\"\"\"${currentScene.body.takeLast(1000)}\"\"\"\n")
            }
            append("\nProvide constructive, high-caliber creative advice, sensory details, dialogue beats, or structural guidance. Never be preachy. Answer directly and concisely.")
        }

        val requestJson = JSONObject().apply {
            val contents = JSONArray()
            val contentObj = JSONObject()
            contentObj.put("role", "user")

            val parts = JSONArray()
            parts.put(JSONObject().put("text", "$systemContext\n\nAuthor's Request: $userPrompt"))
            contentObj.put("parts", parts)
            contents.put(contentObj)

            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.75)
                put("maxOutputTokens", 1500)
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        if (!response.isSuccessful) {
            throw RuntimeException("API error: ${response.code} $responseBody")
        }

        val respObj = JSONObject(responseBody)
        val candidates = respObj.optJSONArray("candidates")
        if (candidates != null && candidates.length() > 0) {
            val firstCandidate = candidates.getJSONObject(0)
            val parts = firstCandidate.getJSONObject("content").getJSONArray("parts")
            val textBuilder = StringBuilder()
            for (i in 0 until parts.length()) {
                textBuilder.append(parts.getJSONObject(i).optString("text", ""))
            }
            return textBuilder.toString().trim()
        }

        throw RuntimeException("No response text")
    }

    private fun generateOfflineAssistance(
        prompt: String,
        project: ProjectEntity,
        characters: List<CharacterEntity>,
        worldEntries: List<WorldEntryEntity>,
        subplots: List<SubplotEntity>,
        currentScene: SceneEntity?
    ): String {
        val p = prompt.lowercase()
        return when {
            p.contains("scene") || p.contains("idea") || p.contains("plan") -> {
                buildString {
                    appendLine("💡 Scene Development Framework for '${currentScene?.title ?: project.title}':")
                    appendLine()
                    appendLine("1. Goal: What does the POV character desire at the start of this beat?")
                    appendLine("2. Conflict / Obstacle: What immediate resistance do they encounter?")
                    appendLine("3. Disaster / Turn: How does the scene end in a dilemma or setback?")
                    appendLine("4. Reaction & Dilemma: What emotional impact follows the setback?")
                    if (characters.isNotEmpty()) {
                        val pick = characters.random()
                        appendLine()
                        appendLine("Character Focus: Consider bringing in ${pick.name} (${pick.goals.ifBlank { "Personal objective" }}) to intensify the stakes.")
                    }
                }
            }
            p.contains("character") || p.contains("motivation") -> {
                buildString {
                    appendLine("🎭 Character Arc Matrix:")
                    appendLine("- The Lie: What false belief about the world is this character holding onto?")
                    appendLine("- The Want vs Need: What do they actively pursue, and what truth must they embrace instead?")
                    appendLine("- The Ghost / Wound: What past vulnerability causes their defensive habits?")
                    if (characters.isNotEmpty()) {
                        val c = characters.first()
                        appendLine("\nApplying to ${c.name}:")
                        appendLine("Current Goal: ${c.goals.ifBlank { "Undetermined" }}")
                        appendLine("Internal Conflict: ${c.internalConflict.ifBlank { "Identify their deepest reluctance or guilt." }}")
                    }
                }
            }
            p.contains("summar") || p.contains("review") -> {
                if (currentScene != null && currentScene.body.isNotBlank()) {
                    val words = currentScene.wordCount
                    val chars = currentScene.characterCount
                    val sentences = currentScene.body.split(Regex("[.!?]+")).filter { it.isNotBlank() }
                    "📝 Scene Summary Analysis:\n\n" +
                            "• Length: $words words across ${sentences.size} sentences.\n" +
                            "• POV Character: ${currentScene.povCharacterName ?: "Unassigned"}\n" +
                            "• Setting: ${currentScene.locationName ?: "Unassigned"}\n" +
                            "• Current Status: ${currentScene.status}\n" +
                            "• Opening Line: \"${sentences.firstOrNull()?.trim() ?: ""}\"\n\n" +
                            "Editorial Tip: Check if the opening sentence pulls the reader into sensory action."
                } else {
                    "Your project '${project.title}' currently has ${characters.size} characters, ${worldEntries.size} world items, and ${subplots.size} subplots defined."
                }
            }
            p.contains("dialogue") || p.contains("wording") -> {
                buildString {
                    appendLine("🗣️ Dialogue Polish Checklist:")
                    appendLine("1. Subtext Check: Are characters saying exactly what they mean? Make them veil their real emotions.")
                    appendLine("2. Distinct Cadence: Do different characters use different sentence lengths and idioms?")
                    appendLine("3. Action Beats: Replace adverb dialogue tags (e.g., 'he said angrily') with physical micro-movements.")
                    appendLine("4. The 3-Beat Rule: Every 3 lines of dialogue, ground the reader in the physical room/environment.")
                }
            }
            p.contains("goal") || p.contains("streak") || p.contains("words") -> {
                "🎯 Goal Tracker: '${project.title}' target is ${project.targetWordCount} words. Writing 500 words daily achieves this milestone in ${(project.targetWordCount / 500)} writing sessions."
            }
            else -> {
                buildString {
                    appendLine("📖 Literary Assistant for '${project.title}':")
                    appendLine()
                    appendLine("Ask me to:")
                    appendLine("• 'Brainstorm scene conflict'")
                    appendLine("• 'Analyze character motivation'")
                    appendLine("• 'Polish dialogue'")
                    appendLine("• 'Review current scene stats'")
                    appendLine("• 'Give me a sensory setting prompt'")
                    if (worldEntries.isNotEmpty()) {
                        appendLine("\nWorld Lore available: ${worldEntries.joinToString { it.title }}")
                    }
                }
            }
        }
    }
}
