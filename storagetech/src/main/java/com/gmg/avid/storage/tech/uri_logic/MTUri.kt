package com.gmg.avid.storage.tech.uri_logic

import android.net.Uri
import com.gmg.avid.storage.tech.common.MTBaseStorage
import com.gmg.avid.storage.tech.common.MTFileData
import com.gmg.avid.storage.tech.common.MTStorageAppModule
import com.gmg.avid.storage.tech.filepath_logic.MTFile
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getPathUsingProviderUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.deleteFileFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getDisplayNameFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getDurationFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getFileDetailsFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getInputStreamFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getLengthFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getMimeTypeFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getOutputStreamFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getPathFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getProvideUriPath
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getRelativePathFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.updateFileFromUri
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

/**
 * This class handle the uri logic
 */
class MTUri(private val uri: Uri) : MTBaseStorage() {

    companion object {
       private val applicationContext = MTStorageAppModule.getAppTechContext()

        fun getPathFromProviderUri(uri: Uri?): String? {
            return if (uri != null && applicationContext != null) {
                applicationContext.getProvideUriPath(uri)
            } else {
                null
            }
        }
    }

    /**
     * The method called to initial setup
     *
     * @return The boolean determine the error on the operation starting
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
     * The method called to get path
     *
     * @return  the file path
     */
    fun getPath() : String? {
        return if (initialChecks()) {
            context?.getPathFromUri(uri)
        } else {
            null
        }
    }

    /**
     * The method called to get length
     *
     * @return  the file length
     */
    fun getLength() : Long? {
        return if (initialChecks()) {
            context?.getLengthFromUri(uri)
        } else {
            null
        }
    }

    /**
     * The method called to get duration
     *
     * @return  The duration
     */
    fun getDuration() : String? {
        return if (initialChecks()) {
            context?.getDurationFromUri(uri)
        } else {
            null
        }
    }

    /**
     * The method called to get mime type
     *
     * @return  The mime type
     */
    fun getMimetype() : String? {
        return if (initialChecks()) {
            context?.getMimeTypeFromUri(uri)
        } else {
            null
        }
    }

    /**
     * The method called to get input stream
     *
     * @return The input stream
     */
    fun getInputStream() : InputStream? {
        return if (initialChecks()) {
            context?.getInputStreamFromUri(uri)
        } else {
            null
        }
    }

    /**
     * The method called to get ouput stream
     *
     * @return  The output stream
     */
    fun getOutputStream() : OutputStream? {
        return if (initialChecks()) {
            context?.getOutputStreamFromUri(uri)
        } else {
            null
        }
    }

    /**
     * The method called to get relative path
     *
     * @return The relative path
     */
    fun getRelativePath() : String? {
       return if (initialChecks()) {
           context?.getRelativePathFromUri(uri)
       } else {
           null
       }
    }

    /**
     * The method called to get display name
     *
     * @return  The display name
     */
    fun getDisplayName() : String? {
        return if (initialChecks()) {
            context?.getDisplayNameFromUri(uri)
        } else {
            null
        }
    }

    /**
     * The method called to get uri details
     *
     * @return  The uri details
     */
    fun getUriDetails() : MTFileData? {
        return if (initialChecks()) {
            context?.getFileDetailsFromUri(uri)
        } else {
            null
        }
    }

    /**
     * The method called to create new file
     *
     * @param path   The file path
     * @return  The file details
     */
    fun createNewFile(path : String): MTFileData? {
        val file = MTFile(path)
        file.initializeErrorCallBack(errorCallback?.fileErrorResultCallback)
        file.initializeSuccessCallBack(successCallback?.fileSuccessResultCallback)
        file.setSourceUri(uri)
        return file.createFile()
    }

    /**
     * The method called to update file
     *
     * @return  The file details
     */
    fun updateFile(): MTFileData? {
        if (initialChecks()) {
            if (sourceInputStm == null) {
                errorCallback?.throwException(SOURCE_IS_MISSING)
            } else {
                try {
                    val fileData = context?.updateFileFromUri(uri, sourceInputStm!!)
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

    /**
     * The method called to delete file
     */
    fun delete() {
        if (initialChecks()) {
            try {
                val isDeleted =  context?.deleteFileFromUri(uri)
                if (isDeleted != null && !isDeleted) {
                    errorCallback?.throwException(SOME_THING_WENT_WRONG)
                }
            } catch (securityException: SecurityException) {
                errorCallback?.throwSecurityException(securityException)
            } catch (exception: Exception) {
                errorCallback?.throwException(exception)
            }
        }
    }
}