package com.example.export

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.compiler.CompiledManuscript
import com.example.data.db.AppDatabase
import com.example.data.model.ActEntity
import com.example.data.model.ChapterEntity
import com.example.data.model.CharacterEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectSettingsEntity
import com.example.data.model.RelationshipEntity
import com.example.data.model.SceneEntity
import com.example.data.model.StoryNoteEntity
import com.example.data.model.SubplotEntity
import com.example.data.model.WorldEntryEntity
import com.example.data.model.WritingGoalEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ManuscriptExporter {

    /**
     * Generates a valid EPUB 3 file for the compiled manuscript.
     */
    fun exportToEpub(manuscript: CompiledManuscript, outputStream: OutputStream) {
        val zip = ZipOutputStream(outputStream)

        // 1. mimetype (MUST be first, uncompressed)
        val mimetypeBytes = "application/epub+zip".toByteArray(Charsets.US_ASCII)
        val mimeEntry = ZipEntry("mimetype").apply {
            method = ZipEntry.STORED
            size = mimetypeBytes.size.toLong()
            compressedSize = mimetypeBytes.size.toLong()
            crc = CRC32().apply { update(mimetypeBytes) }.value
        }
        zip.putNextEntry(mimeEntry)
        zip.write(mimetypeBytes)
        zip.closeEntry()

        // 2. META-INF/container.xml
        val containerXml = """<?xml version="1.0" encoding="UTF-8"?>
<container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container">
    <rootfiles>
        <rootfile full-path="OEBPS/content.opf" media-type="application/oebps-package+xml"/>
    </rootfiles>
</container>""".trimIndent()
        zip.putNextEntry(ZipEntry("META-INF/container.xml"))
        zip.write(containerXml.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 3. OEBPS/style.css
        val css = """
body {
    font-family: ${if (manuscript.settings.defaultFontFamily == "Serif") "Georgia, 'Times New Roman', serif" else "system-ui, -apple-system, sans-serif"};
    line-height: ${manuscript.settings.lineSpacing};
    margin: 5%;
    padding: 0;
    color: #111;
    background-color: #fff;
}
h1.title {
    text-align: center;
    margin-top: 30%;
    margin-bottom: 1em;
    font-size: 2.2em;
    font-weight: bold;
}
h2.author {
    text-align: center;
    font-style: italic;
    font-size: 1.4em;
    margin-bottom: 2em;
}
h2.chapter-title {
    text-align: center;
    margin-top: 2em;
    margin-bottom: 1.5em;
    font-size: 1.6em;
    page-break-before: always;
}
p {
    text-indent: 1.5em;
    margin-top: 0;
    margin-bottom: 0.5em;
    text-align: justify;
}
p.first, p.break-after {
    text-indent: 0;
}
.scene-break {
    text-align: center;
    margin: 1.5em 0;
    letter-spacing: 0.3em;
}
.dedication {
    text-align: center;
    font-style: italic;
    margin-top: 25%;
    margin-bottom: 25%;
}
.copyright {
    font-size: 0.9em;
    color: #555;
    margin-top: 20%;
}
nav#toc ol {
    list-style-type: none;
    padding-left: 0;
}
nav#toc li {
    margin-bottom: 0.8em;
}
""".trimIndent()
        zip.putNextEntry(ZipEntry("OEBPS/style.css"))
        zip.write(css.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 4. Title page
        val titleXhtml = """<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:epub="http://www.idpf.org/2007/ops">
<head>
    <title>${escapeXml(manuscript.project.title)}</title>
    <link rel="stylesheet" type="text/css" href="style.css"/>
</head>
<body>
    <h1 class="title">${escapeXml(manuscript.project.title)}</h1>
    ${if (manuscript.project.author.isNotBlank()) "<h2 class=\"author\">${escapeXml(manuscript.project.author)}</h2>" else ""}
    ${if (manuscript.settings.dedication.isNotBlank()) "<div class=\"dedication\"><p>${escapeXml(manuscript.settings.dedication)}</p></div>" else ""}
    ${if (manuscript.settings.exportIncludeFrontMatter) """
    <div class="copyright">
        <p>Copyright © ${SimpleDateFormat("yyyy", Locale.US).format(Date())} ${escapeXml(manuscript.settings.copyrightHolder.ifBlank { manuscript.project.author.ifBlank { "Author" } })}</p>
        <p>All rights reserved. Created with Novel Writer.</p>
    </div>
    """ else ""}
</body>
</html>""".trimIndent()
        zip.putNextEntry(ZipEntry("OEBPS/title.xhtml"))
        zip.write(titleXhtml.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 5. Chapters
        val chapterFiles = mutableListOf<String>()
        manuscript.chapters.forEachIndexed { index, ch ->
            val fileName = "chapter_${index + 1}.xhtml"
            chapterFiles.add(fileName)

            val chapterHeading = if (manuscript.settings.exportChapterNumbering) {
                if (ch.chapter.title.isNotBlank()) "Chapter ${ch.chapter.chapterNumber}: ${ch.chapter.title}"
                else "Chapter ${ch.chapter.chapterNumber}"
            } else {
                ch.chapter.title.ifBlank { "Chapter ${ch.chapter.chapterNumber}" }
            }

            val paragraphs = ch.formattedContent.split("\n\n").filter { it.isNotBlank() }
            val bodyHtml = buildString {
                for (p in paragraphs) {
                    val trimmed = p.trim()
                    if (trimmed == "* * *" || trimmed == "***" || trimmed == "#") {
                        append("<div class=\"scene-break\">* * *</div>\n")
                    } else {
                        val formattedParagraph = escapeXml(trimmed).replace("\n", "<br/>")
                        append("<p>$formattedParagraph</p>\n")
                    }
                }
            }

            val chapterXhtml = """<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:epub="http://www.idpf.org/2007/ops">
<head>
    <title>${escapeXml(chapterHeading)}</title>
    <link rel="stylesheet" type="text/css" href="style.css"/>
</head>
<body>
    ${if (ch.actTitle != null) "<p style=\"text-align:center;text-transform:uppercase;letter-spacing:0.1em;color:#666;\">${escapeXml(ch.actTitle)}</p>" else ""}
    <h2 class="chapter-title">${escapeXml(chapterHeading)}</h2>
    $bodyHtml
</body>
</html>""".trimIndent()

            zip.putNextEntry(ZipEntry("OEBPS/$fileName"))
            zip.write(chapterXhtml.toByteArray(Charsets.UTF_8))
            zip.closeEntry()
        }

        // 6. Navigation (nav.xhtml)
        val navXhtml = buildString {
            append("""<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:epub="http://www.idpf.org/2007/ops">
<head>
    <title>Table of Contents</title>
    <link rel="stylesheet" type="text/css" href="style.css"/>
</head>
<body>
    <nav epub:type="toc" id="toc">
        <h1>Table of Contents</h1>
        <ol>
            <li><a href="title.xhtml">Title Page</a></li>
""")
            manuscript.chapters.forEachIndexed { index, ch ->
                val title = if (manuscript.settings.exportChapterNumbering) {
                    if (ch.chapter.title.isNotBlank()) "Chapter ${ch.chapter.chapterNumber}: ${ch.chapter.title}"
                    else "Chapter ${ch.chapter.chapterNumber}"
                } else {
                    ch.chapter.title.ifBlank { "Chapter ${ch.chapter.chapterNumber}" }
                }
                append("            <li><a href=\"chapter_${index + 1}.xhtml\">${escapeXml(title)}</a></li>\n")
            }
            append("""        </ol>
    </nav>
</body>
</html>""")
        }
        zip.putNextEntry(ZipEntry("OEBPS/nav.xhtml"))
        zip.write(navXhtml.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 7. content.opf
        val bookUuid = manuscript.project.id
        val opf = buildString {
            append("""<?xml version="1.0" encoding="utf-8"?>
<package xmlns="http://www.idpf.org/2007/opf" unique-identifier="BookId" version="3.0">
    <metadata xmlns:dc="http://purl.org/dc/elements/1.1/">
        <dc:identifier id="BookId">urn:uuid:$bookUuid</dc:identifier>
        <dc:title>${escapeXml(manuscript.project.title)}</dc:title>
        <dc:creator>${escapeXml(manuscript.project.author.ifBlank { "Unknown Author" })}</dc:creator>
        <dc:language>en</dc:language>
        <meta property="dcterms:modified">${SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())}</meta>
    </metadata>
    <manifest>
        <item id="style" href="style.css" media-type="text/css"/>
        <item id="nav" href="nav.xhtml" media-type="application/xhtml+xml" properties="nav"/>
        <item id="title" href="title.xhtml" media-type="application/xhtml+xml"/>
""")
            chapterFiles.forEachIndexed { index, file ->
                append("        <item id=\"ch_${index + 1}\" href=\"$file\" media-type=\"application/xhtml+xml\"/>\n")
            }
            append("""    </manifest>
    <spine>
        <itemref idref="title"/>
        <itemref idref="nav"/>
""")
            chapterFiles.forEachIndexed { index, _ ->
                append("        <itemref idref=\"ch_${index + 1}\"/>\n")
            }
            append("""    </spine>
</package>""")
        }
        zip.putNextEntry(ZipEntry("OEBPS/content.opf"))
        zip.write(opf.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        zip.finish()
    }

    /**
     * Generates a printable, styled PDF document on Android.
     */
    fun exportToPdf(manuscript: CompiledManuscript, outputStream: OutputStream) {
        val document = PdfDocument()

        val pageWidth = 595 // standard A4 @ 72dpi
        val pageHeight = 842
        val margin = 54
        val contentWidth = pageWidth - (margin * 2)

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 11f
            isAntiAlias = true
            typeface = if (manuscript.settings.defaultFontFamily == "Serif") Typeface.SERIF else Typeface.SANS_SERIF
        }

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
            isAntiAlias = true
            typeface = Typeface.SERIF
        }

        val authorPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 14f
            isAntiAlias = true
            typeface = Typeface.SERIF
        }

        val chapterHeaderPaint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
            isFakeBoldText = true
            isAntiAlias = true
            typeface = Typeface.SERIF
        }

        val pageNumberPaint = Paint().apply {
            color = Color.GRAY
            textSize = 9f
            isAntiAlias = true
        }

        var pageNum = 1

        // Page 1: Title & Dedication Page
        val titlePageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum++).create()
        val titlePage = document.startPage(titlePageInfo)
        val titleCanvas = titlePage.canvas

        var y = 260f
        titleCanvas.drawText(manuscript.project.title, margin.toFloat(), y, titlePaint)
        y += 40f
        if (manuscript.project.author.isNotBlank()) {
            titleCanvas.drawText("by ${manuscript.project.author}", margin.toFloat(), y, authorPaint)
            y += 60f
        }
        if (manuscript.settings.dedication.isNotBlank()) {
            y += 80f
            titleCanvas.drawText(manuscript.settings.dedication, margin.toFloat(), y, authorPaint)
        }
        y = pageHeight - 80f
        titleCanvas.drawText("Total Words: ${manuscript.totalWords} • Chapters: ${manuscript.chapters.size}", margin.toFloat(), y, pageNumberPaint)
        document.finishPage(titlePage)

        // Chapter Pages
        for (ch in manuscript.chapters) {
            var currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum++).create()
            var currentPage = document.startPage(currentPageInfo)
            var canvas = currentPage.canvas
            var currentY = margin.toFloat() + 30f

            val chapterHeading = if (manuscript.settings.exportChapterNumbering) {
                if (ch.chapter.title.isNotBlank()) "Chapter ${ch.chapter.chapterNumber}: ${ch.chapter.title}"
                else "Chapter ${ch.chapter.chapterNumber}"
            } else {
                ch.chapter.title.ifBlank { "Chapter ${ch.chapter.chapterNumber}" }
            }

            if (ch.actTitle != null) {
                val actPaint = Paint(authorPaint).apply { textSize = 11f }
                canvas.drawText(ch.actTitle.uppercase(Locale.US), margin.toFloat(), currentY, actPaint)
                currentY += 24f
            }

            canvas.drawText(chapterHeading, margin.toFloat(), currentY, chapterHeaderPaint)
            currentY += 32f

            val paragraphs = ch.formattedContent.split("\n\n").filter { it.isNotBlank() }

            for (p in paragraphs) {
                val trimmed = p.trim()
                if (trimmed == "* * *" || trimmed == "***" || trimmed == "#") {
                    currentY += 14f
                    if (currentY > pageHeight - margin - 30) {
                        canvas.drawText("${pageNum - 1}", (pageWidth / 2).toFloat(), (pageHeight - margin / 2).toFloat(), pageNumberPaint)
                        document.finishPage(currentPage)
                        currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum++).create()
                        currentPage = document.startPage(currentPageInfo)
                        canvas = currentPage.canvas
                        currentY = margin.toFloat() + 20f
                    }
                    val breakPaint = Paint(chapterHeaderPaint).apply { textSize = 12f }
                    canvas.drawText("*  *  *", (pageWidth / 2 - 20).toFloat(), currentY, breakPaint)
                    currentY += 24f
                    continue
                }

                // Wrap text lines
                val words = trimmed.split(Regex("\\s+"))
                var currentLine = ""

                for (word in words) {
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    val measure = textPaint.measureText(testLine)
                    if (measure > contentWidth && currentLine.isNotEmpty()) {
                        // Print current line
                        if (currentY > pageHeight - margin - 30) {
                            canvas.drawText("${pageNum - 1}", (pageWidth / 2).toFloat(), (pageHeight - margin / 2).toFloat(), pageNumberPaint)
                            document.finishPage(currentPage)
                            currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum++).create()
                            currentPage = document.startPage(currentPageInfo)
                            canvas = currentPage.canvas
                            currentY = margin.toFloat() + 20f
                        }
                        canvas.drawText(currentLine, margin.toFloat(), currentY, textPaint)
                        currentY += 16f
                        currentLine = word
                    } else {
                        currentLine = testLine
                    }
                }

                if (currentLine.isNotEmpty()) {
                    if (currentY > pageHeight - margin - 30) {
                        canvas.drawText("${pageNum - 1}", (pageWidth / 2).toFloat(), (pageHeight - margin / 2).toFloat(), pageNumberPaint)
                        document.finishPage(currentPage)
                        currentPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum++).create()
                        currentPage = document.startPage(currentPageInfo)
                        canvas = currentPage.canvas
                        currentY = margin.toFloat() + 20f
                    }
                    canvas.drawText(currentLine, margin.toFloat(), currentY, textPaint)
                    currentY += 22f
                }
            }

            canvas.drawText("${pageNum - 1}", (pageWidth / 2).toFloat(), (pageHeight - margin / 2).toFloat(), pageNumberPaint)
            document.finishPage(currentPage)
        }

        document.writeTo(outputStream)
        document.close()
    }

    /**
     * Generates a clean HTML book.
     */
    fun exportToHtml(manuscript: CompiledManuscript): String = buildString {
        append("""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${escapeXml(manuscript.project.title)}</title>
    <style>
        body {
            max-width: 750px;
            margin: 40px auto;
            padding: 0 24px;
            font-family: Georgia, serif;
            line-height: ${manuscript.settings.lineSpacing};
            color: #1a1a1a;
            background: #faf7f2;
        }
        h1.title { text-align: center; margin-top: 60px; font-size: 2.5em; }
        h2.author { text-align: center; color: #555; font-style: italic; margin-bottom: 40px; }
        .toc { background: #fff; padding: 24px; border-radius: 8px; border: 1px solid #e5e0d8; margin: 40px 0; }
        .toc h3 { margin-top: 0; }
        .chapter { margin-top: 60px; page-break-before: always; }
        .chapter-title { font-size: 1.8em; text-align: center; border-bottom: 2px solid #e5e0d8; padding-bottom: 12px; }
        p { text-indent: 1.5em; margin: 0 0 12px 0; text-align: justify; }
        .scene-break { text-align: center; letter-spacing: 0.3em; margin: 24px 0; font-weight: bold; }
    </style>
</head>
<body>
    <h1 class="title">${escapeXml(manuscript.project.title)}</h1>
    ${if (manuscript.project.author.isNotBlank()) "<h2 class=\"author\">${escapeXml(manuscript.project.author)}</h2>" else ""}
    
    <div class="toc">
        <h3>Table of Contents</h3>
        <ol>
""")
        manuscript.chapters.forEachIndexed { index, ch ->
            val heading = if (manuscript.settings.exportChapterNumbering) {
                if (ch.chapter.title.isNotBlank()) "Chapter ${ch.chapter.chapterNumber}: ${ch.chapter.title}"
                else "Chapter ${ch.chapter.chapterNumber}"
            } else {
                ch.chapter.title.ifBlank { "Chapter ${ch.chapter.chapterNumber}" }
            }
            append("            <li><a href=\"#ch${index + 1}\">${escapeXml(heading)}</a></li>\n")
        }
        append("""        </ol>
    </div>
""")
        manuscript.chapters.forEachIndexed { index, ch ->
            val heading = if (manuscript.settings.exportChapterNumbering) {
                if (ch.chapter.title.isNotBlank()) "Chapter ${ch.chapter.chapterNumber}: ${ch.chapter.title}"
                else "Chapter ${ch.chapter.chapterNumber}"
            } else {
                ch.chapter.title.ifBlank { "Chapter ${ch.chapter.chapterNumber}" }
            }
            append("""
    <div class="chapter" id="ch${index + 1}">
        ${if (ch.actTitle != null) "<p style=\"text-align:center;color:#666;text-transform:uppercase;\">${escapeXml(ch.actTitle)}</p>" else ""}
        <h2 class="chapter-title">${escapeXml(heading)}</h2>
""")
            val paragraphs = ch.formattedContent.split("\n\n").filter { it.isNotBlank() }
            for (p in paragraphs) {
                val trimmed = p.trim()
                if (trimmed == "* * *" || trimmed == "***" || trimmed == "#") {
                    append("        <div class=\"scene-break\">* * *</div>\n")
                } else {
                    append("        <p>${escapeXml(trimmed).replace("\n", "<br/>")}</p>\n")
                }
            }
            append("    </div>\n")
        }
        append("</body>\n</html>")
    }

    /**
     * Generates a standard Plain Text manuscript.
     */
    fun exportToTxt(manuscript: CompiledManuscript): String = buildString {
        appendLine("================================================================================")
        appendLine(manuscript.project.title.uppercase(Locale.US))
        if (manuscript.project.author.isNotBlank()) {
            appendLine("by ${manuscript.project.author}")
        }
        appendLine("Total Words: ${manuscript.totalWords} | Chapters: ${manuscript.chapters.size}")
        appendLine("================================================================================")
        appendLine()

        if (manuscript.settings.dedication.isNotBlank()) {
            appendLine(manuscript.settings.dedication)
            appendLine()
            appendLine("--------------------------------------------------------------------------------")
            appendLine()
        }

        for (ch in manuscript.chapters) {
            val heading = if (manuscript.settings.exportChapterNumbering) {
                if (ch.chapter.title.isNotBlank()) "CHAPTER ${ch.chapter.chapterNumber}: ${ch.chapter.title}"
                else "CHAPTER ${ch.chapter.chapterNumber}"
            } else {
                ch.chapter.title.ifBlank { "CHAPTER ${ch.chapter.chapterNumber}" }
            }

            appendLine()
            if (ch.actTitle != null) {
                appendLine("[ ${ch.actTitle.uppercase(Locale.US)} ]")
            }
            appendLine(heading)
            appendLine("--------------------------------------------------------------------------------")
            appendLine()

            val paragraphs = ch.formattedContent.split("\n\n").filter { it.isNotBlank() }
            for (p in paragraphs) {
                val trimmed = p.trim()
                if (trimmed == "* * *" || trimmed == "***" || trimmed == "#") {
                    appendLine("    * * *")
                    appendLine()
                } else {
                    appendLine("    $trimmed")
                    appendLine()
                }
            }
            appendLine()
        }
    }

    /**
     * Generates a standard RTF file compatible with MS Word / LibreOffice.
     */
    fun exportToRtf(manuscript: CompiledManuscript): String = buildString {
        append("{\\rtf1\\ansi\\deff0\n")
        append("{\\fonttbl{\\f0\\froman\\fcharset0 Georgia;}{\\f1\\fswiss\\fcharset0 Arial;}}\n")
        append("{\\colortbl;\\red0\\green0\\blue0;\\red120\\green120\\blue120;}\n")
        append("\\viewkind4\\uc1\\pard\\sa200\\sl276\\slmult1\n")

        // Title Page
        append("\\qc\\b\\fs48 ${escapeRtf(manuscript.project.title)}\\b0\\fs24\\par\n")
        if (manuscript.project.author.isNotBlank()) {
            append("\\qc\\i\\fs28 by ${escapeRtf(manuscript.project.author)}\\i0\\fs24\\par\n")
        }
        append("\\page\n")

        // Chapters
        for (ch in manuscript.chapters) {
            append("\\page\n")
            val heading = if (manuscript.settings.exportChapterNumbering) {
                if (ch.chapter.title.isNotBlank()) "Chapter ${ch.chapter.chapterNumber}: ${ch.chapter.title}"
                else "Chapter ${ch.chapter.chapterNumber}"
            } else {
                ch.chapter.title.ifBlank { "Chapter ${ch.chapter.chapterNumber}" }
            }

            if (ch.actTitle != null) {
                append("\\qc\\cf2\\fs20 ${escapeRtf(ch.actTitle.uppercase(Locale.US))}\\cf1\\fs24\\par\n")
            }
            append("\\qc\\b\\fs36 ${escapeRtf(heading)}\\b0\\fs24\\par\\par\n")

            val paragraphs = ch.formattedContent.split("\n\n").filter { it.isNotBlank() }
            for (p in paragraphs) {
                val trimmed = p.trim()
                if (trimmed == "* * *" || trimmed == "***" || trimmed == "#") {
                    append("\\qc\\b *  *  *\\b0\\par\\par\n")
                } else {
                    append("\\qj\\fi720 ${escapeRtf(trimmed)}\\par\\par\n")
                }
            }
        }
        append("}\n")
    }

    /**
     * Full Project Backup into JSON format.
     */
    suspend fun backupProjectToJson(db: AppDatabase, projectId: String): String {
        val root = JSONObject()
        val project = db.projectDao().getProjectById(projectId) ?: return "{}"

        root.put("version", 1)
        root.put("app", "Novel Writer")
        root.put("exportedAt", System.currentTimeMillis())

        // Project
        val projJson = JSONObject().apply {
            put("id", project.id)
            put("title", project.title)
            put("author", project.author)
            put("description", project.description)
            put("coverColor", project.coverColor)
            put("coverIcon", project.coverIcon)
            put("targetWordCount", project.targetWordCount)
            put("createdAt", project.createdAt)
            put("updatedAt", project.updatedAt)
        }
        root.put("project", projJson)

        // Settings
        val settings = db.projectSettingsDao().getSettings(projectId)
        if (settings != null) {
            val setJson = JSONObject().apply {
                put("defaultFontFamily", settings.defaultFontFamily)
                put("fontSizeSp", settings.fontSizeSp)
                put("lineSpacing", settings.lineSpacing)
                put("paragraphSpacing", settings.paragraphSpacing)
                put("autosaveIntervalSeconds", settings.autosaveIntervalSeconds)
                put("defaultExportFormat", settings.defaultExportFormat)
                put("exportSceneSeparator", settings.exportSceneSeparator)
                put("dedication", settings.dedication)
                put("copyrightHolder", settings.copyrightHolder)
            }
            root.put("settings", setJson)
        }

        // Acts
        val acts = db.actDao().getActsForProjectSync(projectId)
        val actsArray = JSONArray()
        for (a in acts) {
            actsArray.put(JSONObject().apply {
                put("id", a.id)
                put("title", a.title)
                put("sortOrder", a.sortOrder)
                put("description", a.description)
            })
        }
        root.put("acts", actsArray)

        // Chapters
        val chapters = db.chapterDao().getChaptersForProjectSync(projectId)
        val chaptersArray = JSONArray()
        for (c in chapters) {
            chaptersArray.put(JSONObject().apply {
                put("id", c.id)
                put("actId", c.actId ?: JSONObject.NULL)
                put("title", c.title)
                put("chapterNumber", c.chapterNumber)
                put("sortOrder", c.sortOrder)
                put("summary", c.summary)
                put("notes", c.notes)
                put("status", c.status)
            })
        }
        root.put("chapters", chaptersArray)

        // Scenes
        val scenes = db.sceneDao().getAllScenesForProjectSync(projectId)
        val scenesArray = JSONArray()
        for (s in scenes) {
            scenesArray.put(JSONObject().apply {
                put("id", s.id)
                put("chapterId", s.chapterId)
                put("title", s.title)
                put("body", s.body)
                put("sortOrder", s.sortOrder)
                put("status", s.status)
                put("povCharacterName", s.povCharacterName ?: JSONObject.NULL)
                put("locationName", s.locationName ?: JSONObject.NULL)
                put("timeSetting", s.timeSetting ?: JSONObject.NULL)
                put("summary", s.summary)
                put("purpose", s.purpose)
                put("notes", s.notes)
                put("wordCount", s.wordCount)
                put("characterCount", s.characterCount)
            })
        }
        root.put("scenes", scenesArray)

        // Characters
        val characters = db.characterDao().getCharactersForProjectSync(projectId)
        val charsArray = JSONArray()
        for (ch in characters) {
            charsArray.put(JSONObject().apply {
                put("id", ch.id)
                put("name", ch.name)
                put("nickname", ch.nickname)
                put("age", ch.age)
                put("gender", ch.gender)
                put("occupation", ch.occupation)
                put("appearance", ch.appearance)
                put("personality", ch.personality)
                put("biography", ch.biography)
                put("goals", ch.goals)
                put("motivation", ch.motivation)
                put("fears", ch.fears)
                put("strengths", ch.strengths)
                put("weaknesses", ch.weaknesses)
                put("notes", ch.notes)
                put("color", ch.color)
            })
        }
        root.put("characters", charsArray)

        // World Entries
        val world = db.worldEntryDao().getWorldEntriesForProjectSync(projectId)
        val worldArray = JSONArray()
        for (w in world) {
            worldArray.put(JSONObject().apply {
                put("id", w.id)
                put("category", w.category)
                put("title", w.title)
                put("description", w.description)
                put("notes", w.notes)
                put("tags", w.tags)
                put("color", w.color)
            })
        }
        root.put("worldEntries", worldArray)

        // Relationships
        val relationships = db.relationshipDao().getRelationshipsForProjectSync(projectId)
        val relArray = JSONArray()
        for (r in relationships) {
            relArray.put(JSONObject().apply {
                put("id", r.id)
                put("sourceName", r.sourceName)
                put("targetName", r.targetName)
                put("relationType", r.relationType)
                put("description", r.description)
                put("strength", r.strength)
            })
        }
        root.put("relationships", relArray)

        // Subplots
        val subplots = db.subplotDao().getSubplotsForProjectSync(projectId)
        val subArray = JSONArray()
        for (sp in subplots) {
            subArray.put(JSONObject().apply {
                put("id", sp.id)
                put("name", sp.name)
                put("description", sp.description)
                put("status", sp.status)
                put("progress", sp.progress)
            })
        }
        root.put("subplots", subArray)

        // Notes
        val notes = db.storyNoteDao().getNotesForProjectSync(projectId)
        val notesArray = JSONArray()
        for (n in notes) {
            notesArray.put(JSONObject().apply {
                put("id", n.id)
                put("title", n.title)
                put("content", n.content)
                put("category", n.category)
            })
        }
        root.put("notes", notesArray)

        return root.toString(2)
    }

    /**
     * Restores a full project from JSON backup.
     */
    suspend fun restoreProjectFromJson(db: AppDatabase, jsonString: String): ProjectEntity? {
        try {
            val root = JSONObject(jsonString)
            val projObj = root.getJSONObject("project")
            val newProjectId = UUID.randomUUID().toString()

            val project = ProjectEntity(
                id = newProjectId,
                title = projObj.optString("title", "Restored Project") + " (Restored)",
                author = projObj.optString("author", ""),
                description = projObj.optString("description", ""),
                coverColor = projObj.optLong("coverColor", 0xFF1E293B),
                coverIcon = projObj.optString("coverIcon", "book"),
                targetWordCount = projObj.optInt("targetWordCount", 50000),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            db.projectDao().insertProject(project)

            // Restore settings
            val setObj = root.optJSONObject("settings")
            db.projectSettingsDao().insertOrUpdateSettings(
                ProjectSettingsEntity(
                    projectId = newProjectId,
                    defaultFontFamily = setObj?.optString("defaultFontFamily", "Serif") ?: "Serif",
                    fontSizeSp = setObj?.optDouble("fontSizeSp", 16.0)?.toFloat() ?: 16f,
                    lineSpacing = setObj?.optDouble("lineSpacing", 1.6)?.toFloat() ?: 1.6f,
                    dedication = setObj?.optString("dedication", "") ?: "",
                    copyrightHolder = setObj?.optString("copyrightHolder", "") ?: ""
                )
            )

            // Restore Acts
            val actsArray = root.optJSONArray("acts")
            val actIdMap = mutableMapOf<String, String>()
            if (actsArray != null) {
                for (i in 0 until actsArray.length()) {
                    val a = actsArray.getJSONObject(i)
                    val oldId = a.optString("id")
                    val newActId = UUID.randomUUID().toString()
                    actIdMap[oldId] = newActId
                    db.actDao().insertAct(
                        ActEntity(
                            id = newActId,
                            projectId = newProjectId,
                            title = a.optString("title", "Act"),
                            sortOrder = a.optInt("sortOrder", i),
                            description = a.optString("description", "")
                        )
                    )
                }
            }

            // Restore Chapters
            val chArray = root.optJSONArray("chapters")
            val chIdMap = mutableMapOf<String, String>()
            if (chArray != null) {
                for (i in 0 until chArray.length()) {
                    val c = chArray.getJSONObject(i)
                    val oldId = c.optString("id")
                    val newChapterId = UUID.randomUUID().toString()
                    chIdMap[oldId] = newChapterId
                    val actId = c.optString("actId").takeIf { it.isNotBlank() && it != "null" }?.let { actIdMap[it] }
                    db.chapterDao().insertChapter(
                        ChapterEntity(
                            id = newChapterId,
                            projectId = newProjectId,
                            actId = actId,
                            title = c.optString("title", "Chapter ${i + 1}"),
                            chapterNumber = c.optInt("chapterNumber", i + 1),
                            sortOrder = c.optInt("sortOrder", i),
                            summary = c.optString("summary", ""),
                            notes = c.optString("notes", ""),
                            status = c.optString("status", "To Do")
                        )
                    )
                }
            }

            // Restore Scenes
            val scArray = root.optJSONArray("scenes")
            if (scArray != null) {
                for (i in 0 until scArray.length()) {
                    val s = scArray.getJSONObject(i)
                    val oldChId = s.optString("chapterId")
                    val newChId = chIdMap[oldChId] ?: chIdMap.values.firstOrNull() ?: continue
                    val body = s.optString("body", "")
                    val words = if (body.isBlank()) 0 else body.trim().split(Regex("\\s+")).size
                    db.sceneDao().insertScene(
                        SceneEntity(
                            id = UUID.randomUUID().toString(),
                            projectId = newProjectId,
                            chapterId = newChId,
                            title = s.optString("title", "Scene ${i + 1}"),
                            body = body,
                            sortOrder = s.optInt("sortOrder", i),
                            status = s.optString("status", "To Do"),
                            povCharacterName = s.optString("povCharacterName").takeIf { it.isNotBlank() && it != "null" },
                            locationName = s.optString("locationName").takeIf { it.isNotBlank() && it != "null" },
                            timeSetting = s.optString("timeSetting").takeIf { it.isNotBlank() && it != "null" },
                            summary = s.optString("summary", ""),
                            purpose = s.optString("purpose", ""),
                            notes = s.optString("notes", ""),
                            wordCount = words,
                            characterCount = body.length
                        )
                    )
                }
            }

            // Restore Characters
            val charArray = root.optJSONArray("characters")
            if (charArray != null) {
                for (i in 0 until charArray.length()) {
                    val ch = charArray.getJSONObject(i)
                    db.characterDao().insertCharacter(
                        CharacterEntity(
                            id = UUID.randomUUID().toString(),
                            projectId = newProjectId,
                            name = ch.optString("name", "Character"),
                            nickname = ch.optString("nickname", ""),
                            age = ch.optString("age", ""),
                            gender = ch.optString("gender", ""),
                            occupation = ch.optString("occupation", ""),
                            appearance = ch.optString("appearance", ""),
                            personality = ch.optString("personality", ""),
                            biography = ch.optString("biography", ""),
                            goals = ch.optString("goals", ""),
                            motivation = ch.optString("motivation", ""),
                            notes = ch.optString("notes", ""),
                            color = ch.optLong("color", 0xFFB45309)
                        )
                    )
                }
            }

            // Restore World Entries
            val wArray = root.optJSONArray("worldEntries")
            if (wArray != null) {
                for (i in 0 until wArray.length()) {
                    val w = wArray.getJSONObject(i)
                    db.worldEntryDao().insertWorldEntry(
                        WorldEntryEntity(
                            id = UUID.randomUUID().toString(),
                            projectId = newProjectId,
                            category = w.optString("category", "Location"),
                            title = w.optString("title", "Entry"),
                            description = w.optString("description", ""),
                            notes = w.optString("notes", ""),
                            tags = w.optString("tags", ""),
                            color = w.optLong("color", 0xFF4338CA)
                        )
                    )
                }
            }

            // Restore Subplots
            val spArray = root.optJSONArray("subplots")
            if (spArray != null) {
                for (i in 0 until spArray.length()) {
                    val sp = spArray.getJSONObject(i)
                    db.subplotDao().insertSubplot(
                        SubplotEntity(
                            id = UUID.randomUUID().toString(),
                            projectId = newProjectId,
                            name = sp.optString("name", "Subplot"),
                            description = sp.optString("description", ""),
                            status = sp.optString("status", "Active"),
                            progress = sp.optInt("progress", 0)
                        )
                    )
                }
            }

            return project
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Imports a plain text or formatted text manuscript, intelligently detecting chapters and scene breaks.
     */
    suspend fun importManuscriptFromText(db: AppDatabase, title: String, text: String): ProjectEntity {
        val newProjectId = UUID.randomUUID().toString()
        val project = ProjectEntity(
            id = newProjectId,
            title = title.ifBlank { "Imported Manuscript" },
            targetWordCount = 50000,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        db.projectDao().insertProject(project)

        db.projectSettingsDao().insertOrUpdateSettings(
            ProjectSettingsEntity(projectId = newProjectId)
        )

        // Parse text into chapters using common patterns (e.g. "Chapter 1", "CHAPTER I", "Act 1", etc.)
        val lines = text.split("\n")
        val chapterRegex = Regex("^(Chapter|CHAPTER|Act|ACT|Part|PART)\\s+([0-9IVXLCDM]+)(.*)$", RegexOption.IGNORE_CASE)

        val parsedChapters = mutableListOf<Pair<String, MutableList<String>>>()
        var currentChapterTitle = "Chapter 1"
        var currentLines = mutableListOf<String>()

        for (line in lines) {
            val trimmed = line.trim()
            if (chapterRegex.matches(trimmed)) {
                if (currentLines.any { it.isNotBlank() }) {
                    parsedChapters.add(currentChapterTitle to currentLines)
                    currentLines = mutableListOf()
                }
                currentChapterTitle = trimmed
            } else {
                currentLines.add(line)
            }
        }
        if (currentLines.any { it.isNotBlank() }) {
            parsedChapters.add(currentChapterTitle to currentLines)
        }

        if (parsedChapters.isEmpty()) {
            parsedChapters.add("Chapter 1" to currentLines)
        }

        parsedChapters.forEachIndexed { chIndex, (chTitle, chLines) ->
            val chapterId = UUID.randomUUID().toString()
            db.chapterDao().insertChapter(
                ChapterEntity(
                    id = chapterId,
                    projectId = newProjectId,
                    title = chTitle,
                    chapterNumber = chIndex + 1,
                    sortOrder = chIndex
                )
            )

            // Split into scenes using scene breaks like *** or * * * or #
            val fullChapterText = chLines.joinToString("\n")
            val sceneChunks = fullChapterText.split(Regex("(?m)^\\s*(\\*\\s*\\*\\s*\\*|#{1,3}|---+)\\s*$"))
                .filter { it.isNotBlank() }

            if (sceneChunks.isEmpty()) {
                val sceneId = UUID.randomUUID().toString()
                db.sceneDao().insertScene(
                    SceneEntity(
                        id = sceneId,
                        projectId = newProjectId,
                        chapterId = chapterId,
                        title = "Scene 1",
                        body = fullChapterText.trim(),
                        sortOrder = 0,
                        wordCount = fullChapterText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size,
                        characterCount = fullChapterText.trim().length
                    )
                )
            } else {
                sceneChunks.forEachIndexed { scIndex, sceneText ->
                    val cleanText = sceneText.trim()
                    val wordCount = cleanText.split(Regex("\\s+")).filter { it.isNotBlank() }.size
                    db.sceneDao().insertScene(
                        SceneEntity(
                            id = UUID.randomUUID().toString(),
                            projectId = newProjectId,
                            chapterId = chapterId,
                            title = "Scene ${scIndex + 1}",
                            body = cleanText,
                            sortOrder = scIndex,
                            wordCount = wordCount,
                            characterCount = cleanText.length
                        )
                    )
                }
            }
        }

        return project
    }

    private fun escapeXml(input: String): String {
        return input.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    private fun escapeRtf(input: String): String {
        val sb = StringBuilder()
        for (c in input) {
            when (c) {
                '\\' -> sb.append("\\\\")
                '{' -> sb.append("\\{")
                '}' -> sb.append("\\}")
                '\n' -> sb.append("\\par\n")
                else -> {
                    if (c.code in 32..126) {
                        sb.append(c)
                    } else {
                        sb.append("\\u${c.code}?")
                    }
                }
            }
        }
        return sb.toString()
    }
}
