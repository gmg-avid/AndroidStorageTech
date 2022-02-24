package com.gmg.avid.storage

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.gmg.avid.storage.databinding.ActivityMediaFileBinding
import com.gmg.avid.storage.tech.callback.MTFileErrorResultCallBack
import com.gmg.avid.storage.tech.filepath_logic.MTFile
import com.gmg.avid.storage.tech.multi_file_logic.MTMultiFile
import com.gmg.avid.storage.tech.uri_logic.MTUri


class MultiFileOperationActivity : AppCompatActivity() {

    private val activityBinding: ActivityMediaFileBinding by lazy {
        ActivityMediaFileBinding.inflate(layoutInflater)
    }
    private lateinit var filePickerResult : ActivityResultLauncher<Intent>
    private lateinit var errorPickerResult : ActivityResultLauncher<Intent>
    private lateinit var deletePickerResult : ActivityResultLauncher<IntentSenderRequest>
    private lateinit var modifyPickerResult : ActivityResultLauncher<IntentSenderRequest>
    private val uriList : MutableList<Uri> = mutableListOf()
    private var uri : Uri? = null
    private val fileList : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityBinding.root)
        setListener()
    }

    private fun setListener() {
        activityBinding.btChooseFile.setOnClickListener {
            val filePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            filePickerIntent.type = "*/*"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                filePickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            filePickerResult.launch(filePickerIntent)
        }

        activityBinding.btChooseFileN.setOnClickListener {
            val filePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            filePickerIntent.type = "*/*"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                filePickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            }
            errorPickerResult.launch(filePickerIntent)
        }

        filePickerResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri ->
                    val filePath = MTUri.getPathFromProviderUri(uri)
                    if (filePath != null) {
                        fileList.add(filePath)
                    }
                }
                it.data?.clipData?.let { clip ->
                    for (i in 1..clip.itemCount) {
                        val uri = clip.getItemAt(i-1).uri
                        val filePath = MTUri.getPathFromProviderUri(uri)
                        if (filePath != null) {
                            fileList.add(filePath)
                        }
                    }
                }
            }
        }

        errorPickerResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let {
                    uri = it
                }
            }
        }

        deletePickerResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult(), ActivityResultCallback { it ->
            if (it.resultCode == RESULT_OK) {
               deleteMultiMediaFile()
            }
        })

        modifyPickerResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult(), ActivityResultCallback { it ->
            if (it.resultCode == RESULT_OK) {
                modifyMultiMediaFile()
            }
        })

        activityBinding.btDeleteFile.setOnClickListener {
            deleteMultiMediaFile()
        }

        activityBinding.btModifyFile.setOnClickListener {
            modifyMultiMediaFile()
        }
    }

    private fun modifyMultiMediaFile() {
        if (fileList.isNotEmpty()) {
            if (fileList.size == 1) {
                if (uri != null) {
                    val file = MTFile(fileList[0])
                    file.initializeErrorCallBack(object : MTFileErrorResultCallBack {
                        override fun onThrowingRecoverableSecurityException(intentSender: IntentSender) {
                            val intentSenderRequest: IntentSenderRequest =
                                IntentSenderRequest.Builder(intentSender).build()
                            modifyPickerResult.launch(intentSenderRequest)
                        }

                        override fun onThrowingException(exception: Exception) {

                        }
                    })
                    file.setSourceUri(uri)
                    file.updateFile()
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val file = MTMultiFile()
                file.initializeErrorCallBack(object : MTFileErrorResultCallBack {
                    override fun onThrowingRecoverableSecurityException(intentSender: IntentSender) {
                        val intentSenderRequest: IntentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                        modifyPickerResult.launch(intentSenderRequest)
                    }

                    override fun onThrowingException(exception: Exception) {

                    }
                })
                file.modifyMultiMediaFile(fileList)
            } else {
                showToast("This is available in Android 11")
            }
        }
    }

    private fun deleteMultiMediaFile() {
        if (fileList.isNotEmpty()) {
            if (fileList.size == 1) {
                val file = MTFile(fileList[0])
                file.initializeErrorCallBack(object : MTFileErrorResultCallBack {
                    override fun onThrowingRecoverableSecurityException(intentSender: IntentSender) {
                        val intentSenderRequest: IntentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                        deletePickerResult.launch(intentSenderRequest)
                    }

                    override fun onThrowingException(exception: Exception) {

                    }
                })
                file.deleteFile()
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val file = MTMultiFile()
                file.initializeErrorCallBack(object : MTFileErrorResultCallBack {
                    override fun onThrowingRecoverableSecurityException(intentSender: IntentSender) {
                        val intentSenderRequest: IntentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                        deletePickerResult.launch(intentSenderRequest)
                    }

                    override fun onThrowingException(exception: Exception) {
                        TODO("Not yet implemented")
                    }
                })
                file.deleteMultiMediaFile(fileList)
            } else {
                showToast("This is available in Android 11")
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}