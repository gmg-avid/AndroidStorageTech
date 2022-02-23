package com.gmg.avid.storage.tech.callback

import com.gmg.avid.storage.tech.common.MTFileData


/**
 * The interface to listen for file success result call back
 */
interface MTFileSuccessResultCallback {

    /**
     * This method called to denote on file action performed successfully
     */
    fun onActionSuccess(fileDetails: MTFileData? = null, message : String? = null)
}