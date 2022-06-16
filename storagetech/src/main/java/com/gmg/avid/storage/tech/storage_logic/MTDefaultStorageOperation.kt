package com.gmg.avid.storage.tech.storage_logic

import android.content.Context
import android.net.Uri
import com.gmg.avid.storage.tech.common.MTFileData
import com.gmg.avid.storage.tech.common.MTStorageCommonOperation.isDurationRequired
import com.gmg.avid.storage.tech.common.MTStorageCommonOperation.writeFileInDestination
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getDurationFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getMimeTypeFromUri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

/**
 * The object class contains default storage Operation
 */
object MTDefaultStorageOperation {

    /**
     * The method called to check file exist by default method
     *
     * @param filePath      The file path
     * @return The boolean that determines the file exist
     */
    fun isFileExistByDefault(filePath : String): Boolean {
        return File(filePath).exists()
    }

    /**
     * The method called to get file uri by default method
     *
     * @param filePath      The file path
     * @return  The file uri by default
     */
    fun getFileUriByDefault(filePath: String) : Uri? {
        return Uri.fromFile(File(filePath))
    }

    /**
     * The method called to get file length by default method
     *
     * @param filePath     The file path
     * @return  The file length by default
     */
    fun getFileLengthByDefault(filePath: String) : Long {
        return File(filePath).length()
    }

    /**
     * The method called to get file details by default method
     *
     * @param filePath      The file path
     * @return  The file details
     */
    fun Context.getFileDetailByDefault(filePath: String) : MTFileData {
        val file = File(filePath)
        val relativePath = file.parent
        val displayName = file.name
        val uri = getFileUriByDefault(filePath)
        val length = getFileLengthByDefault(filePath)
        val mimeType = uri?.let { getMimeTypeFromUri(it) }
        val isDurationRequired = isDurationRequired(mimeType)
        val duration = if (isDurationRequired) uri?.let { getDurationFromUri(it) } else null
        return MTFileData(filePath = filePath, relativePath = relativePath, displayName = displayName, mimeType = mimeType, duration = duration, size = length, uri = uri)
    }

    /**
     * The method called to create file by default
     *
     * @param filePath      The file path
     * @param inputStream   The input stream
     * @return  The file details
     */
    @Throws(Exception::class)
    fun Context.createFileByDefault(filePath: String, inputStream: InputStream) : MTFileData? {
        val file = File(filePath)
        val relativeFile : File? = file.parentFile
        relativeFile?.let {
            if (!relativeFile.exists()) {
                relativeFile.mkdirs()
            }
        }
        file.createNewFile()
        val outputStream: OutputStream = FileOutputStream(file)
        writeFileInDestination(inputStream, outputStream)
        return getFileDetailByDefault(filePath)
    }

    /**
     * The method called to update file by default method
     *
     * @param filePath      The file path
     * @param inputStream   The input stream
     * @return The file details
     */
    @Throws(Exception::class)
    fun Context.updateFileByDefault(filePath: String, inputStream: InputStream) : MTFileData? {
        val file = File(filePath)
        val outputStream: OutputStream = FileOutputStream(file)
        writeFileInDestination(inputStream, outputStream)
        return getFileDetailByDefault(filePath)
    }

    /**
     * The method called to delete file by default method
     *
     * @param filePath     The file path
     * @return  The boolean determines the file is delete
     */
    @Throws(Exception::class)
    fun deleteFileByDefault(filePath: String): Boolean {
        File(filePath).delete()
        return true
    }
}