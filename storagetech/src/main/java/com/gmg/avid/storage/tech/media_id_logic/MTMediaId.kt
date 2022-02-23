package com.gmg.avid.storage.tech.media_id_logic

import android.app.RecoverableSecurityException
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.gmg.avid.storage.tech.common.MTBaseStorage
import com.gmg.avid.storage.tech.common.MTFileData
import com.gmg.avid.storage.tech.common.MTStorageAppModule
import com.gmg.avid.storage.tech.filepath_logic.MTFile
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.deleteFileFromMediaId
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.getDisplayNameFromMediaId
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.getDurationFromMediaId
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.getFileDetailsFromMediaId
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.getFilePathFromMediaId
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.getInputStreamFromMediaId
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.getLengthFromMediaId
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.getMimeTypeFromMediaId
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.getOutputStreamFromMediaId
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.getRelativePathFromMediaId
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.getUriFromMediaId
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.isFileAvailableInDevice
import com.gmg.avid.storage.tech.media_id_logic.MTMediaIdHandler.updateFileFromMediaId
import com.gmg.avid.storage.tech.storage_logic.MTStorageDirectoryHandler.isOsHigherThenQ
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

/**
 * The class to handle media id logic
 */
class MTMediaId(private val mediaId : Long) : MTBaseStorage() {

    /**
     * The method called to check initial check for running library
     *
     * @return the boolean determines whether the operation is continue
     */
    private fun initialChecks(): Boolean {
        context = MTStorageAppModule.getAppTechContext()
        if (context == null) {
            errorCallback?.throwException(CONTEXT_IS_MISSING)
            return false
        }
        return true
    }

    /**
     * The method called to check initial check for running library
     *
     * @return the boolean determines whether the operation is continue
     */
    private fun initialCheckWithExist(): Boolean {
        context = MTStorageAppModule.getAppTechContext()
        if (context == null) {
            errorCallback?.throwException(CONTEXT_IS_MISSING)
            return false
        } else if (!isFileExist()) {
            errorCallback?.throwException(FILE_NOT_FOUND)
            return false
        }
        return true
    }

    /**
     * The method called to check file exist
     *
     * @return  the boolean determine the file exist state
     */
    fun isFileExist(): Boolean {
        return if (initialChecks()) {
            context!!.isFileAvailableInDevice(mediaId)
        } else {
            false
        }
    }

    /**
     * The method called to get uri from media id
     *
     * @return  the file uri
     */
    fun getUri() : Uri? {
        return if (initialCheckWithExist()) {
            context!!.getUriFromMediaId(mediaId)
        } else {
            null
        }
    }

    /**
     * The method called to get length from media id
     *
     * @return  the file length
     */
    fun getLength() : Long? {
        return if (initialCheckWithExist()) {
            context!!.getLengthFromMediaId(mediaId)
        } else {
            null
        }
    }

    /**
     * The method called to get duration from media id
     *
     * @return  the file duration
     */
    fun getDuration() : String? {
        return if (initialCheckWithExist()) {
            context!!.getDurationFromMediaId(mediaId)
        } else {
            null
        }
    }

    /**
     * The method called to get mime type from media id
     *
     * @return  the mime type
     */
    fun getMimeType() : String? {
        return if (initialCheckWithExist()) {
            context!!.getMimeTypeFromMediaId(mediaId)
        } else {
            null
        }
    }

    /**
     * The method called to get input stream from media id
     *
     * @return  the input stream
     */
    fun getInputStream() : InputStream? {
        return if (initialCheckWithExist()) {
            context!!.getInputStreamFromMediaId(mediaId)
        } else {
            null
        }
    }

    /**
     * The method called to get output stream from media id
     *
     * @return  the output stream
     */
    fun getOutputStream() : OutputStream? {
        return if (initialCheckWithExist()) {
            context!!.getOutputStreamFromMediaId(mediaId)
        } else {
            null
        }
    }

    /**
     * The method called to get relative path from media id
     *
     * @return  The relative path
     */
    fun getRelativePath() : String? {
        return if (initialCheckWithExist()) {
            context!!.getRelativePathFromMediaId(mediaId)
        } else {
            null
        }
    }

    /**
     * The method called to get display name from media id
     *
     * @return  The display name
     */
    fun displayName() : String? {
        return if (initialCheckWithExist()) {
            context!!.getDisplayNameFromMediaId(mediaId)
        } else {
            null
        }
    }

    /**
     * The method called to get path from media id
     *
     * @return The path
     */
    fun getPath() : String? {
        return if (initialCheckWithExist()) {
            context!!.getFilePathFromMediaId(mediaId)
        } else {
            null
        }
    }

    /**
     * The method called to get media id details
     *
     * @return  The media details
     */
    fun getMediaIdDetails() : MTFileData? {
        return if (initialCheckWithExist()) {
            context!!.getFileDetailsFromMediaId(mediaId)
        } else {
            null
        }
    }

    /**
     * The method called to create new file from media id
     *
     * @return  The media details
     */
    fun createFile(path : String): MTFileData? {
        return if (initialCheckWithExist()) {
            val file = MTFile(path)
            file.initializeErrorCallBack(errorCallback?.fileErrorResultCallback)
            file.initializeSuccessCallBack(successCallback?.fileSuccessResultCallback)
            file.setSourceMediaId(mediaId)
            file.createFile()
        } else {
            null
        }
    }

    /**
     * The method called to delete file from media id
     */
    fun delete() {
        if (initialCheckWithExist()) {
            try {
               context?.deleteFileFromMediaId(mediaId)
            } catch (securityException: SecurityException) {
                errorCallback?.throwSecurityException(securityException)
            } catch (exception: Exception) {
                errorCallback?.throwException(exception)
            }
        }
    }

    /**
     * The method called to update file from media id
     *
     * @return  The media details
     */
    fun updateFile(): MTFileData? {
        if (initialCheckWithExist()) {
            if (sourceInputStm == null) {
                errorCallback?.throwException(SOURCE_IS_MISSING)
            } else {
                try {
                    val fileData = context?.updateFileFromMediaId(mediaId, sourceInputStm!!)
                    if (fileData != null) {
                        errorCallback?.throwException(SOME_THING_WENT_WRONG)
                    }
                    return fileData
                } catch (securityException: SecurityException) {
                    errorCallback?.throwSecurityException(securityException)
                } catch (exception: Exception) {
                    errorCallback?.throwException(exception)
                }
            }
        }
        return null
    }
}