package com.gmg.avid.storage.tech.filepath_logic

import android.content.Context
import android.net.Uri
import com.gmg.avid.storage.tech.common.MTFileData
import com.gmg.avid.storage.tech.storage_logic.MTDefaultStorageOperation
import com.gmg.avid.storage.tech.storage_logic.MTDefaultStorageOperation.createFileByDefault
import com.gmg.avid.storage.tech.storage_logic.MTDefaultStorageOperation.deleteFileByDefault
import com.gmg.avid.storage.tech.storage_logic.MTDefaultStorageOperation.getFileDetailByDefault
import com.gmg.avid.storage.tech.storage_logic.MTDefaultStorageOperation.updateFileByDefault
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.createFileByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.deleteFileByScope
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getDurationFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getInputStreamFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getMimeTypeFromUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getOutputStreamFromUri
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getFileDetailByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getFileLengthByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getFileUriByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.isFileExistByScope
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.updateFileByScope
import com.gmg.avid.storage.tech.storage_logic.MTStorageDirectoryHandler.isScopeStorageRequiredToAccessFile
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

/**
 * This object class contain file path related logic
 */
object MTFilePathHandler {

    /**
     * This method called to check whether file is present in device or not
     *
     * @param filePath      The file path need to check the whether exist
     * @return      The boolean that determines the exist state
     */
    fun Context.isFileAvailableInDevice(filePath: String): Boolean {
        return if (isFileAccessByScopeStorage(filePath)) {
            //this case execute if path required to access by scope storage logic
            isFileExistByScope(filePath)
        } else {
            MTDefaultStorageOperation.isFileExistByDefault(filePath)
        }
    }

    /**
     * The method to check whether the file is required access by scope storage
     *
     * @param  filePath     The file path
     * @return  The boolean determine the file access by scope storage
     */
    private fun Context.isFileAccessByScopeStorage(filePath: String) : Boolean {
        return isScopeStorageRequiredToAccessFile(filePath)
    }

    /**
     * The method called to get uri from file path
     *
     * @param filePath      The file path
     * @return   The uri of the path
     */
    fun Context.getUriFromPath(filePath: String) : Uri? {
        return if (isFileAccessByScopeStorage(filePath)) {
            //this case execute if path required to access by scope storage logic
            getFileUriByScope(filePath)
        } else {
            MTDefaultStorageOperation.getFileUriByDefault(filePath)
        }
    }

    /**
     * The method called to get length from file path
     *
     * @param filePath      The file path
     * @return  the length of the path
     */
    fun Context.getLengthFromPath(filePath: String) : Long? {
        return if (isFileAccessByScopeStorage(filePath)) {
            //this case execute if path required to access by scope storage logic
            getFileLengthByScope(filePath)
        } else {
            MTDefaultStorageOperation.getFileLengthByDefault(filePath)
        }
    }

    /**
     * The method called to get duration from file path
     *
     * @param filePath      The file path
     * @return  the duration of the path
     */
    fun Context.getDurationFromPath(filePath: String) : String? {
        val uri : Uri? = getUriFromPath(filePath)
        return if (uri != null) {
            getDurationFromUri(uri)
        } else {
            null
        }
    }

    /**
     * The method called to get mime type from file path
     *
     * @param filePath      The file path
     * @return  the mime type of the path
     */
    fun Context.getMimeTypeFromPath(filePath: String) : String? {
        val uri : Uri? = getUriFromPath(filePath)
        return if (uri != null) {
            getMimeTypeFromUri(uri)
        } else {
            null
        }
    }

    /**
     * The method called to get input stream from file path
     *
     * @param filePath      The file path
     * @return  the input stream of the path
     */
    fun Context.getInputStreamFromPath(filePath: String) : InputStream? {
        val uri : Uri? = getUriFromPath(filePath)
        return if (uri != null) {
            getInputStreamFromUri(uri);
        } else {
            null
        }
    }

    /**
     * The method called to get output stream from file path
     *
     * @param filePath      The file path
     * @return  the output stream of the path
     */
    fun Context.getOutputStreamFromPath(filePath: String) : OutputStream? {
        val uri : Uri? = getUriFromPath(filePath)
        return if (uri != null) {
            getOutputStreamFromUri(uri)
        } else {
            null
        }
    }

    /**
     * The method called to get file details from file path
     *
     * @param filePath      The file path
     * @return  the file details from path
     */
    fun Context.getFileDetailsFromPath(filePath: String) : MTFileData? {
       return if (isFileAccessByScopeStorage(filePath)) {
           //this case execute if path required to access by scope storage logic
            getFileDetailByScope(filePath)
        } else {
           getFileDetailByDefault(filePath)
        }
    }

    /**
     * The method  create file from path
     *
     * @param filePath      The file path
     * @param inputStream   The input stream
     * @return  the file details
     * @throws  Exception file exceptions
     */
    @Throws(Exception::class)
    fun Context.createFileInPath(filePath: String, inputStream: InputStream): MTFileData? {
        return if (isFileAccessByScopeStorage(filePath)) {
            //this case execute if path required to access by scope storage logic
            createFileByScope(filePath, inputStream)
        } else {
            createFileByDefault(filePath, inputStream)
        }
    }

    /**
     * The method update file from path by scope storage
     *
     * @param filePath      The file path
     * @param inputStream   The input stream
     * @return  the file details
     * @throws  Exception file exceptions
     */
    @Throws(Exception::class, SecurityException::class)
    fun Context.updateFileInPath(filePath: String, inputStream: InputStream): MTFileData? {
        return if (isFileAccessByScopeStorage(filePath)) {
            //this case execute if path required to access by scope storage logic
            updateFileByScope(filePath, inputStream)
        } else {
            updateFileByDefault(filePath, inputStream)
        }
    }

    /**
     * The method delete file from path by scope storage
     *
     * @param filePath      The file path
     * @return  the file details
     * @throws  Exception file exceptions
     */
    @Throws(Exception::class, SecurityException::class)
    fun Context.deleteFileByPath(filePath: String): Boolean {
        return if (isFileAccessByScopeStorage(filePath)) {
            //this case execute if path required to access by scope storage logic
            deleteFileByScope(filePath)
        } else {
            deleteFileByDefault(filePath)
        }
    }
}
