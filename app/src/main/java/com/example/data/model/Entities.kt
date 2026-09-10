package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "projects",
    indices = [Index(value = ["updatedAt"])]
)
data class ProjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val author: String = "",
    val description: String = "",
    val coverColor: Long = 0xFF1E293B,
    val coverIcon: String = "book", // "book", "pen", "scroll", "feather", "castle", "sword", "compass", "star"
    val coverImageUri: String? = null,
    val status: String = "In Progress", // "Draft", "In Progress", "Revising", "Completed", "Archived"
    val isArchived: Boolean = false,
    val targetWordCount: Int = 50000,
    val deadlineEpochDay: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "acts",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId"), Index("sortOrder")]
)
data class ActEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val title: String,
    val sortOrder: Int = 0,
    val description: String = ""
)

@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId"), Index("actId"), Index("sortOrder")]
)
data class ChapterEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val actId: String? = null, // optional parent act
    val title: String,
    val chapterNumber: Int = 1,
    val sortOrder: Int = 0,
    val summary: String = "",
    val notes: String = "",
    val status: String = "To Do", // "To Do", "First Draft", "Revised", "Final Draft", "Done"
    val isTitleHidden: Boolean = false
)

@Entity(
    tableName = "scenes",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId"), Index("chapterId"), Index("sortOrder"), Index("subplotId")]
)
data class SceneEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val chapterId: String,
    val title: String,
    val body: String = "",
    val sortOrder: Int = 0,
    val status: String = "To Do", // "To Do", "First Draft", "Revised", "Final Draft", "Done"
    val povCharacterId: String? = null,
    val povCharacterName: String? = null,
    val locationId: String? = null,
    val locationName: String? = null,
    val timeSetting: String? = null,
    val charactersPresent: String = "", // comma-separated names
    val subplotId: String? = null,
    val subplotName: String? = null,
    val summary: String = "",
    val purpose: String = "",
    val notes: String = "",
    val wordCount: Int = 0,
    val characterCount: Int = 0,
    val timelineDate: String? = null,
    val timelineOrder: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId"), Index("name")]
)
data class CharacterEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val name: String,
    val nickname: String = "",
    val age: String = "",
    val gender: String = "",
    val appearance: String = "",
    val personality: String = "",
    val occupation: String = "",
    val background: String = "",
    val biography: String = "",
    val goals: String = "",
    val motivation: String = "",
    val fears: String = "",
    val strengths: String = "",
    val weaknesses: String = "",
    val secrets: String = "",
    val characterArc: String = "",
    val internalConflict: String = "",
    val externalConflict: String = "",
    val development: String = "",
    val notes: String = "",
    val tags: String = "",
    val color: Long = 0xFFB45309,
    val imageUri: String? = null,
    val customFields: String = "[]", // JSON array of custom field objects or JSON map
    val updatedAt: Long = System.currentTimeMillis(),

    // Extended Character Attributes
    val aliases: String = "",
    val dateOfBirth: String = "",
    val pronouns: String = "",
    val roleInStory: String = "",
    val species: String = "",
    val nationality: String = "",
    val currentLocation: String = "",
    val status: String = "Alive", // Alive, Deceased, Missing, Unknown, Inactive, Transformed
    val shortDescription: String = "",
    val imageDisplayMode: String = "cover", // cover, contain, fill
    val galleryJson: String = "[]", // JSON array of CharacterGalleryItem
    val appearanceDataJson: String = "{}", // CharacterAppearanceData
    val personalityDataJson: String = "{}", // CharacterPersonalityData
    val psychologyDataJson: String = "{}", // CharacterPsychologyData
    val historyDataJson: String = "{}", // CharacterHistoryData
    val goalsDataJson: String = "[]", // JSON array of CharacterGoalItem
    val arcDataJson: String = "{}", // CharacterArcData
    val voiceDataJson: String = "{}", // CharacterVoiceData
    val knowledgeDataJson: String = "{}", // CharacterKnowledgeData
    val inventoryDataJson: String = "[]", // JSON array of CharacterInventoryItem
    val locationsDataJson: String = "{}", // CharacterLocationsData
    val customNotesJson: String = "", // Free-form unrestricted notes
    val templateName: String = ""
)

@Entity(
    tableName = "character_templates",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class CharacterTemplateEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val name: String,
    val description: String = "",
    val templateJson: String = "{}",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "world_entries",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId"), Index("category")]
)
data class WorldEntryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val category: String, // "Location", "Faction", "Culture", "Religion", "Magic system", "Technology", "History", "Prop/Object", "Rule", "Event", "Custom"
    val title: String,
    val description: String = "",
    val notes: String = "",
    val tags: String = "",
    val color: Long = 0xFF4338CA,
    val imageUri: String? = null,
    val customFields: String = "{}",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "relationships",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId"), Index("sourceId"), Index("targetId")]
)
data class RelationshipEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val sourceId: String,
    val sourceName: String,
    val sourceType: String = "Character", // "Character", "Location", "Faction", "Prop", "Event", "Subplot"
    val targetId: String,
    val targetName: String,
    val targetType: String = "Character",
    val relationType: String = "Friend", // "Friend", "Enemy", "Family", "Romantic", "Rival", "Mentor", "Student", "Employer", "Member", "Leader", "Custom"
    val customRelationLabel: String? = null,
    val description: String = "",
    val strength: String = "Moderate", // "Weak", "Moderate", "Strong", "Complicated"
    val notes: String = ""
)

@Entity(
    tableName = "subplots",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class SubplotEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val name: String,
    val description: String = "",
    val mainCharacters: String = "",
    val status: String = "Active", // "Planned", "Active", "Resolved", "Abandoned"
    val progress: Int = 0, // 0 to 100
    val notes: String = "",
    val color: Long = 0xFF8B5CF6
)

@Entity(
    tableName = "story_notes",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId"), Index("category")]
)
data class StoryNoteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val title: String,
    val content: String = "",
    val category: String = "General", // "General", "Research", "Idea", "Checklist", "Plot Hole", "Revision"
    val isChecklist: Boolean = false,
    val checklistData: String = "", // JSON or newline separated items with [x] / [ ]
    val tags: String = "",
    val linkedEntityType: String? = null,
    val linkedEntityId: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "writing_goals",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class WritingGoalEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val title: String,
    val type: String = "TOTAL_WORDS", // "TOTAL_WORDS", "DAILY_WORDS", "DEADLINE", "CHAPTER_COUNT", "SCENE_COUNT"
    val targetValue: Int = 50000,
    val currentProgress: Int = 0,
    val deadlineEpochDay: Long? = null,
    val isCompleted: Boolean = false
)

@Entity(
    tableName = "writing_sessions",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId"), Index("dateEpochDay")]
)
data class WritingSessionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val dateEpochDay: Long, // LocalDate.toEpochDay()
    val wordsWritten: Int = 0,
    val durationMinutes: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "scene_versions",
    foreignKeys = [
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["sceneId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sceneId"), Index("createdAt")]
)
data class SceneVersionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val sceneId: String,
    val projectId: String,
    val name: String,
    val notes: String = "",
    val bodySnapshot: String,
    val wordCount: Int,
    val isStarred: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "project_settings",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class ProjectSettingsEntity(
    @PrimaryKey val projectId: String,
    val defaultFontFamily: String = "Serif", // "Serif", "Sans", "Monospace"
    val fontSizeSp: Float = 16f,
    val lineSpacing: Float = 1.6f,
    val paragraphSpacing: Float = 12f,
    val autosaveIntervalSeconds: Int = 5,
    val defaultExportFormat: String = "EPUB",
    val exportIncludeFrontMatter: Boolean = true,
    val exportChapterNumbering: Boolean = true,
    val exportSceneSeparator: String = "* * *",
    val copyrightHolder: String = "",
    val dedication: String = "",
    val themeMode: String = "SYSTEM" // "LIGHT", "DARK", "SYSTEM"
)
