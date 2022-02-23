package com.gmg.avid.storage.tech.multi_file_logic

import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.gmg.avid.storage.tech.filepath_logic.MTFile
import com.gmg.avid.storage.tech.media_id_logic.MTMediaId
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.deleteMultipleItem
import com.gmg.avid.storage.tech.storage_logic.MTScopeStorageHandler.modifyMultipleItem
import com.gmg.avid.storage.tech.uri_logic.MTUri
import com.gmg.avid.storage.tech.uri_logic.MTUriOperationHandler.getFileDetailsFromUri

object MTMultiFileHandler {

    @RequiresApi(Build.VERSION_CODES.R)
    fun Context.deleteMultiMediaFile(fileList : List<String>): IntentSender? {
        val uriList : MutableList<Uri> = mutableListOf()
        fileList.forEach {
            val uri = MTFile(it).getUri()
            if (uri != null) {
                uriList.add(uri)
            }
        }
        return if (uriList.isEmpty()) {
            null
        } else {
            deleteMultipleItem(uriList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun Context.modifyMultiMediaFile(fileList : List<String>): IntentSender? {
        val uriList : MutableList<Uri> = mutableListOf()
        fileList.forEach {
            val uri = MTFile(it).getUri()
            if (uri != null) {
                uriList.add(uri)
            }
        }
        return if (uriList.isEmpty()) {
            null
        } else {
            modifyMultipleItem(uriList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun Context.deleteMultiMediaIdFile(mediaIdList : List<Long>) : IntentSender? {
        val uriList : MutableList<Uri> = mutableListOf()
        mediaIdList.forEach {
            val uri = MTMediaId(it).getUri()
            if (uri != null) {
                uriList.add(uri)
            }
        }
        return if (uriList.isEmpty()) {
            null
        } else {
            deleteMultipleItem(uriList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun Context.modifyMultiMediaIdFile(mediaIdList : List<Long>) : IntentSender? {
        val uriList : MutableList<Uri> = mutableListOf()
        mediaIdList.forEach {
            val uri = MTMediaId(it).getUri()
            if (uri != null) {
                uriList.add(uri)
            }
        }
        return if (uriList.isEmpty()) {
            null
        } else {
            modifyMultipleItem(uriList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun Context.deleteMultiUriFile(uriList : List<Uri>) : IntentSender? {
        return deleteMultipleItem(uriList)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun Context.modifyMultiUriFile(uriList : List<Uri>) : IntentSender? {
        val uriLists : MutableList<Uri> = mutableListOf()
        uriList.forEach {
            val uri = MTUri(it)
            uri.getUriDetails()?.uri?.let { ur->
                uriLists.add(ur)
            }
        }
        return modifyMultipleItem(uriList)
    }
}