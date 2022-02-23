package com.gmg.avid.storage

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.annotation.RequiresApi
import com.gmg.avid.storage.databinding.ActivityUriBinding
import com.gmg.avid.storage.tech.callback.MTFileErrorResultCallBack
import com.gmg.avid.storage.tech.common.MTFileData
import com.gmg.avid.storage.tech.filepath_logic.MTFile
import com.gmg.avid.storage.tech.media_id_logic.MTMediaId
import com.gmg.avid.storage.tech.storage_logic.MTStorageDirectoryHandler.isOsHigherThenQ
import com.gmg.avid.storage.tech.storage_logic.MTStorageDirectoryHandler.isOsHigherThenR
import com.gmg.avid.storage.tech.uri_logic.MTUri
import java.lang.Exception

class MediaIdActivity : AppCompatActivity() {

    private val activityBinding: ActivityUriBinding by lazy {
        ActivityUriBinding.inflate(layoutInflater)
    }
    private lateinit var filePickerResult: ActivityResultLauncher<Intent>
    private lateinit var fileData: MTFileData
    private lateinit var uri: Uri
    private var actionRequiredFile: MTMediaId? = null
    private lateinit var deletePickerResult: ActivityResultLauncher<IntentSenderRequest>


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityBinding.root)
        setListener()
        uploadData()
    }

    private fun setListener() {
        activityBinding.btChooseFile.setOnClickListener {
            val filePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            filePickerIntent.type = "*/*"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                filePickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            }
            filePickerResult.launch(filePickerIntent)
        }

        activityBinding.btCreateFile.setOnClickListener {
            if (this::uri.isInitialized) {
                val directory = when {
                    isOsHigherThenR() -> Environment.DIRECTORY_DOCUMENTS
                    isOsHigherThenQ() -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
                    else -> Environment.getExternalStorageDirectory().absolutePath
                }
                val path = directory + "/" + getString(R.string.app_name) + "/" + System.currentTimeMillis().toString() + MTUri(uri).getDisplayName()
                val file = MTUri(uri)
                val fileListData = file.createNewFile(path)
                fileListData?.let {
                    fileData = fileListData
                }
            }
        }

        activityBinding.btUpdateFile.setOnClickListener {
            if (this::uri.isInitialized && this::fileData.isInitialized) {
                val mediaId = fileData.mediaId
                mediaId?.let {
                    val file = MTMediaId(mediaId)
                    file.setSourceUri(this.uri)
                    val fileListData = file.updateFile()
                    fileListData?.let {
                        fileData = fileListData
                    }
                }
            }
        }

        activityBinding.btCheckExist.setOnClickListener {
            if (this::fileData.isInitialized) {
                val mediaId = fileData.mediaId
                mediaId?.let {
                    val file = MTMediaId(mediaId)
                    if (file.isFileExist()) {
                        showToast("File exist")
                    } else {
                        showToast("File not exist")
                    }
                }
            }
        }

        activityBinding.btDeleteFile.setOnClickListener {
            if (this::fileData.isInitialized) {
                val mediaId = fileData.mediaId
                mediaId?.let {
                    val file = MTMediaId(5937)
                    file.initializeErrorCallBack(object : MTFileErrorResultCallBack {
                        override fun onThrowingRecoverableSecurityException(intentSender: IntentSender) {
                            actionRequiredFile = file
                            val intentSenderRequest: IntentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                            deletePickerResult.launch(intentSenderRequest)
                        }

                        override fun onThrowingException(exception: Exception) {
                            logError(exception.message.toString())
                        }
                    })
                    file.delete()
                }
            }
        }

        activityBinding.btFileDuration.setOnClickListener {
            if (this::fileData.isInitialized) {
                val mediaId = fileData.mediaId
                mediaId?.let {
                    val file = MTMediaId(mediaId)
                    val duration = file.getDuration()
                    showToast(duration.toString())
                }
            }
        }

        activityBinding.btFileLength.setOnClickListener {
            if (this::fileData.isInitialized) {
                val mediaId = fileData.mediaId
                mediaId?.let {
                    val file = MTMediaId(mediaId)
                    val duration = file.getLength()
                    showToast(duration.toString())
                }
            }
        }

        activityBinding.btMimeType.setOnClickListener {
            if (this::fileData.isInitialized) {
                val mediaId = fileData.mediaId
                mediaId?.let {
                    val file = MTMediaId(mediaId)
                    val duration = file.getMimeType()
                    showToast(duration.toString())
                }
            }
        }

        activityBinding.btFileDetails.setOnClickListener {
            if (this::fileData.isInitialized) {
                val mediaId = fileData.mediaId
                mediaId?.let {
                    val file = MTMediaId(mediaId)
                    val duration = file.getMediaIdDetails()
                    showToast(duration.toString())
                }
            }
        }
    }

    private fun uploadData() {
        filePickerResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data?.data?.let { uri ->
                        this.uri = uri
                        fileData = MTUri(uri).getUriDetails()!!
                    }
                }
            }

        deletePickerResult = registerForActivityResult(StartIntentSenderForResult(), ActivityResultCallback { it ->
                if (it.resultCode == Activity.RESULT_OK) {
                    actionRequiredFile?.let { file ->
                        file.delete()
                    }
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun logError(message: String) {
        Log.e(this.javaClass.simpleName, message)
    }
}