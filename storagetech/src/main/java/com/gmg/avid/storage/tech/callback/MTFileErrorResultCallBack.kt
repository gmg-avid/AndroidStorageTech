package com.gmg.avid.storage.tech.callback

import android.content.IntentSender
import java.lang.Exception

/**
 * The interface to listen for file error result call back
 */
interface MTFileErrorResultCallBack {

    /**
     * This method called to throw the security exception for android R
     */
    fun onThrowingRecoverableSecurityException(intentSender: IntentSender)

    /**
     * This method called to throw exceptions
     */
    fun onThrowingException(exception: Exception)
}