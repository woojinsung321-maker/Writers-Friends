package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.ActEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.CharacterEntity
import com.example.data.model.CharacterTemplateEntity
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
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.UUID

class NovelRepository(private val db: AppDatabase) {

    // --- Projects ---
    val activeProjects: Flow<List<ProjectEntity>> = db.projectDao().getActiveProjects()
    val archivedProjects: Flow<List<ProjectEntity>> = db.projectDao().getArchivedProjects()

    fun getProject(id: String): Flow<ProjectEntity?> = db.projectDao().getProjectByIdFlow(id)
    suspend fun getProjectSync(id: String): ProjectEntity? = db.projectDao().getProjectById(id)

    suspend fun createProject(
        title: String,
        author: String = "",
        description: String = "",
        targetWords: Int = 50000,
        coverColor: Long = 0xFF1E293B,
        coverIcon: String = "book"
    ): ProjectEntity {
        val project = ProjectEntity(
            title = title.trim(),
            author = author.trim(),
            description = description.trim(),
            targetWordCount = targetWords,
            coverColor = coverColor,
            coverIcon = coverIcon
        )
        db.projectDao().insertProject(project)

        // Initialize default project settings
        db.projectSettingsDao().insertOrUpdateSettings(
            ProjectSettingsEntity(projectId = project.id)
        )

        // Initialize default goal
        db.writingGoalDao().insertGoal(
            WritingGoalEntity(
                projectId = project.id,
                title = "Complete First Draft",
                type = "TOTAL_WORDS",
                targetValue = targetWords
            )
        )

        // Create an initial Act 1 and Chapter 1 so the user can immediately write
        val act = ActEntity(
            projectId = project.id,
            title = "Act I: The Beginning",
            sortOrder = 0,
            description = "Setup and inciting incident"
        )
        db.actDao().insertAct(act)

        val chapter = ChapterEntity(
            projectId = project.id,
            actId = act.id,
            title = "Chapter 1",
            chapterNumber = 1,
            sortOrder = 0,
            summary = "The opening hook and introduction"
        )
        db.chapterDao().insertChapter(chapter)

        val scene = SceneEntity(
            projectId = project.id,
            chapterId = chapter.id,
            title = "Scene 1",
            sortOrder = 0,
            status = "To Do",
            body = "",
            summary = "Opening moments"
        )
        db.sceneDao().insertScene(scene)

        return project
    }

    suspend fun updateProject(project: ProjectEntity) {
        db.projectDao().updateProject(project.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun setArchived(id: String, archived: Boolean) {
        db.projectDao().setArchived(id, archived)
    }

    suspend fun deleteProject(id: String) {
        db.projectDao().deleteProjectById(id)
    }

    suspend fun duplicateProject(projectId: String): ProjectEntity? {
        val original = db.projectDao().getProjectById(projectId) ?: return null
        val newProjectId = UUID.randomUUID().toString()
        val duplicated = original.copy(
            id = newProjectId,
            title = "${original.title} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        db.projectDao().insertProject(duplicated)

        // Duplicate settings
        val settings = db.projectSettingsDao().getSettings(projectId)
        if (settings != null) {
            db.projectSettingsDao().insertOrUpdateSettings(settings.copy(projectId = newProjectId))
        }

        // Duplicate acts
        val acts = db.actDao().getActsForProjectSync(projectId)
        val actMap = mutableMapOf<String, String>()
        for (act in acts) {
            val newActId = UUID.randomUUID().toString()
            actMap[act.id] = newActId
            db.actDao().insertAct(act.copy(id = newActId, projectId = newProjectId))
        }

        // Duplicate chapters & scenes
        val chapters = db.chapterDao().getChaptersForProjectSync(projectId)
        for (ch in chapters) {
            val newChapterId = UUID.randomUUID().toString()
            val newActId = ch.actId?.let { actMap[it] }
            db.chapterDao().insertChapter(ch.copy(id = newChapterId, projectId = newProjectId, actId = newActId))

            val scenes = db.sceneDao().getScenesForChapterSync(ch.id)
            for (sc in scenes) {
                db.sceneDao().insertScene(sc.copy(id = UUID.randomUUID().toString(), projectId = newProjectId, chapterId = newChapterId))
            }
        }

        // Duplicate characters
        val characters = db.characterDao().getCharactersForProjectSync(projectId)
        for (c in characters) {
            db.characterDao().insertCharacter(c.copy(id = UUID.randomUUID().toString(), projectId = newProjectId))
        }

        // Duplicate character templates
        val templates = db.characterTemplateDao().getTemplatesForProjectSync(projectId)
        for (t in templates) {
            db.characterTemplateDao().insertTemplate(t.copy(id = UUID.randomUUID().toString(), projectId = newProjectId))
        }

        // Duplicate world entries
        val world = db.worldEntryDao().getWorldEntriesForProjectSync(projectId)
        for (w in world) {
            db.worldEntryDao().insertWorldEntry(w.copy(id = UUID.randomUUID().toString(), projectId = newProjectId))
        }

        // Duplicate subplots
        val subplots = db.subplotDao().getSubplotsForProjectSync(projectId)
        for (sp in subplots) {
            db.subplotDao().insertSubplot(sp.copy(id = UUID.randomUUID().toString(), projectId = newProjectId))
        }

        return duplicated
    }

    // --- Acts & Chapters & Scenes ---
    fun getActs(projectId: String): Flow<List<ActEntity>> = db.actDao().getActsForProject(projectId)
    suspend fun getActsSync(projectId: String): List<ActEntity> = db.actDao().getActsForProjectSync(projectId)
    suspend fun saveAct(act: ActEntity) {
        db.actDao().insertAct(act)
        db.projectDao().touchProject(act.projectId)
    }
    suspend fun deleteAct(act: ActEntity) {
        db.actDao().deleteAct(act)
        db.projectDao().touchProject(act.projectId)
    }

    fun getChapters(projectId: String): Flow<List<ChapterEntity>> = db.chapterDao().getChaptersForProject(projectId)
    suspend fun getChaptersSync(projectId: String): List<ChapterEntity> = db.chapterDao().getChaptersForProjectSync(projectId)
    fun getChapter(id: String): Flow<ChapterEntity?> = db.chapterDao().getChapterByIdFlow(id)
    suspend fun getChapterSync(id: String): ChapterEntity? = db.chapterDao().getChapterById(id)
    suspend fun saveChapter(chapter: ChapterEntity) {
        db.chapterDao().insertChapter(chapter)
        db.projectDao().touchProject(chapter.projectId)
    }
    suspend fun deleteChapter(chapterId: String, projectId: String) {
        db.chapterDao().deleteChapterById(chapterId)
        db.projectDao().touchProject(projectId)
    }

    fun getAllScenes(projectId: String): Flow<List<SceneEntity>> = db.sceneDao().getAllScenesForProject(projectId)
    suspend fun getAllScenesSync(projectId: String): List<SceneEntity> = db.sceneDao().getAllScenesForProjectSync(projectId)
    fun getScenesForChapter(chapterId: String): Flow<List<SceneEntity>> = db.sceneDao().getScenesForChapter(chapterId)
    fun getScene(id: String): Flow<SceneEntity?> = db.sceneDao().getSceneByIdFlow(id)
    suspend fun getSceneSync(id: String): SceneEntity? = db.sceneDao().getSceneById(id)

    suspend fun saveScene(scene: SceneEntity) {
        db.sceneDao().insertScene(scene)
        db.projectDao().touchProject(scene.projectId)
    }

    suspend fun updateSceneContent(sceneId: String, projectId: String, body: String, wordCount: Int, characterCount: Int) {
        db.sceneDao().updateSceneContent(
            id = sceneId,
            body = body,
            wordCount = wordCount,
            characterCount = characterCount,
            updatedAt = System.currentTimeMillis()
        )
        db.projectDao().touchProject(projectId)
    }

    suspend fun moveScene(sceneId: String, newChapterId: String, newSortOrder: Int, projectId: String) {
        db.sceneDao().moveScene(sceneId, newChapterId, newSortOrder)
        db.projectDao().touchProject(projectId)
    }

    suspend fun deleteScene(sceneId: String, projectId: String) {
        db.sceneDao().deleteSceneById(sceneId)
        db.projectDao().touchProject(projectId)
    }

    fun getTotalWordCount(projectId: String): Flow<Int> = db.sceneDao().getTotalWordCountFlow(projectId)

    // --- Characters ---
    fun getCharacters(projectId: String): Flow<List<CharacterEntity>> = db.characterDao().getCharactersForProject(projectId)
    suspend fun getCharactersSync(projectId: String): List<CharacterEntity> = db.characterDao().getCharactersForProjectSync(projectId)
    fun getCharacter(id: String): Flow<CharacterEntity?> = db.characterDao().getCharacterByIdFlow(id)
    suspend fun saveCharacter(character: CharacterEntity) {
        db.characterDao().insertCharacter(character)
        db.projectDao().touchProject(character.projectId)
    }
    suspend fun deleteCharacter(character: CharacterEntity) {
        db.characterDao().deleteCharacter(character)
        db.projectDao().touchProject(character.projectId)
    }

    // --- Character Templates ---
    fun getCharacterTemplates(projectId: String): Flow<List<CharacterTemplateEntity>> = db.characterTemplateDao().getTemplatesForProject(projectId)
    suspend fun getCharacterTemplatesSync(projectId: String): List<CharacterTemplateEntity> = db.characterTemplateDao().getTemplatesForProjectSync(projectId)
    suspend fun saveCharacterTemplate(template: CharacterTemplateEntity) {
        db.characterTemplateDao().insertTemplate(template)
        db.projectDao().touchProject(template.projectId)
    }
    suspend fun deleteCharacterTemplate(template: CharacterTemplateEntity) {
        db.characterTemplateDao().deleteTemplate(template)
        db.projectDao().touchProject(template.projectId)
    }

    // --- World Entries ---
    fun getWorldEntries(projectId: String): Flow<List<WorldEntryEntity>> = db.worldEntryDao().getWorldEntriesForProject(projectId)
    suspend fun getWorldEntriesSync(projectId: String): List<WorldEntryEntity> = db.worldEntryDao().getWorldEntriesForProjectSync(projectId)
    fun getWorldEntry(id: String): Flow<WorldEntryEntity?> = db.worldEntryDao().getWorldEntryByIdFlow(id)
    suspend fun saveWorldEntry(entry: WorldEntryEntity) {
        db.worldEntryDao().insertWorldEntry(entry)
        db.projectDao().touchProject(entry.projectId)
    }
    suspend fun deleteWorldEntry(entry: WorldEntryEntity) {
        db.worldEntryDao().deleteWorldEntry(entry)
        db.projectDao().touchProject(entry.projectId)
    }

    // --- Relationships ---
    fun getRelationships(projectId: String): Flow<List<RelationshipEntity>> = db.relationshipDao().getRelationshipsForProject(projectId)
    suspend fun getRelationshipsSync(projectId: String): List<RelationshipEntity> = db.relationshipDao().getRelationshipsForProjectSync(projectId)
    fun getRelationshipsForEntity(projectId: String, entityId: String): Flow<List<RelationshipEntity>> =
        db.relationshipDao().getRelationshipsForEntity(projectId, entityId)
    suspend fun saveRelationship(relationship: RelationshipEntity) {
        db.relationshipDao().insertRelationship(relationship)
        db.projectDao().touchProject(relationship.projectId)
    }
    suspend fun deleteRelationship(relationship: RelationshipEntity) {
        db.relationshipDao().deleteRelationship(relationship)
        db.projectDao().touchProject(relationship.projectId)
    }

    // --- Subplots ---
    fun getSubplots(projectId: String): Flow<List<SubplotEntity>> = db.subplotDao().getSubplotsForProject(projectId)
    suspend fun getSubplotsSync(projectId: String): List<SubplotEntity> = db.subplotDao().getSubplotsForProjectSync(projectId)
    suspend fun saveSubplot(subplot: SubplotEntity) {
        db.subplotDao().insertSubplot(subplot)
        db.projectDao().touchProject(subplot.projectId)
    }
    suspend fun deleteSubplot(subplot: SubplotEntity) {
        db.subplotDao().deleteSubplot(subplot)
        db.projectDao().touchProject(subplot.projectId)
    }

    // --- Notes ---
    fun getNotes(projectId: String): Flow<List<StoryNoteEntity>> = db.storyNoteDao().getNotesForProject(projectId)
    suspend fun getNotesSync(projectId: String): List<StoryNoteEntity> = db.storyNoteDao().getNotesForProjectSync(projectId)
    suspend fun saveNote(note: StoryNoteEntity) {
        db.storyNoteDao().insertNote(note)
        db.projectDao().touchProject(note.projectId)
    }
    suspend fun deleteNote(note: StoryNoteEntity) {
        db.storyNoteDao().deleteNote(note)
        db.projectDao().touchProject(note.projectId)
    }

    // --- Goals & Sessions ---
    fun getGoals(projectId: String): Flow<List<WritingGoalEntity>> = db.writingGoalDao().getGoalsForProject(projectId)
    suspend fun saveGoal(goal: WritingGoalEntity) {
        db.writingGoalDao().insertGoal(goal)
    }
    suspend fun deleteGoal(goal: WritingGoalEntity) {
        db.writingGoalDao().deleteGoal(goal)
    }

    fun getSessions(projectId: String): Flow<List<WritingSessionEntity>> = db.writingSessionDao().getSessionsForProject(projectId)
    suspend fun getSessionsSync(projectId: String): List<WritingSessionEntity> = db.writingSessionDao().getSessionsForProjectSync(projectId)

    suspend fun recordWritingProgress(projectId: String, deltaWords: Int) {
        if (deltaWords <= 0) return
        val todayEpochDay = getTodayEpochDay()
        val existingSession = db.writingSessionDao().getSessionByDay(projectId, todayEpochDay)
        if (existingSession != null) {
            db.writingSessionDao().insertSession(
                existingSession.copy(
                    wordsWritten = existingSession.wordsWritten + deltaWords,
                    durationMinutes = existingSession.durationMinutes + 1,
                    timestamp = System.currentTimeMillis()
                )
            )
        } else {
            db.writingSessionDao().insertSession(
                WritingSessionEntity(
                    projectId = projectId,
                    dateEpochDay = todayEpochDay,
                    wordsWritten = deltaWords,
                    durationMinutes = 1
                )
            )
        }
    }

    fun getWordsWrittenToday(projectId: String): Flow<Int> {
        val todayEpochDay = getTodayEpochDay()
        return db.writingSessionDao().getWordsWrittenTodayFlow(projectId, todayEpochDay)
    }

    // --- Versions ---
    fun getSceneVersions(sceneId: String): Flow<List<SceneVersionEntity>> = db.sceneVersionDao().getVersionsForScene(sceneId)
    suspend fun createSceneVersion(sceneId: String, projectId: String, name: String, notes: String, body: String, wordCount: Int) {
        val version = SceneVersionEntity(
            sceneId = sceneId,
            projectId = projectId,
            name = name.ifBlank { "Version ${System.currentTimeMillis() / 1000}" },
            notes = notes,
            bodySnapshot = body,
            wordCount = wordCount
        )
        db.sceneVersionDao().insertVersion(version)
    }
    suspend fun setVersionStarred(id: String, starred: Boolean) {
        db.sceneVersionDao().setVersionStarred(id, starred)
    }
    suspend fun deleteVersion(version: SceneVersionEntity) {
        db.sceneVersionDao().deleteVersion(version)
    }

    // --- Settings ---
    fun getSettings(projectId: String): Flow<ProjectSettingsEntity?> = db.projectSettingsDao().getSettingsFlow(projectId)
    suspend fun getSettingsSync(projectId: String): ProjectSettingsEntity? = db.projectSettingsDao().getSettings(projectId)
    suspend fun saveSettings(settings: ProjectSettingsEntity) {
        db.projectSettingsDao().insertOrUpdateSettings(settings)
    }

    private fun getTodayEpochDay(): Long {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        // Approximate epoch day formula:
        return (year * 365.25 + month * 30.6 + day).toLong()
    }
}
