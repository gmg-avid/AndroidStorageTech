package com.gmg.avid.storage.tech.common

import android.content.ContentResolver
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.webkit.MimeTypeMap
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.*

/**
 * This class handle storage common logic
 */
object MTStorageCommonOperation {
    private const val IMAGE = "image"
    private const val VIDEO = "video"
    private const val AUDIO = "audio"

    /**
     * The method called to check whether duration required for given mimetype
     *
     * @param mimeType      The mime type of file
     * @return The boolean determines the duration required to calculate this file
     */
    fun isDurationRequired(mimeType : String?): Boolean {
        return if (mimeType != null) {
            mimeType.startsWith(VIDEO) || mimeType.startsWith(AUDIO)
        } else {
            false
        }
    }

    /**
     * This method called to write file in destination with source input-stream
     *
     * @param inputStream       The input stream from source
     * @param outputStream      The output stream from destination
     * @throws Exception   - The exceptions throw in this method while error occur while writing
     */
    @Throws(Exception::class)
    fun writeFileInDestination(inputStream: InputStream, outputStream: OutputStream) {
        outputStream.let { output ->
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                output.write(buf, 0, len)
            }
        }
        inputStream.close()
        outputStream.close()
    }
}