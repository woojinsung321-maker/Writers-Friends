package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AssistantService
import com.example.compiler.BookCompiler
import com.example.compiler.CompiledManuscript
import com.example.data.db.AppDatabase
import com.example.data.model.ActEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.CharacterEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectSettingsEntity
import com.example.data.model.RelationshipEntity
import com.example.data.model.SceneEntity
import com.example.data.model.SceneVersionEntity
import com.example.data.model.StoryNoteEntity
import com.example.data.model.SubplotEntity
import com.example.data.model.WorldEntryEntity
import com.example.data.model.WritingGoalEntity
import com.example.data.model.WritingSessionEntity
import com.example.data.repository.NovelRepository
import com.example.export.ManuscriptExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NovelViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = NovelRepository(db)

    // --- Projects Lists ---
    val activeProjects: StateFlow<List<ProjectEntity>> = repository.activeProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedProjects: StateFlow<List<ProjectEntity>> = repository.archivedProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Current Active Project State ---
    private val _currentProjectId = MutableStateFlow<String?>(null)
    val currentProjectId = _currentProjectId.asStateFlow()

    private val _currentProject = MutableStateFlow<ProjectEntity?>(null)
    val currentProject = _currentProject.asStateFlow()

    private val _projectSettings = MutableStateFlow<ProjectSettingsEntity?>(null)
    val projectSettings = _projectSettings.asStateFlow()

    private val _acts = MutableStateFlow<List<ActEntity>>(emptyList())
    val acts = _acts.asStateFlow()

    private val _chapters = MutableStateFlow<List<ChapterEntity>>(emptyList())
    val chapters = _chapters.asStateFlow()

    private val _scenes = MutableStateFlow<List<SceneEntity>>(emptyList())
    val scenes = _scenes.asStateFlow()

    private val _characters = MutableStateFlow<List<CharacterEntity>>(emptyList())
    val characters = _characters.asStateFlow()

    private val _worldEntries = MutableStateFlow<List<WorldEntryEntity>>(emptyList())
    val worldEntries = _worldEntries.asStateFlow()

    private val _relationships = MutableStateFlow<List<RelationshipEntity>>(emptyList())
    val relationships = _relationships.asStateFlow()

    private val _subplots = MutableStateFlow<List<SubplotEntity>>(emptyList())
    val subplots = _subplots.asStateFlow()

    private val _notes = MutableStateFlow<List<StoryNoteEntity>>(emptyList())
    val notes = _notes.asStateFlow()

    private val _goals = MutableStateFlow<List<WritingGoalEntity>>(emptyList())
    val goals = _goals.asStateFlow()

    private val _sessions = MutableStateFlow<List<WritingSessionEntity>>(emptyList())
    val sessions = _sessions.asStateFlow()

    private val _totalWordCount = MutableStateFlow(0)
    val totalWordCount = _totalWordCount.asStateFlow()

    private val _wordsToday = MutableStateFlow(0)
    val wordsToday = _wordsToday.asStateFlow()

    // --- Active Scene in Editor ---
    private val _activeScene = MutableStateFlow<SceneEntity?>(null)
    val activeScene = _activeScene.asStateFlow()

    private val _sceneVersions = MutableStateFlow<List<SceneVersionEntity>>(emptyList())
    val sceneVersions = _sceneVersions.asStateFlow()

    // Autosave Debounce Job
    private var autosaveJob: Job? = null

    // UI Global Navigation State
    val currentWorkspaceTab = MutableStateFlow(WorkspaceTab.WRITE)

    // Compiled Manuscript Cache
    private val _compiledManuscript = MutableStateFlow<CompiledManuscript?>(null)
    val compiledManuscript = _compiledManuscript.asStateFlow()

    // Assistant UI State
    val assistantLoading = MutableStateFlow(false)
    val assistantResponse = MutableStateFlow<String?>(null)

    // Export & Backup status message
    val exportStatusMessage = MutableStateFlow<String?>(null)

    private var projectDataJob: Job? = null
    private var sceneVersionsJob: Job? = null

    fun selectProject(projectId: String?) {
        // Save current active scene changes immediately before switching
        autosaveJob?.cancel()
        saveActiveSceneContent()

        projectDataJob?.cancel()
        sceneVersionsJob?.cancel()

        _currentProjectId.value = projectId
        _activeScene.value = null
        _sceneVersions.value = emptyList()

        if (projectId == null) {
            _currentProject.value = null
            _projectSettings.value = null
            _acts.value = emptyList()
            _chapters.value = emptyList()
            _scenes.value = emptyList()
            _characters.value = emptyList()
            _worldEntries.value = emptyList()
            _relationships.value = emptyList()
            _subplots.value = emptyList()
            _notes.value = emptyList()
            _goals.value = emptyList()
            _sessions.value = emptyList()
            _totalWordCount.value = 0
            _wordsToday.value = 0
            return
        }

        currentWorkspaceTab.value = WorkspaceTab.WRITE

        projectDataJob = viewModelScope.launch {
            launch {
                repository.getProject(projectId).collect { _currentProject.value = it }
            }
            launch {
                repository.getSettings(projectId).collect { _projectSettings.value = it }
            }
            launch {
                repository.getActs(projectId).collect { _acts.value = it }
            }
            launch {
                repository.getChapters(projectId).collect { _chapters.value = it }
            }
            launch {
                repository.getAllScenes(projectId).collect { sceneList ->
                    _scenes.value = sceneList
                    // If active scene is null and we have scenes, default to the first scene
                    if (_activeScene.value == null && sceneList.isNotEmpty()) {
                        _activeScene.value = sceneList.first()
                        loadSceneVersions(sceneList.first().id)
                    }
                }
            }
            launch {
                repository.getCharacters(projectId).collect { _characters.value = it }
            }
            launch {
                repository.getWorldEntries(projectId).collect { _worldEntries.value = it }
            }
            launch {
                repository.getRelationships(projectId).collect { _relationships.value = it }
            }
            launch {
                repository.getSubplots(projectId).collect { _subplots.value = it }
            }
            launch {
                repository.getNotes(projectId).collect { _notes.value = it }
            }
            launch {
                repository.getGoals(projectId).collect { _goals.value = it }
            }
            launch {
                repository.getSessions(projectId).collect { _sessions.value = it }
            }
            launch {
                repository.getTotalWordCount(projectId).collect { _totalWordCount.value = it }
            }
            launch {
                repository.getWordsWrittenToday(projectId).collect { _wordsToday.value = it }
            }
        }
    }

    fun selectScene(scene: SceneEntity) {
        // Save previous scene before selecting new one
        autosaveJob?.cancel()
        saveActiveSceneContent()

        _activeScene.value = scene
        loadSceneVersions(scene.id)
    }

    private fun loadSceneVersions(sceneId: String) {
        sceneVersionsJob?.cancel()
        sceneVersionsJob = viewModelScope.launch {
            repository.getSceneVersions(sceneId).collect { _sceneVersions.value = it }
        }
    }

    // --- Autosave & Scene Content Updates ---
    fun onSceneContentChanged(newBody: String) {
        val scene = _activeScene.value ?: return
        val words = if (newBody.isBlank()) 0 else newBody.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        val chars = newBody.length
        val wordDelta = (words - scene.wordCount).coerceAtLeast(0)

        _activeScene.value = scene.copy(
            body = newBody,
            wordCount = words,
            characterCount = chars,
            updatedAt = System.currentTimeMillis()
        )

        // Cancel previous pending autosave and debounce
        autosaveJob?.cancel()
        autosaveJob = viewModelScope.launch {
            delay(1500) // 1.5 second debounce for typing comfort
            saveActiveSceneContent()
            if (wordDelta > 0) {
                repository.recordWritingProgress(scene.projectId, wordDelta)
            }
        }
    }

    fun saveActiveSceneContent() {
        val scene = _activeScene.value ?: return
        viewModelScope.launch {
            repository.updateSceneContent(
                sceneId = scene.id,
                projectId = scene.projectId,
                body = scene.body,
                wordCount = scene.wordCount,
                characterCount = scene.characterCount
            )
        }
    }

    fun updateActiveSceneMetadata(
        title: String,
        status: String,
        povName: String?,
        locationName: String?,
        timeSetting: String?,
        summary: String,
        purpose: String,
        notes: String
    ) {
        val scene = _activeScene.value ?: return
        val updated = scene.copy(
            title = title.ifBlank { "Untitled Scene" },
            status = status,
            povCharacterName = povName,
            locationName = locationName,
            timeSetting = timeSetting,
            summary = summary,
            purpose = purpose,
            notes = notes,
            updatedAt = System.currentTimeMillis()
        )
        _activeScene.value = updated
        viewModelScope.launch {
            repository.saveScene(updated)
        }
    }

    // --- Version Snapshots ---
    fun createVersionSnapshot(name: String, notes: String) {
        val scene = _activeScene.value ?: return
        viewModelScope.launch {
            repository.createSceneVersion(
                sceneId = scene.id,
                projectId = scene.projectId,
                name = name,
                notes = notes,
                body = scene.body,
                wordCount = scene.wordCount
            )
        }
    }

    fun restoreVersionSnapshot(version: SceneVersionEntity) {
        onSceneContentChanged(version.bodySnapshot)
        saveActiveSceneContent()
    }

    fun deleteVersionSnapshot(version: SceneVersionEntity) {
        viewModelScope.launch { repository.deleteVersion(version) }
    }

    // --- Project Operations ---
    fun createProject(
        title: String,
        author: String,
        description: String,
        targetWords: Int,
        coverColor: Long,
        coverIcon: String,
        onCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            val project = repository.createProject(
                title = title,
                author = author,
                description = description,
                targetWords = targetWords,
                coverColor = coverColor,
                coverIcon = coverIcon
            )
            selectProject(project.id)
            onCreated(project.id)
        }
    }

    fun updateProject(project: ProjectEntity) {
        viewModelScope.launch { repository.updateProject(project) }
    }

    fun archiveProject(projectId: String, archive: Boolean) {
        viewModelScope.launch { repository.setArchived(projectId, archive) }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            if (_currentProjectId.value == projectId) {
                selectProject(null)
            }
            repository.deleteProject(projectId)
        }
    }

    fun duplicateProject(projectId: String, onDuplicated: (String) -> Unit) {
        viewModelScope.launch {
            val copy = repository.duplicateProject(projectId)
            if (copy != null) {
                onDuplicated(copy.id)
            }
        }
    }

    // --- Chapters & Acts & Scenes ---
    fun createChapter(actId: String?, title: String, summary: String = "") {
        val projId = _currentProjectId.value ?: return
        val nextNumber = (_chapters.value.maxOfOrNull { it.chapterNumber } ?: 0) + 1
        val nextOrder = (_chapters.value.maxOfOrNull { it.sortOrder } ?: 0) + 1
        val chapter = ChapterEntity(
            projectId = projId,
            actId = actId,
            title = title.ifBlank { "Chapter $nextNumber" },
            chapterNumber = nextNumber,
            sortOrder = nextOrder,
            summary = summary
        )
        viewModelScope.launch {
            repository.saveChapter(chapter)
            // Also create a first scene inside this chapter for convenience
            val scene = SceneEntity(
                projectId = projId,
                chapterId = chapter.id,
                title = "Scene 1",
                sortOrder = 0,
                status = "To Do"
            )
            repository.saveScene(scene)
            _activeScene.value = scene
        }
    }

    fun updateChapter(chapter: ChapterEntity) {
        viewModelScope.launch { repository.saveChapter(chapter) }
    }

    fun deleteChapter(chapterId: String) {
        val projId = _currentProjectId.value ?: return
        viewModelScope.launch { repository.deleteChapter(chapterId, projId) }
    }

    fun createAct(title: String, description: String = "") {
        val projId = _currentProjectId.value ?: return
        val nextOrder = (_acts.value.maxOfOrNull { it.sortOrder } ?: 0) + 1
        val act = ActEntity(
            projectId = projId,
            title = title.ifBlank { "Act ${nextOrder + 1}" },
            sortOrder = nextOrder,
            description = description
        )
        viewModelScope.launch { repository.saveAct(act) }
    }

    fun updateAct(act: ActEntity) {
        viewModelScope.launch { repository.saveAct(act) }
    }

    fun deleteAct(act: ActEntity) {
        viewModelScope.launch { repository.deleteAct(act) }
    }

    fun createScene(chapterId: String, title: String = "") {
        val projId = _currentProjectId.value ?: return
        val scenesInChapter = _scenes.value.filter { it.chapterId == chapterId }
        val nextOrder = (scenesInChapter.maxOfOrNull { it.sortOrder } ?: -1) + 1
        val scene = SceneEntity(
            projectId = projId,
            chapterId = chapterId,
            title = title.ifBlank { "Scene ${nextOrder + 1}" },
            sortOrder = nextOrder,
            status = "To Do"
        )
        viewModelScope.launch {
            repository.saveScene(scene)
            _activeScene.value = scene
        }
    }

    fun moveScene(sceneId: String, targetChapterId: String) {
        val projId = _currentProjectId.value ?: return
        val scenesInTarget = _scenes.value.filter { it.chapterId == targetChapterId }
        val nextOrder = (scenesInTarget.maxOfOrNull { it.sortOrder } ?: -1) + 1
        viewModelScope.launch {
            repository.moveScene(sceneId, targetChapterId, nextOrder, projId)
        }
    }

    fun deleteScene(sceneId: String) {
        val projId = _currentProjectId.value ?: return
        viewModelScope.launch {
            repository.deleteScene(sceneId, projId)
            if (_activeScene.value?.id == sceneId) {
                _activeScene.value = _scenes.value.firstOrNull { it.id != sceneId }
            }
        }
    }

    // --- Characters ---
    fun saveCharacter(character: CharacterEntity) {
        viewModelScope.launch { repository.saveCharacter(character) }
    }

    fun deleteCharacter(character: CharacterEntity) {
        viewModelScope.launch { repository.deleteCharacter(character) }
    }

    // --- World Entries ---
    fun saveWorldEntry(entry: WorldEntryEntity) {
        viewModelScope.launch { repository.saveWorldEntry(entry) }
    }

    fun deleteWorldEntry(entry: WorldEntryEntity) {
        viewModelScope.launch { repository.deleteWorldEntry(entry) }
    }

    // --- Relationships ---
    fun saveRelationship(rel: RelationshipEntity) {
        viewModelScope.launch { repository.saveRelationship(rel) }
    }

    fun deleteRelationship(rel: RelationshipEntity) {
        viewModelScope.launch { repository.deleteRelationship(rel) }
    }

    // --- Subplots ---
    fun saveSubplot(subplot: SubplotEntity) {
        viewModelScope.launch { repository.saveSubplot(subplot) }
    }

    fun deleteSubplot(subplot: SubplotEntity) {
        viewModelScope.launch { repository.deleteSubplot(subplot) }
    }

    // --- Notes ---
    fun saveNote(note: StoryNoteEntity) {
        viewModelScope.launch { repository.saveNote(note) }
    }

    fun deleteNote(note: StoryNoteEntity) {
        viewModelScope.launch { repository.deleteNote(note) }
    }

    // --- Goals ---
    fun saveGoal(goal: WritingGoalEntity) {
        viewModelScope.launch { repository.saveGoal(goal) }
    }

    fun deleteGoal(goal: WritingGoalEntity) {
        viewModelScope.launch { repository.deleteGoal(goal) }
    }

    // --- Settings ---
    fun saveSettings(settings: ProjectSettingsEntity) {
        _projectSettings.value = settings
        viewModelScope.launch { repository.saveSettings(settings) }
    }

    // --- Book Compilation ---
    fun compileBook(): CompiledManuscript? {
        val project = _currentProject.value ?: return null
        val settings = _projectSettings.value ?: ProjectSettingsEntity(projectId = project.id)
        val compiled = BookCompiler.compile(
            project = project,
            settings = settings,
            acts = _acts.value,
            chapters = _chapters.value,
            scenes = _scenes.value
        )
        _compiledManuscript.value = compiled
        return compiled
    }

    // --- Export Operations ---
    fun exportManuscript(context: Context, format: String, onComplete: (File) -> Unit) {
        val manuscript = compileBook() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val safeTitle = manuscript.project.title.replace(Regex("[^a-zA-Z0-9_]"), "_").lowercase()
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }

            val file = when (format.uppercase(Locale.US)) {
                "EPUB" -> {
                    val target = File(exportDir, "$safeTitle.epub")
                    FileOutputStream(target).use { fos ->
                        ManuscriptExporter.exportToEpub(manuscript, fos)
                    }
                    target
                }
                "PDF" -> {
                    val target = File(exportDir, "$safeTitle.pdf")
                    FileOutputStream(target).use { fos ->
                        ManuscriptExporter.exportToPdf(manuscript, fos)
                    }
                    target
                }
                "HTML" -> {
                    val target = File(exportDir, "$safeTitle.html")
                    target.writeText(ManuscriptExporter.exportToHtml(manuscript), Charsets.UTF_8)
                    target
                }
                "RTF" -> {
                    val target = File(exportDir, "$safeTitle.rtf")
                    target.writeText(ManuscriptExporter.exportToRtf(manuscript), Charsets.UTF_8)
                    target
                }
                else -> { // TXT
                    val target = File(exportDir, "$safeTitle.txt")
                    target.writeText(ManuscriptExporter.exportToTxt(manuscript), Charsets.UTF_8)
                    target
                }
            }

            withContext(Dispatchers.Main) {
                exportStatusMessage.value = "Successfully compiled ${manuscript.totalWords} words into $format."
                onComplete(file)
            }
        }
    }

    // --- Backup & Restore ---
    fun backupProject(context: Context, onComplete: (File) -> Unit) {
        val project = _currentProject.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val json = ManuscriptExporter.backupProjectToJson(db, project.id)
            val backupDir = File(context.cacheDir, "backups").apply { mkdirs() }
            val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(backupDir, "${project.title.replace(" ", "_")}_backup_$time.json")
            file.writeText(json, Charsets.UTF_8)

            withContext(Dispatchers.Main) {
                exportStatusMessage.value = "Backup created: ${file.name}"
                onComplete(file)
            }
        }
    }

    fun restoreProject(jsonString: String, onRestored: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val restored = ManuscriptExporter.restoreProjectFromJson(db, jsonString)
            withContext(Dispatchers.Main) {
                if (restored != null) {
                    exportStatusMessage.value = "Project '${restored.title}' restored successfully."
                    selectProject(restored.id)
                    onRestored(restored.id)
                } else {
                    exportStatusMessage.value = "Failed to restore project from backup file."
                }
            }
        }
    }

    fun importManuscript(title: String, text: String, onImported: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val project = ManuscriptExporter.importManuscriptFromText(db, title, text)
            withContext(Dispatchers.Main) {
                selectProject(project.id)
                onImported(project.id)
            }
        }
    }

    // --- Assistant Queries ---
    fun askAssistant(prompt: String) {
        val project = _currentProject.value ?: return
        assistantLoading.value = true
        assistantResponse.value = null

        viewModelScope.launch {
            val resp = AssistantService.generateAssistance(
                prompt = prompt,
                project = project,
                characters = _characters.value,
                worldEntries = _worldEntries.value,
                subplots = _subplots.value,
                currentScene = _activeScene.value
            )
            assistantLoading.value = false
            assistantResponse.value = resp
        }
    }
    val currentTab = currentWorkspaceTab.asStateFlow()

    fun selectTab(tab: WorkspaceTab) {
        currentWorkspaceTab.value = tab
    }

    fun setActiveScene(scene: SceneEntity) {
        selectScene(scene)
    }

    fun closeProject() {
        selectProject(null)
    }

    fun saveProject(project: ProjectEntity) {
        updateProject(project)
    }

    fun updateSettings(settings: ProjectSettingsEntity) {
        saveSettings(settings)
    }

    fun restoreVersion(version: SceneVersionEntity) {
        restoreVersionSnapshot(version)
    }

    fun deleteVersion(version: SceneVersionEntity) {
        deleteVersionSnapshot(version)
    }

    fun reorderScene(scene: SceneEntity, newSortOrder: Int) {
        viewModelScope.launch {
            repository.saveScene(scene.copy(sortOrder = newSortOrder))
        }
    }
}

enum class WorkspaceTab {
    WRITE,
    ORGANIZE,
    CHARACTERS,
    WORLD,
    PLOT,
    NOTES,
    GOALS,
    STATS,
    EXPORT,
    ASSISTANT,
    SETTINGS,
    MORE
}
