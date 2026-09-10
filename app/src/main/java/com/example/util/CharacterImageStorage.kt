package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object CharacterImageStorage {

    private const val IMAGES_DIR = "character_images"
    private const val TEMP_CAMERA_DIR = "camera_photos"

    fun getImagesDirectory(context: Context): File {
        val dir = File(context.filesDir, IMAGES_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Copies an image from an external/content URI into internal app storage
     * to ensure it never expires or becomes invalid.
     */
    fun saveImagePermanently(context: Context, sourceUri: Uri, characterId: String): String? {
        return try {
            val dir = getImagesDirectory(context)
            val destFile = File(dir, "char_${characterId}_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg")
            val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
            if (inputStream != null) {
                inputStream.use { input ->
                    // Optionally decode and compress to a reasonable size for smooth rendering
                    val bitmap = BitmapFactory.decodeStream(input)
                    if (bitmap != null) {
                        FileOutputStream(destFile).use { output ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
                        }
                        destFile.absolutePath
                    } else {
                        // Fallback direct copy
                        val directStream = context.contentResolver.openInputStream(sourceUri)
                        directStream?.use { directInput ->
                            FileOutputStream(destFile).use { directOutput ->
                                directInput.copyTo(directOutput)
                            }
                        }
                        destFile.absolutePath
                    }
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Creates a temporary file for camera capture with a FileProvider URI.
     */
    fun createCameraCaptureUri(context: Context): Pair<Uri, File>? {
        return try {
            val cacheDir = File(context.cacheDir, TEMP_CAMERA_DIR)
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val tempFile = File(cacheDir, "cam_temp_${System.currentTimeMillis()}.jpg")
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, tempFile)
            Pair(uri, tempFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getFileForPath(context: Context, path: String): File {
        return File(path)
    }

    fun saveImageFromUri(context: Context, sourceUri: Uri): String? {
        return saveImagePermanently(context, sourceUri, "img")
    }

    fun createTempCameraUri(context: Context): Uri? {
        return createCameraCaptureUri(context)?.first
    }

    /**
     * Deletes a local image file if it belongs to character images.
     */
    fun deleteImageFile(path: String) {
        try {
            val file = File(path)
            if (file.exists() && file.path.contains(IMAGES_DIR)) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
