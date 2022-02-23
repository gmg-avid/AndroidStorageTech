package com.gmg.avid.storage.tech.uri_logic

import android.content.ContentResolver
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.webkit.MimeTypeMap
import com.gmg.avid.storage.tech.common.MTFileData
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.deleteUriByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getDisplayNameByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getFileDetailByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getPathUsingProviderUri
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getRelativePathByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getUriLengthByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getUriPathByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.updateUriByScope
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.*

/**
 * This object class handle the uri logic
 */
object MTUriOperationHandler {

    /**
     * The method called to get duration form uri
     *
     * @param uri       The uri
     * @return The duration from uri
     */
    fun Context.getDurationFromUri(uri : Uri?) : String {
        if (uri != null) {
            return try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(this, uri)
                val duration =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                        .toLong()
                retriever.release()
                val seconds = (duration / 1000).toInt() % 60
                val minutes = (duration / (1000 * 60) % 60).toInt()
                val hours = (duration / (1000 * 60 * 60) % 24).toInt()
                String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds)
            } catch (exception: Exception) {
                "00:00:00"
            }
        } else {
            return "00:00:00"
        }
    }

    /**
     * The method called to get path from uri
     *
     * @param uri       The uri
     * @return The file path
     */
    fun Context.getPathFromUri(uri: Uri) : String? {
        return getUriPathByScope(uri)
    }

    /**
     * The method called to get length from uri
     *
     * @param uri   The uri
     * @return  The file length
     */
    fun Context.getLengthFromUri(uri: Uri) : Long? {
        return getUriLengthByScope(uri)
    }

    /**
     * The method called to get mime type from uri
     *
     * @param uri   The uri
     * @return  The mime type from uri
     */
    fun Context.getMimeTypeFromUri(uri: Uri) : String? {
        return if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            contentResolver.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase(Locale.getDefault()))
        }
    }

    /**
     * The method called to get relative  path from uri
     *
     * @param uri   The uri
     * @return  The relative path from uri
     */
    fun Context.getRelativePathFromUri(uri: Uri) : String? {
        return getRelativePathByScope(uri)
    }

    /**
     * The method called to get display name from uri
     *
     * @param uri   The uri
     * @return  The display name from uri
     */
    fun Context.getDisplayNameFromUri(uri: Uri) : String? {
        return getDisplayNameByScope(uri)
    }

    /**
     * The method called to get input stream from uri
     *
     * @param uri   The uri
     * @return  The input stream from uri
     */
    fun Context.getInputStreamFromUri(uri: Uri) : InputStream? {
        return contentResolver.openInputStream(uri)
    }

    /**
     * The method called to get output stream from uri
     *
     * @param uri   The uri
     * @return  The output stream from uri
     */
    fun Context.getOutputStreamFromUri(uri: Uri) : OutputStream? {
        return contentResolver.openOutputStream(uri)
    }

    /**
     * The method called to get file details from uri
     *
     * @param uri   The uri
     * @return  The file details from uri
     */
    fun Context.getFileDetailsFromUri(uri: Uri)  : MTFileData? {
        return getFileDetailByScope(uri)
    }

    /**
     * The method called to update file from uri
     *
     * @param uri               The uri
     * @param inputStream       The input stream
     * @return  the file details from uri
     */
    @Throws(Exception::class, SecurityException::class)
    fun Context.updateFileFromUri(uri: Uri, inputStream: InputStream) : MTFileData? {
        return updateUriByScope(uri, inputStream)
    }

    /**
     * The method called to delete file from uri
     *
     * @param uri       The uri
     * @return The boolean that determines the file is delete or not
     */
    @Throws(Exception::class, SecurityException::class)
    fun Context.deleteFileFromUri(uri: Uri): Boolean {
        return deleteUriByScope(uri)
    }

    fun Context.getProvideUriPath(uri: Uri) : String? {
        return getPathUsingProviderUri(uri)
    }
}