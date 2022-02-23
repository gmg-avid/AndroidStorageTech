package com.gmg.avid.storage.tech.multi_file_logic

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.gmg.avid.storage.tech.common.MTBaseStorage
import com.gmg.avid.storage.tech.common.MTStorageAppModule
import com.gmg.avid.storage.tech.multi_file_logic.MTMultiFileHandler.deleteMultiMediaFile
import com.gmg.avid.storage.tech.multi_file_logic.MTMultiFileHandler.deleteMultiMediaIdFile
import com.gmg.avid.storage.tech.multi_file_logic.MTMultiFileHandler.deleteMultiUriFile
import com.gmg.avid.storage.tech.multi_file_logic.MTMultiFileHandler.modifyMultiMediaFile
import com.gmg.avid.storage.tech.multi_file_logic.MTMultiFileHandler.modifyMultiMediaIdFile
import com.gmg.avid.storage.tech.multi_file_logic.MTMultiFileHandler.modifyMultiUriFile

/**
 * This class handle to multiple media file
 */
class MTMultiFile : MTBaseStorage() {

    /**
     * The method called to delete multi media files
     *
     * @param fileList  The file list required delete
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun deleteMultiMediaFile(fileList : List<String>) {
        context = MTStorageAppModule.getAppTechContext()
        when {
            context == null -> {  //context missing throw error
                errorCallback?.throwException(CONTEXT_IS_MISSING)
            }
            fileList.isEmpty() -> {
                errorCallback?.throwException(FILE_NOT_FOUND)
            }
            else -> {
                context?.deleteMultiMediaFile(fileList)?.let {
                    errorCallback?.throwIntentSenderException(it)
                }
            }
        }
    }

    /**
     * The method called to modify multi media files
     *
     * @param fileList    The file list required deleter
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun modifyMultiMediaFile(fileList : List<String>) {
        context = MTStorageAppModule.getAppTechContext()
        when {
            context == null -> {  //context missing throw error
                errorCallback?.throwException(CONTEXT_IS_MISSING)
            }
            fileList.isEmpty() -> {
                errorCallback?.throwException(FILE_NOT_FOUND)
            }
            else -> {
                context?.modifyMultiMediaFile(fileList)?.let {
                    errorCallback?.throwIntentSenderException(it)
                }
            }
        }
    }

    /**
     * The method called to delete multi media id
     *
     * @param mediaIdLIst   The media id list
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun deleteMultiMediaId(mediaIdLIst : List<Long>) {
        context = MTStorageAppModule.getAppTechContext()
        when {
            context == null -> {  //context missing throw error
                errorCallback?.throwException(CONTEXT_IS_MISSING)
            }
            mediaIdLIst.isEmpty() -> {
                errorCallback?.throwException(FILE_NOT_FOUND)
            }
            else -> {
                context?.deleteMultiMediaIdFile(mediaIdLIst)?.let {
                    errorCallback?.throwIntentSenderException(it)
                }
            }
        }
    }

    /**
     * The method called to modify multi media id
     *
     * @param mediaIdLIst       The media id list
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun modifyMultiMediaId(mediaIdLIst : List<Long>) {
        context = MTStorageAppModule.getAppTechContext()
        when {
            context == null -> {  //context missing throw error
                errorCallback?.throwException(CONTEXT_IS_MISSING)
            }
            mediaIdLIst.isEmpty() -> {
                errorCallback?.throwException(FILE_NOT_FOUND)
            }
            else -> {
                context?.modifyMultiMediaIdFile(mediaIdLIst)?.let {
                    errorCallback?.throwIntentSenderException(it)
                }
            }
        }
    }

    /**
     * The method called to delete multi media uri
     *
     * @param uriList       The uri list
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun deleteMultiMediaUri(uriList : List<Uri>) {
        context = MTStorageAppModule.getAppTechContext()
        when {
            context == null -> {
                errorCallback?.throwException(CONTEXT_IS_MISSING)
            }
            uriList.isEmpty() -> {
                errorCallback?.throwException(FILE_NOT_FOUND)
            } else -> {
               context?.deleteMultiUriFile(uriList)?.let {
                   errorCallback?.throwIntentSenderException(it)
               }
            }
        }
    }

    /**
     * The method called to modify multi media uri
     *
     * @param uriList   The uri list
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun modifyMultiMediaUri(uriList : List<Uri>) {
        context = MTStorageAppModule.getAppTechContext()
        when {
            context == null -> {
                errorCallback?.throwException(CONTEXT_IS_MISSING)
            }
            uriList.isEmpty() -> {
                errorCallback?.throwException(FILE_NOT_FOUND)
            } else -> {
            context?.modifyMultiUriFile(uriList)?.let {
                errorCallback?.throwIntentSenderException(it)
            }
        }
        }
    }
}