package com.gmg.avid.storage.tech.media_id_logic

import android.content.Context
import android.net.Uri
import com.gmg.avid.storage.tech.common.MTFileData
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.deleteMediaByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getMediaDetailsByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getMediaDisplayNameByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getMediaFileDurationByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getMediaFileInputStreamTypeByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getMediaFileLengthByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getMediaFileMimeTypeByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getMediaFileOutputStreamTypeByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getMediaFileUriByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getMediaPathByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getMediaRelativePathByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.isMediaFileExistByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.updateMediaByScope
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

/**
 * This object class contains media id related logic
 */
object MTMediaIdHandler {

    /**
     * The method called to check whether file is present in device or not
     *
     * @param mediaId  The media id
     * @return  The boolean that determines whether the file is present in device
     */
    fun Context.isFileAvailableInDevice(mediaId : Long): Boolean {
        return isMediaFileExistByScope(mediaId)
    }

    /**
     * The method called to get uri from media id
     *
     * @param mediaId   The media id
     * @return  The uri from media id
     */
    fun Context.getUriFromMediaId(mediaId: Long) : Uri? {
        return getMediaFileUriByScope(mediaId)
    }

    /**
     * The method called to get length from media id
     *
     * @param mediaId    The media id
     * @return  The length from media id
     */
    fun Context.getLengthFromMediaId(mediaId: Long) : Long? {
        return getMediaFileLengthByScope(mediaId)
    }

    /**
     * The method called to get duration from media id
     *
     * @param mediaId  The media id
     * @return The duration from media id
     */
    fun Context.getDurationFromMediaId(mediaId: Long) : String? {
        return getMediaFileDurationByScope(mediaId)
    }

    /**
     * The method called to get mime type from media id
     *
     * @param mediaId    The media id
     * @return  The mime type from media id
     */
    fun Context.getMimeTypeFromMediaId(mediaId: Long) : String? {
        return getMediaFileMimeTypeByScope(mediaId)
    }

    /**
     * The method called to get input stream from media id
     *
     * @param mediaId   The media id
     * @return  The input stream from media id
     */
    fun Context.getInputStreamFromMediaId(mediaId: Long) : InputStream? {
        return getMediaFileInputStreamTypeByScope(mediaId)
    }

    /**
     * The method called to get output stream from media id
     *
     * @param mediaId   The media id
     * @return  The output stream from media id
     */
    fun Context.getOutputStreamFromMediaId(mediaId: Long) : OutputStream? {
        return getMediaFileOutputStreamTypeByScope(mediaId)
    }

    /**
     * The method called to get relative path from media id
     *
     * @param mediaId   The media id
     * @return The relative path from media id
     */
    fun Context.getRelativePathFromMediaId(mediaId: Long) : String? {
        return getMediaRelativePathByScope(mediaId)
    }

    /**
     * The method called to get display name from media id
     *
     * @param mediaId   The media id
     * @return  The display name from media id
     */
    fun Context.getDisplayNameFromMediaId(mediaId: Long) : String? {
        return getMediaDisplayNameByScope(mediaId)
    }

    /**
     * The method called to get file path from media id
     *
     * @param mediaId     The media id
     * @return  The file path from media id
     */
    fun Context.getFilePathFromMediaId(mediaId: Long) : String? {
        return getMediaPathByScope(mediaId)
    }

    /**
     * The method called to get file details from media id
     *
     * @param mediaId   The media id
     * @return  The file details from media id
     */
    fun Context.getFileDetailsFromMediaId(mediaId: Long) : MTFileData? {
        return getMediaDetailsByScope(mediaId)
    }

    /**
     * The method called to delete file details from media id
     *
     * @param mediaId     The media id
     * @return  The delete file from media id
     */
    @Throws(Exception::class, SecurityException::class)
    fun Context.deleteFileFromMediaId(mediaId: Long) {
        deleteMediaByScope(mediaId)
    }

    /**
     * The method called to update file from media id
     *
     * @param mediaId       The media id
     * @param inputStream   The input stream
     * @return  The update file from media id
     */
    @Throws(Exception::class, SecurityException::class)
    fun Context.updateFileFromMediaId(mediaId: Long, inputStream: InputStream): MTFileData? {
        return updateMediaByScope(mediaId, inputStream)
    }
}