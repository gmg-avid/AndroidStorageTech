package com.gmg.avid.storage.tech.common

import android.net.Uri

/**
 * This data class contains the file details
 *
 * @param mediaId       The file media id
 * @param filePath      The file path
 * @param relativePath  The file relative path
 * @param displayName   The file display name
 * @param mimeType      The file mime type
 * @param duration      The file duration
 * @param size          The file length
 * @param uri           The file uri
 */
data class MTFileData(val mediaId: Long? = null, val filePath: String? = null, val relativePath: String? = null, val displayName: String? = null, val mimeType : String? = null, val duration: String? = null, val size: Long? = null, val uri : Uri? = null)
