package com.gmg.avid.storage.tech.storage_logic

import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.gmg.avid.storage.tech.common.MTFileData
import com.gmg.avid.storage.tech.common.MTStorageCommonOperation.isDurationRequired
import com.gmg.avid.storage.tech.common.MTStorageCommonOperation.writeFileInDestination
import com.gmg.avid.storage.tech.storage_logic.MTStorageDirectoryHandler.isOsHigherThenQ
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getDurationFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getInputStreamFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getLengthFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getMimeTypeFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getOutputStreamFromUri
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import android.database.Cursor
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import com.gmg.avid.storage.tech.common.MTMediaData
import com.gmg.avid.storage.tech.common.MTMediaType
import com.gmg.avid.storage.tech.storage_logic.MTStorageDirectoryHandler.isOsHigherThenR


/**
 * This object include the method of scope storage to access files
 */
object MTScopeStorageHandler {

    private const val CONTENT = "content"
    private const val FILE = "file"
    private const val IMAGE = "image"
    private const val VIDEO = "video"
    private const val AUDIO = "audio"
    private const val PRIMARY = "primary"
    private const val PUBLIC_DOWNLOAD_AUTHORITY = "content://downloads/public_downloads"
    private const val EXTERNAL_STORAGE_DOCUMENT_AUTHORITY = "com.android.externalstorage.documents"
    private const val DOWNLOAD_STORAGE_DOCUMENT_AUTHORITY = "com.android.providers.downloads.documents"
    private const val MEDIA_STORAGE_DOCUMENT_AUTHORITY = "com.android.providers.media.documents"

    /**
     * The method called to check whether the file exist by scope storage using path
     *
     * @param path      The file path
     * @return  The boolean determine the file exist or not
     */
    fun Context.isFileExistByScope(path : String): Boolean {
        return try {
            val fileUri = getFileUriByScope(path) //get uri using path
            val inputStream = fileUri?.let { contentResolver.openInputStream(it) }
            inputStream != null //uri address does not remove after file getting delete so check byte are available using input stream
        } catch (exception: Exception) {
            false
        }
    }

    /**
     * The method called to check whether the media exist by scope storage using media id
     *
     * @param mediaId     The media id
     * @return  The boolean determine the media exist
     */
    fun Context.isMediaFileExistByScope(mediaId: Long) : Boolean {
        return try {
            val fileUri = getMediaFileUriByScope(mediaId) //get uri using media id
            val inputStream = fileUri?.let { contentResolver.openInputStream(it) }
            inputStream != null //uri address does not remove after file getting delete so check byte are available using input stream
        } catch (exception : Exception) {
            false
        }
    }

    /**
     * The method called to get uri by scope storage using file path
     *
     * @param path     The file path
     * @return  The uri from the file
     */
    @SuppressLint("NewApi")
    fun Context.getFileUriByScope(path : String): Uri? {
        return if (isOsHigherThenR()) {  //check whether the device is greater than R, if yes then handle uri getting process by media store api
            val fileData: MTMediaType? = getFileMediaIdByScope(path) //get media id and type using path
            if (fileData?.mediaId != null) {
                getFileUriByIdScope(fileData.mediaId, fileData.mimeType) //get file uri by scope
            } else {
                null
            }
        } else {
            MTDefaultStorageOperation.getFileUriByDefault(path) //get uri using default storage
        }
    }

    /**
     * The method called to get file uri with id by scope using file path
     *
     * @param path      The path
     * @return the media data
     */
    private fun Context.getFileUriWithIdByScope(path: String) : MTMediaData? {
        val fileData: MTMediaType? = getFileMediaIdByScope(path) //get media type
        return if (fileData?.mediaId != null) {  //check whether media id exist
            val uri = getFileUriByIdScope(fileData.mediaId, fileData.mimeType)
            MTMediaData(fileData.mediaId, uri) //return media id with uri
        } else {
            null
        }
    }

    /**
     * The method called to get file uri by scope storage through media id and mime type
     *
     * @param mediaId       The media id
     * @param mimeType      The mime tyoe
     * @return  The uri
     */
    @SuppressLint("InlinedApi")
    fun getFileUriByIdScope(mediaId: Long, mimeType : Int?): Uri? {
        return if (mimeType != null) {
            when (mimeType) {
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> { //if it is image media type then get uri using image media api
                    Uri.withAppendedPath(MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), mediaId.toString())
                }
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> { //if it is video media type then get video using video media api
                    Uri.withAppendedPath(MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), mediaId.toString())
                }
                MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> { //if it is audio media type then get audio using audio media api
                    Uri.withAppendedPath(MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), mediaId.toString())
                }
                else -> { //if it is document media type then get document using file media api
                    Uri.withAppendedPath(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), mediaId.toString())
                }
            }
        } else { //for unknown data type get file using file media api
            Uri.withAppendedPath(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), mediaId.toString())
        }
    }

    /**
     * The method called to get file media id by scope storage using file path
     *
     * @param filePath      The file path
     * @return  The file details from the media id
     */
    @SuppressLint("InlinedApi")
    fun Context.getFileMediaIdByScope(filePath: String): MTMediaType? {
        val file = File(filePath)
        val displayName: String = file.name
        val relativePath: String? = file.parent
        val resolver: ContentResolver = contentResolver
        val photoUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.RELATIVE_PATH, MediaStore.Files.FileColumns.MEDIA_TYPE)
        val selection = MediaStore.MediaColumns.DISPLAY_NAME + " LIKE ?"
        resolver.query(photoUri, projection, selection, arrayOf(displayName), null)?.use { cursor ->
            var mediaId: Long? = null
            var mediaType : Int? = null
            if (relativePath != null && cursor.count > 1) { //Better to check the relative path is same to identify the same file
                //And to avoid the conflict same name files
                while (cursor.moveToNext()) {
                    val mediaRelativePath = getMediaRelativePathFromCursor(cursor)
                    val parentPath = relativePath + "/";
                    if (mediaRelativePath != null && parentPath.contains(mediaRelativePath)) {
                        mediaId = getMediaIdFromCursor(cursor)
                        mediaType = getMediaTypeFromCursor(cursor)
                        break
                    }
                }
            } else {
                //unless, relative path is empty consider the first item as required file
                if (cursor.moveToFirst()) {
                    mediaId = getMediaIdFromCursor(cursor)
                    mediaType = getMediaTypeFromCursor(cursor)
                }
            }
            cursor.close()
            return MTMediaType(mediaId, mediaType)
        }
        return null
    }

    /**
     * To method called to get file length by scope storage using file path
     *
     * @param filePath      The file path
     * @return  The file length
     */
    @SuppressLint("InlinedApi")
    fun Context.getFileLengthByScope(filePath: String): Long? {
        val file = File(filePath)
        val displayName: String = file.name
        val relativePath: String? = file.parent
        val resolver: ContentResolver = contentResolver
        val photoUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val projection = arrayOf(MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.RELATIVE_PATH)
        val selection = MediaStore.MediaColumns.DISPLAY_NAME + " LIKE ?"
        resolver.query(photoUri, projection, selection, arrayOf(displayName), null)?.use { cursor ->
            var mediaLength: Long? = null
            if (relativePath != null && cursor.count > 1) { //Better to check the relative path is same to identify the same file
                //And to avoid the conflict same name files
                while (cursor.moveToNext()) {
                    val mediaRelativePath = getMediaRelativePathFromCursor(cursor)
                    val parentPath = relativePath + "/";
                    if (mediaRelativePath != null && parentPath.contains(mediaRelativePath)) {
                        mediaLength = getMediaSizeFromCursor(cursor)
                        break
                    }
                }
            } else {
                //unless, relative path is empty consider the first item as required file
                if (cursor.moveToFirst()) {
                    mediaLength = getMediaSizeFromCursor(cursor)
                }
            }
            cursor.close()
            return mediaLength;
        }
        return null
    }

    /**
     * The method called to get file details by scope storage using file path
     *
     * @param filePath  The file path
     * @return The file details
     */
    @SuppressLint("InlinedApi")
    fun Context.getFileDetailByScope(filePath: String) : MTFileData? {
        val file = File(filePath)
        val displayName: String = file.name
        val relativePath: String? = file.parent
        val resolver: ContentResolver = contentResolver
        val photoUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.RELATIVE_PATH, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.MIME_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE)
        val selection = MediaStore.MediaColumns.DISPLAY_NAME + " LIKE ?"
        resolver.query(photoUri, projection, selection, arrayOf(displayName), null)?.use { cursor ->
            var fileData : MTFileData? = null
            if (relativePath != null && cursor.count > 1) { //Better to check the relative path is same to identify the same file
                //And to avoid the conflict same name files
                while (cursor.moveToNext()) {
                    val mediaRelativePath = getMediaRelativePathFromCursor(cursor)
                    val parentPath = relativePath + "/";
                    if (mediaRelativePath != null && parentPath.contains(mediaRelativePath)) {
                        val mediaSize : Long = getMediaSizeFromCursor(cursor)
                        val mediaId : Long = getMediaIdFromCursor(cursor)
                        val mimeType : String? = getMediaMimeTypeFromCursor(cursor)
                        val isDurationRequired = isDurationRequired(mimeType)
                        val mediaType = getMediaTypeFromCursor(cursor)
                        val uri : Uri? = getFileUriByIdScope(mediaId, mediaType)
                        val duration : String? = if (isDurationRequired) getDurationFromUri(uri) else null
                        fileData = MTFileData(mediaId = mediaId, filePath = mediaRelativePath + displayName, relativePath = mediaRelativePath, displayName = displayName, mimeType = mimeType, duration = duration, size = mediaSize, uri = uri)
                    }
                }
            } else {
                //unless, relative path is empty consider the first item as required file
                if (cursor.moveToFirst()) {
                    val mediaRelativePath = getMediaRelativePathFromCursor(cursor)
                    val mediaSize : Long = getMediaSizeFromCursor(cursor)
                    val mediaId : Long = getMediaIdFromCursor(cursor)
                    val mimeType : String? = getMediaMimeTypeFromCursor(cursor)
                    val isDurationRequired = isDurationRequired(mimeType)
                    val mediaType = getMediaTypeFromCursor(cursor)
                    val uri : Uri? = getFileUriByIdScope(mediaId, mediaType)
                    val duration : String? = if (isDurationRequired) getDurationFromUri(uri) else null
                    fileData = MTFileData(mediaId = mediaId, mediaRelativePath + displayName, relativePath = mediaRelativePath, displayName = displayName, mimeType = mimeType, duration = duration, size = mediaSize, uri = uri)
                }
            }
            cursor.close()
            return fileData;
        }
        return null
    }

    /**
     * To get file details by scope storage using uri
     *
     * @param uri       The uri of media
     * @return   The file details
     */
    fun Context.getFileDetailByScope(uri : Uri) : MTFileData? {
        val resolver: ContentResolver = contentResolver
        val projection = arrayOf(MediaStore.MediaColumns._ID, if (isOsHigherThenQ()) MediaStore.MediaColumns.RELATIVE_PATH else MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.DISPLAY_NAME)
        resolver.query(uri, projection, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            val fileData: MTFileData?
            val filePath: String?
            val relativePath: String?
            val displayName = getMediaDisplayNameFromCursor(cursor)
            if (isOsHigherThenQ()) { //if it is android Q, on the state get relative path and file path
                relativePath = getMediaRelativePathFromCursor(cursor)
                filePath = relativePath + displayName
            } else { //get relative path
                filePath = getMediaPathFromCursor(cursor)
                relativePath = filePath?.let { File(filePath).parent }
            }
            val mediaSize: Long = getMediaSizeFromCursor(cursor)
            val mediaId: Long = getMediaIdFromCursor(cursor)
            val mimeType: String? = getMediaMimeTypeFromCursor(cursor)
            val isDurationRequired = isDurationRequired(mimeType)
            val duration: String? = if (isDurationRequired) getDurationFromUri(uri) else null
            fileData = MTFileData(mediaId = mediaId, filePath = filePath, relativePath = relativePath, displayName = displayName, mimeType = mimeType, duration = duration, size = mediaSize, uri = uri)
            cursor.close()
            return fileData
        }
        return null
    }

    /**
     * To method called to create file by using scope storage through file path
     *
     * @param filePath          The file path
     * @param inputStream       The input stream
     * @return The file details
     */
    @SuppressLint("InlinedApi")
    @Throws(Exception::class)
    fun Context.createFileByScope(filePath: String, inputStream: InputStream) : MTFileData? {
        val file = File(filePath)
        var fileParentPath = file.parent
        if (fileParentPath != null && fileParentPath.isNotEmpty() && fileParentPath.startsWith("/")) {
            //Remove / in first field to avoid issue in the samsung device, m and a series we face this issues
            fileParentPath = fileParentPath.replaceFirst("/", "");
        }
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val fileContentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.RELATIVE_PATH, fileParentPath)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val fileUri = contentResolver.insert(collection, fileContentValues)
        return if (fileUri != null) {
            val outputStream = contentResolver.openOutputStream(fileUri)
            outputStream?.let {
                writeFileInDestination(inputStream, outputStream)
                fileContentValues.clear()
                fileContentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(fileUri, fileContentValues, null, null)
                getFileDetailByScope(fileUri)
            }
        } else {
            null
        }
    }

    /**
     * The method called to update file by using scope storage through file path
     *
     * @param filePath      The file path
     * @param inputStream   The input stream
     * @return  The file details
     */
    @Throws(Exception::class, SecurityException::class)
    fun Context.updateFileByScope(filePath: String, inputStream: InputStream): MTFileData? {
        val fileUri = getFileUriByScope(filePath)
        var fileData : MTFileData? = null
        if (fileUri != null) {
            fileData = updateUriByScope(fileUri, inputStream)
        }
        return fileData
    }

    /**
     * The method called to update uri by using scope storage through file uri
     *
     * @param fileUri       The file uri
     * @param inputStream   The input stream
     * @return  The file details
     */
    @Throws(Exception::class, SecurityException::class)
    fun Context.updateUriByScope(fileUri : Uri, inputStream: InputStream) : MTFileData? {
        val outputStream = contentResolver.openOutputStream(fileUri)
        outputStream?.let {
            writeFileInDestination(inputStream, it)
        }
        return getFileDetailByScope(uri = fileUri)
    }

    /**
     * The method called to delete file by scope storage through file path
     *
     * @param filePath      The file path
     * @return  The boolean determines that the file delete successfully
     */
    @Throws(Exception::class, SecurityException::class)
    fun Context.deleteFileByScope(filePath: String): Boolean {
        val mediaFile = getFileUriWithIdByScope(filePath)
        if (mediaFile?.mediaId != null) {
            if (mediaFile.uri != null) {
                deleteFileUsingMediaId(mediaFile.mediaId, mediaFile.uri)
            } else {
                deleteFileUsingMediaId(mediaFile.mediaId)
            }
            return true
        }
        return false
    }

    /**
     * The method called to delete file by scope storage through media id
     *
     * @param mediaFileId   The media id
     * @return  The boolean determines that the file delete successfully
     */
    @SuppressLint("InlinedApi")
    @Throws(Exception::class, SecurityException::class)
    private fun Context.deleteFileUsingMediaId(mediaFileId: Long): Boolean {
        val contentUri =  if (isOsHigherThenQ()) MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else MediaStore.Files.getContentUri("external")
        val selectArg = MediaStore.MediaColumns._ID + " LIKE ?"
        contentResolver.delete(contentUri, selectArg, arrayOf(mediaFileId.toString()))
        return true
    }

    /**
     * The method called to delete file using scope storag through media id
     *
     * @param mediaFileId       The media id
     * @param uri               The uri
     * @return  The boolean determines that the file is delete
     */
    private fun Context.deleteFileUsingMediaId(mediaFileId: Long, uri : Uri) : Boolean {
        val selectArg = MediaStore.MediaColumns._ID + " LIKE ?"
        contentResolver.delete(uri, selectArg, arrayOf(mediaFileId.toString()))
        return true
    }

    /**
     * The method called to get path by scope storage through uri
     *
     * @param uri      The uri
     * @return  The file path
     */
    fun Context.getUriPathByScope(uri: Uri): String? {
        var filePath : String? = null
        val resolver: ContentResolver = contentResolver
        val projection = arrayOf(if (isOsHigherThenQ()) MediaStore.MediaColumns.RELATIVE_PATH else MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
        resolver.query(uri, projection, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            filePath = if (isOsHigherThenQ()) {  //access the scope storage file greater than q
                val displayName = getMediaDisplayNameFromCursor(cursor)
                val relativePath = getMediaRelativePathFromCursor(cursor)
                relativePath + displayName
            } else {  //get file path using media file through data
                getMediaPathFromCursor(cursor)
            }
            cursor.close()
        }
        return filePath
    }

    /**
     * The method called to get uri path by scope storage through uri
     *
     * @param uri       The uri
     * @return  The media length
     */
    fun Context.getUriLengthByScope(uri: Uri) : Long? {
        var fileSize : Long? = null
        val resolver: ContentResolver = contentResolver
        val projection = arrayOf(MediaStore.MediaColumns.SIZE)
        resolver.query(uri, projection, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            fileSize = getMediaSizeFromCursor(cursor)
            cursor.close()
        }
        return fileSize
    }

    /**
     * The method called to get relative path by scope storage through uri
     *
     * @param uri      The uri
     * @return  The relative path
     */
    fun Context.getRelativePathByScope(uri: Uri) : String? {
        var relativePath : String? = null
        val resolver: ContentResolver = contentResolver
        val projection = arrayOf(if (isOsHigherThenQ()) MediaStore.MediaColumns.RELATIVE_PATH else MediaStore.MediaColumns.DATA)
        resolver.query(uri, projection, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            relativePath = if (isOsHigherThenQ()) { //get the relative path using new api in android Q
                getMediaRelativePathFromCursor(cursor)
            } else { //get the relative path using new api data
                val path = getMediaPathFromCursor(cursor)
                if (path != null) {
                    File(path).parent //parent file
                } else {
                    null
                }
            }
            cursor.close()
        }
        return relativePath
    }

    /**
     * The method called to get media id by scope storage through uri
     *
     * @param uri     The uri
     * @return  The media id
     */
    private fun Context.getUriMediaIdByScope(uri: Uri) : Long? {
        var mediaId : Long? = null
        val resolver: ContentResolver = contentResolver
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        resolver.query(uri, projection, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            mediaId = getMediaIdFromCursor(cursor)
            cursor.close()
        }
        return mediaId
    }

    /**
     * The method called to get display name by scope storage through uri
     *
     * @param uri   The uri
     * @return  The display name
     */
    fun Context.getDisplayNameByScope(uri: Uri) : String? {
        var displayName : String? = null
        val resolver: ContentResolver = contentResolver
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        resolver.query(uri, projection, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            displayName = getMediaDisplayNameFromCursor(cursor)
            cursor.close()
        }
        return displayName
    }

    /**
     * The method called to get media type  by scope storage through media id
     *
     * @param mediaId   The media id
     * @return The media type
     */
    private fun Context.getMediaTypeByScope(mediaId: Long) : Int? {
        val contentUri = if (isOsHigherThenQ()) MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else MediaStore.Files.getContentUri("external")
        val selectArg = MediaStore.MediaColumns._ID + " LIKE ?"
        val projection = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE)
        var mediaType : Int? = null
        contentResolver.query(contentUri, projection, selectArg, arrayOf(mediaId.toString()), null)?.use { cursor ->
            cursor.moveToFirst()
            mediaType = getMediaTypeFromCursor(cursor)
            cursor.close()
        }
        return mediaType
    }

    /**
     * The method called to get file uri by scope storage using media id
     *
     * @param mediaId   The media id
     * @return The uri of the file
     */
    fun Context.getMediaFileUriByScope(mediaId : Long) : Uri? {
        val mediaType = getMediaTypeByScope(mediaId)
        return getFileUriByIdScope(mediaId, mediaType)
    }

    /**
     * The method called to get media file length by scope storage using media id
     *
     * @param mediaId   The media id
     * @return The media length of the file
     */
    fun Context.getMediaFileLengthByScope(mediaId: Long) : Long? {
        val uri = getMediaFileUriByScope(mediaId)
        uri?.let {
           return getLengthFromUri(uri)
        }
        return null
    }

    /**
     * The method called to get media file duration by scope storage using media id
     *
     * @param mediaId      The media id
     * @return  The media duration of the file
     */
    fun Context.getMediaFileDurationByScope(mediaId: Long) : String? {
        val uri = getMediaFileUriByScope(mediaId)
        uri?.let {
            return getDurationFromUri(uri)
        }
        return null
    }

    /**
     * The method called to get media mime type by scope storage using media id
     *
     * @param mediaId       The media id
     * @return  The mime type of the file
     */
    fun Context.getMediaFileMimeTypeByScope(mediaId: Long) : String? {
        val uri = getMediaFileUriByScope(mediaId)
        uri?.let {
            return getMimeTypeFromUri(uri)
        }
        return null
    }

    /**
     * The method called to get media file input stream by scope storage using media id
     *
     * @param mediaId   The media id
     * @return  The input stream of the file
     */
    fun Context.getMediaFileInputStreamTypeByScope(mediaId: Long) : InputStream? {
        val uri = getMediaFileUriByScope(mediaId)
        uri?.let {
            return getInputStreamFromUri(uri)
        }
        return null
    }

    /**
     * The method called to get media file output stream by scope storage using media id
     *
     * @param mediaId   The media id
     * @return  The output stream of the file
     */
    fun Context.getMediaFileOutputStreamTypeByScope(mediaId: Long) : OutputStream? {
        val uri = getMediaFileUriByScope(mediaId)
        uri?.let {
            return getOutputStreamFromUri(uri)
        }
        return null
    }

    /**
     * The method called to get relative path by scope storage using media id
     *
     * @param mediaId   The media id
     * @return  The relative path of the file
     */
    fun Context.getMediaRelativePathByScope(mediaId: Long) : String? {
        val uri = getMediaFileUriByScope(mediaId)
        uri?.let {
            return getRelativePathByScope(uri)
        }
        return null
    }

    /**
     * The method called to get media display name by scope storage using media id
     *
     * @param mediaId   The media id
     * @return  The display name of the file
     */
    fun Context.getMediaDisplayNameByScope(mediaId: Long) : String? {
        val uri = getMediaFileUriByScope(mediaId)
        uri?.let {
            return getDisplayNameByScope(uri)
        }
        return null
    }

    /**
     * The method called to get media path by scope storage using media id
     *
     * @param mediaId   The media id
     * @return  The path
     */
    fun Context.getMediaPathByScope(mediaId: Long) : String? {
        val uri = getMediaFileUriByScope(mediaId)
        uri?.let {
            return getUriPathByScope(uri)
        }
        return null
    }

    /**
     * The method called to media detail by scope storage using media id
     *
     * @param mediaId   The media id
     * @return The media details
     */
    fun Context.getMediaDetailsByScope(mediaId: Long) : MTFileData? {
        val uri = getMediaFileUriByScope(mediaId)
        uri?.let {
            return getFileDetailByScope(uri)
        }
        return null
    }

    /**
     * The method called to delete media file by scope storage using media id
     *
     * @param mediaId   The media id
     */
    @Throws(Exception::class, SecurityException::class)
    fun Context.deleteMediaByScope(mediaId: Long) {
        val uri = getMediaFileUriByScope(mediaId)
        if (uri != null) {
            deleteFileUsingMediaId(mediaId, uri)
        } else {
            deleteFileUsingMediaId(mediaId)
        }
    }

    /**
     * The method called to update media by scope storage using media id
     *
     * @param mediaId       The media id
     * @param inputStream   The input stream
     * @return  The file details
     */
    @Throws(Exception::class, SecurityException::class)
    fun Context.updateMediaByScope(mediaId: Long, inputStream: InputStream): MTFileData? {
        val uri = getMediaFileUriByScope(mediaId)
        uri?.let {
            return updateUriByScope(uri, inputStream)
        }
        return null
    }

    /**
     * The method called to delete media by scope storage using uri
     *
     * @param  uri  The uri
     * @return  The boolean determines the file delete b
     */
    @Throws(Exception::class, SecurityException::class)
    fun Context.deleteUriByScope(uri: Uri): Boolean {
        val mediaId = getUriMediaIdByScope(uri)
        if (mediaId != null) {
            deleteMediaByScope(mediaId)
            return true
        }
        return false
    }

    /**
     * The method called to delete multiple item by scope storage using uri
     *
     * @param uriList       The uri list
     * @return  The intent sender
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun Context.deleteMultipleItem(uriList : List<Uri>): IntentSender? {
        return if (uriList.isNotEmpty()) {
            MediaStore.createDeleteRequest(contentResolver, uriList).intentSender
        } else {
            null
        }
    }

    /**
     * The method called to modify multiple item by scope storage using uri
     *
     * @param uriList       The uri list
     * @return The intent sender
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun Context.modifyMultipleItem(uriList : List<Uri>): IntentSender? {
        return if (uriList.isNotEmpty()) {
            MediaStore.createWriteRequest(contentResolver, uriList).intentSender
        } else {
            null
        }
    }

    /**
     * To get media display name using cursor
     *
     * @param cursor    The cursor
     * @return The display name
     */
    private fun getMediaDisplayNameFromCursor(cursor: Cursor): String? {
        val displayIdIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
        return cursor.getString(displayIdIndex)
    }

    /**
     * To get media path using cursor
     *
     * @param cursor    The cursor
     * @return The path
     */
    private fun getMediaPathFromCursor(cursor: Cursor): String? {
        val mediaPathIdIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        return cursor.getString(mediaPathIdIndex)
    }

    /**
     * To get media relative path using cursor
     *
     * @param cursor    The cursor
     * @return The relative path
     */
    @SuppressLint("InlinedApi")
    private fun getMediaRelativePathFromCursor(cursor: Cursor): String? {
        val mediaRelativePathIdIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
        return cursor.getString(mediaRelativePathIdIndex)
    }

    /**
     * To get media id using cursor
     *
     * @param cursor    The cursor
     * @return The media id
     */
    private fun getMediaIdFromCursor(cursor: Cursor): Long {
        val mediaIdIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
        return cursor.getLong(mediaIdIndex)
    }

    /**
     * To get media type using cursor
     *
     * @param cursor    The cursor
     * @return The media type
     */
    private fun getMediaTypeFromCursor(cursor: Cursor) : Int {
        val mediaTypeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
        return cursor.getInt(mediaTypeIndex)
    }

    /**
     * To get media size using cursor
     *
     * @param cursor    The cursor
     * @return The media size
     */
    private fun getMediaSizeFromCursor(cursor: Cursor) : Long {
        val mediaIdIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
        return cursor.getLong(mediaIdIndex)
    }

    /**
     * To get media mime type using cursor
     *
     * @param cursor    The cursor
     * @return The mime type
     */
    private fun getMediaMimeTypeFromCursor(cursor: Cursor): String? {
        val mimeTypeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
        return cursor.getString(mimeTypeIndex)
    }

    /**
     * The method called to get path using provider uri
     *
     * @param uri       The uri
     * @return  The valid path
     */
    @SuppressLint("NewApi")
    fun Context.getPathUsingProviderUri(uri : Uri) : String? {
        val isKitKat: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
            // ExternalStorageProvider
            when {
                isExternalStorageDocument(uri) -> { //check whether it is external storage directory
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    return if (PRIMARY.equals(type, ignoreCase = true)) {
                        Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    } else { // non-primary volumes e.g sd card
                        var filePath = "non"
                        //getExternalMediaDirs() added in API 21
                        val externalStorage = externalMediaDirs
                        for (f in externalStorage) {
                            filePath = f.absolutePath
                            if (filePath.contains(type)) {
                                val endIndex = filePath.indexOf("Android")
                                filePath = filePath.substring(0, endIndex) + split[1]
                            }
                        }
                        filePath
                    }
                }
                isDownloadsDocument(uri) -> { //check whether it is download documents
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse(PUBLIC_DOWNLOAD_AUTHORITY), java.lang.Long.valueOf(id)
                    )
                    return getDataColumn(contentUri, null, null)
                }
                isMediaDocument(uri) -> { //check whether it is media document
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    when (type) {
                        IMAGE -> { //image type
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        VIDEO -> { //video type
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        AUDIO -> { //audio type
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                    }
                    val selection = MediaStore.MediaColumns._ID + " LIKE ?"
                    val selectionArgs = arrayOf(split[1])
                    if (contentUri != null) {
                        return getDataColumn(contentUri, selection, selectionArgs)
                    }
                }
            }
        } else if (CONTENT.equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(uri, null, null)
        } else if (FILE.equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun Context.getDataColumn(uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
        val projection = arrayOf(if (isOsHigherThenQ()) MediaStore.MediaColumns.RELATIVE_PATH else MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
        try {
            contentResolver.query(uri, projection, selection, selectionArgs, null)?.let {
                it.moveToFirst()
                val path : String = if (isOsHigherThenQ()) {
                    val relativePath = getMediaRelativePathFromCursor(cursor = it)
                    val displayName = getMediaDisplayNameFromCursor(it)
                    relativePath + displayName
                } else {
                    getMediaPathFromCursor(cursor = it).toString()
                }
                it.close()
                return path
            }
        } catch (e: Exception) {
            Log.e("Scope Storage Exception", e.message, e)
        }
        return null
    }

    /**
     * The method called to check whether is external storage
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return EXTERNAL_STORAGE_DOCUMENT_AUTHORITY == uri.authority
    }

    /**
     * The method called to check whether is download documents
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return DOWNLOAD_STORAGE_DOCUMENT_AUTHORITY == uri.authority
    }

    /**
     * The method called to check whether is media document
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return  MEDIA_STORAGE_DOCUMENT_AUTHORITY == uri.authority
    }
}