package com.gmg.avid.storage.tech.callback

import android.app.RecoverableSecurityException
import android.content.IntentSender
import com.gmg.avid.storage.tech.storage_logic.MTStorageDirectoryHandler.isOsHigherThenQ
import java.lang.Exception

/**
 * This class called to throw file error message
 *
 * @param fileErrorResultCallback   The file error callback
 */
class MTFileErrorHandler(val fileErrorResultCallback: MTFileErrorResultCallBack) {

    /**
     * To Throw security Exception when non-owned files are delete or update by our app
     *
     * @param securityException     The security Exceptions
     */
    fun throwSecurityException(securityException: SecurityException?) {
        if (securityException != null)
            if (isOsHigherThenQ()) {
                //This exception introduce from android q unless this exception will not throw
                //security exception thrown when try to update/delete file which is not owned by our app
                //Using security exception ask to user permission to do certain operation
                val recoverableSecurityException = securityException as? RecoverableSecurityException ?: throw securityException
                fileErrorResultCallback.onThrowingRecoverableSecurityException(recoverableSecurityException.userAction.actionIntent.intentSender)
            } else {
                //if security exception throw below android q, consider as normal exception
                fileErrorResultCallback.onThrowingException(securityException)
            }
    }


    fun throwIntentSenderException(intentSender: IntentSender) {
        fileErrorResultCallback.onThrowingRecoverableSecurityException(intentSender)
    }

    /**
     * To Throw exception when error on the operation
     *
     * @param message           The error message
     */
    fun throwException(message: String?) {
        if (message != null) {
            //Throw error message as the exception
            fileErrorResultCallback.onThrowingException(Exception(message))
        }
    }

    /**
     * To throw exception when error exception
     *
     * @param exception         The exceptions
     */
    fun throwException(exception: Exception?) {
        if (exception != null) {
            fileErrorResultCallback.onThrowingException(exception)
        }
    }
}