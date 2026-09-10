package com.example.compiler

import com.example.data.model.ActEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectSettingsEntity
import com.example.data.model.SceneEntity

data class CompiledChapter(
    val chapter: ChapterEntity,
    val actTitle: String?,
    val scenes: List<SceneEntity>,
    val formattedContent: String,
    val wordCount: Int
)

data class CompiledManuscript(
    val project: ProjectEntity,
    val settings: ProjectSettingsEntity,
    val chapters: List<CompiledChapter>,
    val totalWords: Int,
    val totalCharacters: Int,
    val estimatedReadTimeMinutes: Int,
    val tableOfContents: List<String>
)

object BookCompiler {

    fun compile(
        project: ProjectEntity,
        settings: ProjectSettingsEntity,
        acts: List<ActEntity>,
        chapters: List<ChapterEntity>,
        scenes: List<SceneEntity>
    ): CompiledManuscript {
        val actMap = acts.associateBy { it.id }
        val scenesByChapter = scenes.groupBy { it.chapterId }

        val compiledChapters = mutableListOf<CompiledChapter>()
        var totalWords = 0
        var totalChars = 0
        val toc = mutableListOf<String>()

        val sortedChapters = chapters.sortedWith(compareBy({ it.sortOrder }, { it.chapterNumber }))

        for (chapter in sortedChapters) {
            val chapterScenes = (scenesByChapter[chapter.id] ?: emptyList()).sortedBy { it.sortOrder }
            val act = chapter.actId?.let { actMap[it] }

            val separator = when (settings.exportSceneSeparator) {
                "***" -> "\n\n* * *\n\n"
                "# " -> "\n\n#\n\n"
                "Blank Line" -> "\n\n\n\n"
                else -> "\n\n* * *\n\n"
            }

            val chapterBody = chapterScenes.joinToString(separator = separator) { scene ->
                scene.body.trim()
            }.trim()

            val chapterWordCount = chapterScenes.sumOf { it.wordCount }
            totalWords += chapterWordCount
            totalChars += chapterScenes.sumOf { it.characterCount }

            val chapterHeading = buildChapterHeading(chapter, settings)
            toc.add(chapterHeading)

            compiledChapters.add(
                CompiledChapter(
                    chapter = chapter,
                    actTitle = act?.title,
                    scenes = chapterScenes,
                    formattedContent = chapterBody,
                    wordCount = chapterWordCount
                )
            )
        }

        val estimatedReadTime = if (totalWords > 0) (totalWords / 225).coerceAtLeast(1) else 0

        return CompiledManuscript(
            project = project,
            settings = settings,
            chapters = compiledChapters,
            totalWords = totalWords,
            totalCharacters = totalChars,
            estimatedReadTimeMinutes = estimatedReadTime,
            tableOfContents = toc
        )
    }

    private fun buildChapterHeading(chapter: ChapterEntity, settings: ProjectSettingsEntity): String {
        return when {
            settings.exportChapterNumbering && chapter.title.isNotBlank() ->
                "Chapter ${chapter.chapterNumber}: ${chapter.title}"
            settings.exportChapterNumbering ->
                "Chapter ${chapter.chapterNumber}"
            chapter.title.isNotBlank() ->
                chapter.title
            else ->
                "Chapter ${chapter.chapterNumber}"
        }
    }
}
