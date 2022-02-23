package com.gmg.avid.storage

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.gmg.avid.storage.databinding.ActivityFilePathBinding
import com.gmg.avid.storage.tech.callback.MTFileErrorResultCallBack
import com.gmg.avid.storage.tech.callback.MTFileSuccessResultCallback
import com.gmg.avid.storage.tech.common.MTFileData
import com.gmg.avid.storage.tech.filepath_logic.MTFile
import com.gmg.avid.storage.tech.storage_logic.MTStorageDirectoryHandler.isOsHigherThenQ
import com.gmg.avid.storage.tech.storage_logic.MTStorageDirectoryHandler.isOsHigherThenR
import com.gmg.avid.storage.tech.uri_logic.MTUri
import java.lang.Exception
import android.provider.MediaStore




class FilePathActivity : AppCompatActivity() {

    private val activityBinding: ActivityFilePathBinding by lazy {
        ActivityFilePathBinding.inflate(layoutInflater)
    }
    private lateinit var filePickerResult: ActivityResultLauncher<Intent>
    private lateinit var fileData : MTFileData
    private lateinit var uri : Uri


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
            filePickerIntent.type = "*/*" //any time *images
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
                val file = MTFile(path)
                file.setSourceUri(uri)
                val fileListData = file.createFile()
                fileListData?.let {
                    fileData = fileListData
                }
            }
        }

        activityBinding.btUpdateFile.setOnClickListener {
            if (this::uri.isInitialized && this::fileData.isInitialized) {
                val filePath = fileData.filePath
                filePath?.let {
                    val file = MTFile(it)
                    file.setSourceUri(uri)
                    val fileListData = file.updateFile()
                    fileListData?.let {
                        fileData = fileListData
                    }
                }
            }
        }

        activityBinding.btCheckExist.setOnClickListener {
            if (this::fileData.isInitialized) {
                val filePath = fileData.filePath
                filePath?.let {
                    val file = MTFile(it)
                    if (file.isFileExist()) {
                        showToast("File Exist")
                    } else {
                        showToast("File not Exist")
                    }
                }
            }
        }

        activityBinding.btDeleteFile.setOnClickListener {
            if (this::fileData.isInitialized) {
                val filePath = fileData.filePath
                filePath?.let {
                    val file = MTFile(it)
                    file.deleteFile()
                }
            }
        }

        activityBinding.btFileDuration.setOnClickListener {
            if (this::fileData.isInitialized) {
                val filePath = fileData.filePath
                filePath?.let {
                    val file = MTFile(it)
                    val duration = file.getDuration()
                    showToast(duration.toString())
                }
            }
        }

        activityBinding.btFileLength.setOnClickListener {
            if (this::fileData.isInitialized) {
                val filePath = fileData.filePath
                filePath?.let {
                    val file = MTFile(it)
                    val duration = file.getLength()
                    showToast(duration.toString())
                }
            }
        }

        activityBinding.btMimeType.setOnClickListener {
            if (this::fileData.isInitialized) {
                val filePath = fileData.filePath
                filePath?.let {
                    val file = MTFile(it)
                    val duration = file.getMimeType()
                    showToast(duration.toString())
                }
            }
        }

        activityBinding.btFileDetails.setOnClickListener {
            if (this::fileData.isInitialized) {
                val filePath = fileData.filePath
                filePath?.let {
                    val file = MTFile(it)
                    val duration = file.getFileDetails()
                    showToast(duration.toString())
                }
            }
        }
    }

    private fun uploadData() {
        filePickerResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data?.data?.let { uri ->
                       this.uri = uri
                    }
                }
            }
    }

    private fun showToast(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}