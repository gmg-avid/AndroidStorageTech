package com.gmg.avid.storage.tech.storage_logic

import android.content.Context
import android.os.Build

object MTStorageDirectoryHandler {

    /**
     * This method used to check whether scope storage required to access given file path
     *
     * @param filePath      The file path
     */
    fun Context.isScopeStorageRequiredToAccessFile(filePath : String): Boolean {
        //From Android version Q scope storage is introduced, but on Android version Q have relaxation to use legacy flag instead of scope storage
        //Since Android Q has some difficulties related multiple delete, update and their functionality for android we recommended to use legacy true in our app
        //on This we written at scope required only after android 11 and file no need under app specific directory
        return isOsHigherThenR() && !isFileUnderAppSpecificDirectory(filePath)
    }

    /**
     * This method used to check whether file path created/will create under app specific directory
     *
     * @param filePath       The file path
     */
    private fun Context.isFileUnderAppSpecificDirectory(filePath : String): Boolean {
        return filePath.contains(getFileMediaDirsPath()) || filePath.contains(getFileDirsPath()) || filePath.contains(getExternalFileDirsPath()) || filePath.contains(getExternalCacheFileDirsPath()) || filePath.contains(getCacheFileDirsPath()) || filePath.contains(getDataDirsPath()) || filePath.contains(getObbDirsPath())
    }

    /**
     * This method called to get file media dir path
     */
    private fun Context.getFileMediaDirsPath() : String {
        //get the external media dir which correspond to our app
        //since the method is deprecated from android 11, to make us for migrate scope storage
        //This is only path contains both functionality like it is app specific directory even file in this directory are visible by gallery and other app
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            externalMediaDirs[0].absolutePath
        } else {
            return ""
        }
    }

    /**
     * This method called to get file dir path
     */
    private fun Context.getFileDirsPath() : String {
        return filesDir.absolutePath
    }

    /**
     * This method called external file dir path
     */
    fun Context.getExternalFileDirsPath() : String {
        getExternalFilesDir(null)?.absolutePath?.let { path ->
            return path
        }
        return ""
    }

    /**
     * This method called get cache file dir path
     */
    private fun Context.getCacheFileDirsPath() : String {
        return cacheDir.absolutePath
    }

    /**
     * This method called get External cache file dir path
     */
    private fun Context.getExternalCacheFileDirsPath() : String {
        externalCacheDir?.absolutePath?.let { path ->
            return path
        }
        return ""
    }

    /**
     * This method called to get data dir path
     */
    private fun Context.getDataDirsPath() : String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dataDir.absolutePath
        } else {
            ""
        }
    }

    /**
     * This method called to get obb dir path
     */
    private fun Context.getObbDirsPath() : String {
        return obbDir.absolutePath
    }

    /**
     * This method called to check whether the device OS is Q or greater than it
     */
    fun isOsHigherThenQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    /**
     * This method called to check whether the device OS is R or greater than it
     */
    fun isOsHigherThenR() : Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }
}