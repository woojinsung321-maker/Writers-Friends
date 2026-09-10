package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ActDao
import com.example.data.dao.ChapterDao
import com.example.data.dao.CharacterDao
import com.example.data.dao.ProjectDao
import com.example.data.dao.ProjectSettingsDao
import com.example.data.dao.RelationshipDao
import com.example.data.dao.SceneDao
import com.example.data.dao.SceneVersionDao
import com.example.data.dao.StoryNoteDao
import com.example.data.dao.SubplotDao
import com.example.data.dao.WorldEntryDao
import com.example.data.dao.WritingGoalDao
import com.example.data.dao.WritingSessionDao
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

@Database(
    entities = [
        ProjectEntity::class,
        ActEntity::class,
        ChapterEntity::class,
        SceneEntity::class,
        CharacterEntity::class,
        WorldEntryEntity::class,
        RelationshipEntity::class,
        SubplotEntity::class,
        StoryNoteEntity::class,
        WritingGoalEntity::class,
        WritingSessionEntity::class,
        SceneVersionEntity::class,
        ProjectSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun actDao(): ActDao
    abstract fun chapterDao(): ChapterDao
    abstract fun sceneDao(): SceneDao
    abstract fun characterDao(): CharacterDao
    abstract fun worldEntryDao(): WorldEntryDao
    abstract fun relationshipDao(): RelationshipDao
    abstract fun subplotDao(): SubplotDao
    abstract fun storyNoteDao(): StoryNoteDao
    abstract fun writingGoalDao(): WritingGoalDao
    abstract fun writingSessionDao(): WritingSessionDao
    abstract fun sceneVersionDao(): SceneVersionDao
    abstract fun projectSettingsDao(): ProjectSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "novel_writer.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
