package com.gmg.avid.storage.tech.common

import android.content.Context
import android.net.Uri
import com.gmg.avid.storage.tech.callback.MTFileErrorResultCallBack
import com.gmg.avid.storage.tech.callback.MTFileSuccessResultCallback
import com.gmg.avid.storage.tech.callback.MTFileSuccessHandler
import com.gmg.avid.storage.tech.callback.MTFileErrorHandler
import com.gmg.avid.storage.tech.filepath_logic.MTFilePathHandler.getInputStreamFromPath
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.getMediaFileInputStreamTypeByScope
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getInputStreamFromUri
import java.io.InputStream

/**
 * The class base functionality
 */
open class MTBaseStorage {

    var errorCallback: MTFileErrorHandler? = null
    var successCallback: MTFileSuccessHandler? = null
    var context: Context? = null
    var sourceInputStm: InputStream? = null

    //To handle to constant of file
    companion object {
        const val CONTEXT_IS_MISSING = "Context is missing"
        const val FILE_PATH_IS_EMPTY = "File path is empty"
        const val FILE_NOT_FOUND = "File not Found"
        const val SOURCE_IS_MISSING = "Please provide the source address"
        const val SOME_THING_WENT_WRONG = "Some thing went wrong"
    }

    /**
     * The method called to initialize error call back
     *
     * @param errorCallback     The file error result call back
     */
    fun initializeErrorCallBack(errorCallback: MTFileErrorResultCallBack?) {
        errorCallback?.let {
            this.errorCallback = MTFileErrorHandler(it)
        }
    }

    /**
     * The method called to initialize success call back
     *
     * @param successCallback   The file action success call back
     */
    fun initializeSuccessCallBack(successCallback: MTFileSuccessResultCallback?) {
        successCallback?.let {
            this.successCallback = MTFileSuccessHandler(it)
        }
    }

    /**
     * To set source input stream
     *
     * @param inputStream   The input stream
     */
    fun setSourceInputStream(inputStream: InputStream?) {
        inputStream?.let {
            sourceInputStm = it
        }
    }

    /**
     * To set source file path
     *
     * @param filePath      The source file path
     */
    fun setSourceFilePath(filePath: String?) {
        filePath?.let {
            val inputStream = MTStorageAppModule.getAppTechContext()?.getInputStreamFromPath(it)
            setSourceInputStream(inputStream)
        }
    }

    /**
     * To set source uri
     *
     * @param uri    The uri
     */
    fun setSourceUri(uri: Uri?) {
        uri?.let {
            val inputStream = MTStorageAppModule.getAppTechContext()?.getInputStreamFromUri(it)
            setSourceInputStream(inputStream)
        }
    }

    /**
     * To set source media id
     *
     * @param mediaId   The media id
     */
    fun setSourceMediaId(mediaId: Long?) {
        mediaId?.let {
            val inputStream =
                MTStorageAppModule.getAppTechContext()?.getMediaFileInputStreamTypeByScope(mediaId)
            setSourceInputStream(inputStream)
        }
    }
}
