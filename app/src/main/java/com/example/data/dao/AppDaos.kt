package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getActiveProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun getProjectByIdFlow(id: String): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("UPDATE projects SET updatedAt = :timestamp WHERE id = :id")
    suspend fun touchProject(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE projects SET isArchived = :archived, updatedAt = :timestamp WHERE id = :id")
    suspend fun setArchived(id: String, archived: Boolean, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)
}

@Dao
interface ActDao {
    @Query("SELECT * FROM acts WHERE projectId = :projectId ORDER BY sortOrder ASC")
    fun getActsForProject(projectId: String): Flow<List<ActEntity>>

    @Query("SELECT * FROM acts WHERE projectId = :projectId ORDER BY sortOrder ASC")
    suspend fun getActsForProjectSync(projectId: String): List<ActEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAct(act: ActEntity)

    @Update
    suspend fun updateAct(act: ActEntity)

    @Delete
    suspend fun deleteAct(act: ActEntity)
}

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE projectId = :projectId ORDER BY sortOrder ASC, chapterNumber ASC")
    fun getChaptersForProject(projectId: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE projectId = :projectId ORDER BY sortOrder ASC, chapterNumber ASC")
    suspend fun getChaptersForProjectSync(projectId: String): List<ChapterEntity>

    @Query("SELECT * FROM chapters WHERE id = :id LIMIT 1")
    suspend fun getChapterById(id: String): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE id = :id LIMIT 1")
    fun getChapterByIdFlow(id: String): Flow<ChapterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity)

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Delete
    suspend fun deleteChapter(chapter: ChapterEntity)

    @Query("DELETE FROM chapters WHERE id = :id")
    suspend fun deleteChapterById(id: String)
}

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes WHERE projectId = :projectId ORDER BY sortOrder ASC")
    fun getAllScenesForProject(projectId: String): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE projectId = :projectId ORDER BY sortOrder ASC")
    suspend fun getAllScenesForProjectSync(projectId: String): List<SceneEntity>

    @Query("SELECT * FROM scenes WHERE chapterId = :chapterId ORDER BY sortOrder ASC")
    fun getScenesForChapter(chapterId: String): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE chapterId = :chapterId ORDER BY sortOrder ASC")
    suspend fun getScenesForChapterSync(chapterId: String): List<SceneEntity>

    @Query("SELECT * FROM scenes WHERE id = :id LIMIT 1")
    fun getSceneByIdFlow(id: String): Flow<SceneEntity?>

    @Query("SELECT * FROM scenes WHERE id = :id LIMIT 1")
    suspend fun getSceneById(id: String): SceneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScene(scene: SceneEntity)

    @Update
    suspend fun updateScene(scene: SceneEntity)

    @Query("UPDATE scenes SET body = :body, wordCount = :wordCount, characterCount = :characterCount, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSceneContent(id: String, body: String, wordCount: Int, characterCount: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE scenes SET chapterId = :chapterId, sortOrder = :sortOrder WHERE id = :id")
    suspend fun moveScene(id: String, chapterId: String, sortOrder: Int)

    @Delete
    suspend fun deleteScene(scene: SceneEntity)

    @Query("DELETE FROM scenes WHERE id = :id")
    suspend fun deleteSceneById(id: String)

    @Query("SELECT COALESCE(SUM(wordCount), 0) FROM scenes WHERE projectId = :projectId")
    fun getTotalWordCountFlow(projectId: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(wordCount), 0) FROM scenes WHERE projectId = :projectId")
    suspend fun getTotalWordCountSync(projectId: String): Int
}

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters WHERE projectId = :projectId ORDER BY name ASC")
    fun getCharactersForProject(projectId: String): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE projectId = :projectId ORDER BY name ASC")
    suspend fun getCharactersForProjectSync(projectId: String): List<CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    fun getCharacterByIdFlow(id: String): Flow<CharacterEntity?>

    @Query("SELECT * FROM characters WHERE id = :id LIMIT 1")
    suspend fun getCharacterById(id: String): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)
}

@Dao
interface WorldEntryDao {
    @Query("SELECT * FROM world_entries WHERE projectId = :projectId ORDER BY title ASC")
    fun getWorldEntriesForProject(projectId: String): Flow<List<WorldEntryEntity>>

    @Query("SELECT * FROM world_entries WHERE projectId = :projectId ORDER BY title ASC")
    suspend fun getWorldEntriesForProjectSync(projectId: String): List<WorldEntryEntity>

    @Query("SELECT * FROM world_entries WHERE projectId = :projectId AND category = :category ORDER BY title ASC")
    fun getEntriesByCategory(projectId: String, category: String): Flow<List<WorldEntryEntity>>

    @Query("SELECT * FROM world_entries WHERE id = :id LIMIT 1")
    fun getWorldEntryByIdFlow(id: String): Flow<WorldEntryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorldEntry(entry: WorldEntryEntity)

    @Update
    suspend fun updateWorldEntry(entry: WorldEntryEntity)

    @Delete
    suspend fun deleteWorldEntry(entry: WorldEntryEntity)
}

@Dao
interface RelationshipDao {
    @Query("SELECT * FROM relationships WHERE projectId = :projectId")
    fun getRelationshipsForProject(projectId: String): Flow<List<RelationshipEntity>>

    @Query("SELECT * FROM relationships WHERE projectId = :projectId")
    suspend fun getRelationshipsForProjectSync(projectId: String): List<RelationshipEntity>

    @Query("SELECT * FROM relationships WHERE projectId = :projectId AND (sourceId = :entityId OR targetId = :entityId)")
    fun getRelationshipsForEntity(projectId: String, entityId: String): Flow<List<RelationshipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelationship(relationship: RelationshipEntity)

    @Update
    suspend fun updateRelationship(relationship: RelationshipEntity)

    @Delete
    suspend fun deleteRelationship(relationship: RelationshipEntity)
}

@Dao
interface SubplotDao {
    @Query("SELECT * FROM subplots WHERE projectId = :projectId ORDER BY name ASC")
    fun getSubplotsForProject(projectId: String): Flow<List<SubplotEntity>>

    @Query("SELECT * FROM subplots WHERE projectId = :projectId ORDER BY name ASC")
    suspend fun getSubplotsForProjectSync(projectId: String): List<SubplotEntity>

    @Query("SELECT * FROM subplots WHERE id = :id LIMIT 1")
    suspend fun getSubplotById(id: String): SubplotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubplot(subplot: SubplotEntity)

    @Update
    suspend fun updateSubplot(subplot: SubplotEntity)

    @Delete
    suspend fun deleteSubplot(subplot: SubplotEntity)
}

@Dao
interface StoryNoteDao {
    @Query("SELECT * FROM story_notes WHERE projectId = :projectId ORDER BY updatedAt DESC")
    fun getNotesForProject(projectId: String): Flow<List<StoryNoteEntity>>

    @Query("SELECT * FROM story_notes WHERE projectId = :projectId ORDER BY updatedAt DESC")
    suspend fun getNotesForProjectSync(projectId: String): List<StoryNoteEntity>

    @Query("SELECT * FROM story_notes WHERE projectId = :projectId AND category = :category ORDER BY updatedAt DESC")
    fun getNotesByCategory(projectId: String, category: String): Flow<List<StoryNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StoryNoteEntity)

    @Update
    suspend fun updateNote(note: StoryNoteEntity)

    @Delete
    suspend fun deleteNote(note: StoryNoteEntity)
}

@Dao
interface WritingGoalDao {
    @Query("SELECT * FROM writing_goals WHERE projectId = :projectId")
    fun getGoalsForProject(projectId: String): Flow<List<WritingGoalEntity>>

    @Query("SELECT * FROM writing_goals WHERE projectId = :projectId")
    suspend fun getGoalsForProjectSync(projectId: String): List<WritingGoalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: WritingGoalEntity)

    @Update
    suspend fun updateGoal(goal: WritingGoalEntity)

    @Delete
    suspend fun deleteGoal(goal: WritingGoalEntity)
}

@Dao
interface WritingSessionDao {
    @Query("SELECT * FROM writing_sessions WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getSessionsForProject(projectId: String): Flow<List<WritingSessionEntity>>

    @Query("SELECT * FROM writing_sessions WHERE projectId = :projectId ORDER BY timestamp DESC")
    suspend fun getSessionsForProjectSync(projectId: String): List<WritingSessionEntity>

    @Query("SELECT * FROM writing_sessions WHERE projectId = :projectId AND dateEpochDay = :dateEpochDay LIMIT 1")
    suspend fun getSessionByDay(projectId: String, dateEpochDay: Long): WritingSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WritingSessionEntity)

    @Query("SELECT COALESCE(SUM(wordsWritten), 0) FROM writing_sessions WHERE projectId = :projectId AND dateEpochDay = :dateEpochDay")
    fun getWordsWrittenTodayFlow(projectId: String, dateEpochDay: Long): Flow<Int>
}

@Dao
interface SceneVersionDao {
    @Query("SELECT * FROM scene_versions WHERE sceneId = :sceneId ORDER BY createdAt DESC")
    fun getVersionsForScene(sceneId: String): Flow<List<SceneVersionEntity>>

    @Query("SELECT * FROM scene_versions WHERE sceneId = :sceneId ORDER BY createdAt DESC")
    suspend fun getVersionsForSceneSync(sceneId: String): List<SceneVersionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVersion(version: SceneVersionEntity)

    @Query("UPDATE scene_versions SET isStarred = :starred WHERE id = :id")
    suspend fun setVersionStarred(id: String, starred: Boolean)

    @Delete
    suspend fun deleteVersion(version: SceneVersionEntity)
}

@Dao
interface ProjectSettingsDao {
    @Query("SELECT * FROM project_settings WHERE projectId = :projectId LIMIT 1")
    fun getSettingsFlow(projectId: String): Flow<ProjectSettingsEntity?>

    @Query("SELECT * FROM project_settings WHERE projectId = :projectId LIMIT 1")
    suspend fun getSettings(projectId: String): ProjectSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: ProjectSettingsEntity)
}
