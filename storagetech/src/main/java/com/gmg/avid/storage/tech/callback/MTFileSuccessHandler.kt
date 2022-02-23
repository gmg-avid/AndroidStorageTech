package com.gmg.avid.storage.tech.callback

import com.gmg.avid.storage.tech.common.MTFileData

/**
 * This class handle the file action success operation
 *
 * @param fileSuccessResultCallback     The file success result call back
 */
class MTFileSuccessHandler(val fileSuccessResultCallback: MTFileSuccessResultCallback) {

    /**
     * This method called perform when the operation completed successfully
     *
     * @param fileDetails       The file details
     * @param message           The message
     */
    fun onSuccessFileAction(fileDetails: MTFileData?, message : String?) {
        fileSuccessResultCallback.onActionSuccess(fileDetails, message)
    }

}