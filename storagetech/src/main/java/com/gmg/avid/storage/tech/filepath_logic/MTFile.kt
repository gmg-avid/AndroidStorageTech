package com.gmg.avid.storage.tech.filepath_logic

import android.net.Uri
import android.text.TextUtils
import com.gmg.avid.storage.tech.common.MTBaseStorage
import com.gmg.avid.storage.tech.common.MTFileData
import com.gmg.avid.storage.tech.common.MTStorageAppModule.getAppTechContext
import com.gmg.avid.storage.tech.filepath_logic.MTFilePathHandler.createFileInPath
import com.gmg.avid.storage.tech.filepath_logic.MTFilePathHandler.deleteFileByPath
import com.gmg.avid.storage.tech.filepath_logic.MTFilePathHandler.getDurationFromPath
import com.gmg.avid.storage.tech.filepath_logic.MTFilePathHandler.getFileDetailsFromPath
import com.gmg.avid.storage.tech.filepath_logic.MTFilePathHandler.getInputStreamFromPath
import com.gmg.avid.storage.tech.filepath_logic.MTFilePathHandler.getLengthFromPath
import com.gmg.avid.storage.tech.filepath_logic.MTFilePathHandler.getMimeTypeFromPath
import com.gmg.avid.storage.tech.filepath_logic.MTFilePathHandler.getOutputStreamFromPath
import com.gmg.avid.storage.tech.filepath_logic.MTFilePathHandler.getUriFromPath
import com.gmg.avid.storage.tech.filepath_logic.MTFilePathHandler.isFileAvailableInDevice
import com.gmg.avid.storage.tech.filepath_logic.MTFilePathHandler.updateFileInPath
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

/**
 * The class to handle file logic
 */
class MTFile(private val filePath : String) : MTBaseStorage() {

    private val file : File by lazy { File(filePath) }

    /**
     * The method called to check initial check for running library
     *
     * @return the boolean determines whether the operation is continue
     */
    private fun initialChecks(): Boolean {
        context = getAppTechContext()
        if (context == null) {  //context missing throw error
            errorCallback?.throwException(CONTEXT_IS_MISSING)
            return false
        } else if (TextUtils.isEmpty(filePath)) {  //file path empty throw error
            errorCallback?.throwException(FILE_PATH_IS_EMPTY)
            return false
        }
        return true
    }

    /**
     * The method called to check initial check with exist
     *
     * @return  The boolean determine whether the operation is continue, if existing check
     */
    private fun initialCheckWithExist(): Boolean {
        context = getAppTechContext()
        if (context == null) {
            errorCallback?.throwException(CONTEXT_IS_MISSING)
            return false
        } else if (TextUtils.isEmpty(filePath)) {
            errorCallback?.throwException(FILE_PATH_IS_EMPTY)
            return false
        } else if (!isFileExist()) {
            errorCallback?.throwException(FILE_NOT_FOUND)
            return false
        }
        return true
    }

    /**
     * The method called to check whether the file is exist
     *
     * @return  The boolean determines the file is exist or not
     */
    fun isFileExist(): Boolean {
        return if (initialChecks()) {
             context!!.isFileAvailableInDevice(filePath)
        } else {
            false
        }
    }

    /**
     * The method called to get uri from file
     *
     * @return  The uri of the file
     */
    fun getUri() : Uri? {
        return if (initialCheckWithExist()) {
            context!!.getUriFromPath(filePath)
        } else {
            null
        }
    }

    /**
     * The method called to get the length of the file
     *
     * @return The file length
     */
    fun getLength() : Long? {
        return if (initialCheckWithExist()){
            context!!.getLengthFromPath(filePath)
        } else {
            null
        }
    }

    /**
     * The method called to get the duration of the file
     *
     * @return  The file duration
     */
    fun getDuration() : String? {
        return if (initialCheckWithExist()) {
            context!!.getDurationFromPath(filePath)
        } else {
            null
        }
    }

    /**
     * The method called to get the mime type of the file
     *
     * @return The mime type
     */
    fun getMimeType() : String? {
        return if (initialCheckWithExist()) {
            context!!.getMimeTypeFromPath(filePath)
        } else {
            null
        }
    }

    /**
     * The method called to get input stream of the file
     *
     * @return the input stream
     */
    fun getInputStream() : InputStream? {
        return if (initialCheckWithExist()) {
            context!!.getInputStreamFromPath(filePath)
        } else {
            null
        }
    }

    /**
     * The method called to get output stream of the file
     *
     * @return the output stream
     */
    fun getOutputStream() : OutputStream? {
        return if (initialCheckWithExist()) {
            context!!.getOutputStreamFromPath(filePath)
        } else {
            null
        }
    }

    /**
     * The method called to get relative path of the file
     *
     * @return the relative path
     */
    fun getRelativePath() : String? {
        return file.parent
    }

    /**
     * The method called to get display name of the file
     *
     * @return the display name
     */
    fun getDisplayName() : String {
        return file.name
    }

    /**
     * The method called to get file details of the file
     *
     * @return the file details
     */
    fun getFileDetails() : MTFileData? {
        return if (initialCheckWithExist()) {
            context!!.getFileDetailsFromPath(filePath)
        } else {
            null
        }
    }

    /**
     * The method called to create file
     *
     * @return the file details
     */
    fun createFile(): MTFileData? {
        var fileData : MTFileData? = null
        if (initialChecks()) {
            if (sourceInputStm == null) {
                errorCallback?.throwException(SOURCE_IS_MISSING)
            } else {
                try {
                    fileData = context?.createFileInPath(filePath, sourceInputStm!!)
                    if (fileData == null) {
                        errorCallback?.throwException(SOME_THING_WENT_WRONG)
                    }
                } catch (exception : Exception) {
                    errorCallback?.throwException(exception)
                }
            }
        }
        return fileData
    }


    /**
     * The method called to delete file
     */
    fun deleteFile() {
        if (initialCheckWithExist()) {
            try {
                val isDelete = context?.deleteFileByPath(filePath)
                if (isDelete == null || !isDelete) {
                    errorCallback?.throwException(SOME_THING_WENT_WRONG)
                }
            } catch (securityException: SecurityException) {
                errorCallback?.throwSecurityException(securityException)
            } catch (exception: Exception) {
                errorCallback?.throwException(exception)
            }
        }
    }

    /**
     * The method called to update file
     *
     * @return the file details
     */
    fun updateFile(): MTFileData? {
        if (initialCheckWithExist()) {
            if (sourceInputStm == null) {
                errorCallback?.throwException(SOURCE_IS_MISSING)
            } else {
                try {
                    val fileData = context?.updateFileInPath(filePath, sourceInputStm!!)
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

