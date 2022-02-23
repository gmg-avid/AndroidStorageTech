package com.gmg.avid.storage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.gmg.avid.storage.databinding.ActivityUriBinding
import com.gmg.avid.storage.tech.common.MTFileData
import com.gmg.avid.storage.tech.storage_logic.MTStorageDirectoryHandler.isOsHigherThenQ
import com.gmg.avid.storage.tech.storage_logic.MTStorageDirectoryHandler.isOsHigherThenR
import com.gmg.avid.storage.tech.uri_logic.MTUri

class UriPathActivity : AppCompatActivity() {

    private val activityBinding: ActivityUriBinding by lazy {
        ActivityUriBinding.inflate(layoutInflater)
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
                val uri = MTUri(uri)
//                /**
//                 * demo
//                 */
//                    uri.delete()
//                    uri.getUriDetails()
//                    uri.getDisplayName()
//                    uri.getRelativePath()
//                    uri.getDuration()
//                    uri.getLength()
//                    uri.getMimetype()
//                    uri.getOutputStream()
//                    uri.getInputStream()
//                    uri.createNewFile(path)
//                    uri.delete()
//
//
//
//                    //update
//                uri.updateFile()
//                uri.setSourceMediaId()
//
//                //
                val fileListData = uri.createNewFile(path)
                fileListData?.let {
                    fileData = fileListData
                }
            }
        }

        activityBinding.btUpdateFile.setOnClickListener {
            if (this::uri.isInitialized && this::fileData.isInitialized) {
                val uri = fileData.uri
                uri?.let {
                    val file = MTUri(uri)
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
                val uri = fileData.uri
                uri?.let {
                    showToast("functionality not available")
                }
            }
        }

        activityBinding.btDeleteFile.setOnClickListener {
            if (this::fileData.isInitialized) {
                val uri = fileData.uri
                uri?.let {
                    val file = MTUri(uri)
                    file.delete()
                }
            }
        }

        activityBinding.btFileDuration.setOnClickListener {
            if (this::fileData.isInitialized) {
                val uri = fileData.uri
                uri?.let {
                    val file = MTUri(uri)
                    val duration = file.getDuration()
                    showToast(duration.toString())
                }
            }
        }

        activityBinding.btFileLength.setOnClickListener {
            if (this::fileData.isInitialized) {
                val uri = fileData.uri
                uri?.let {
                    val file = MTUri(uri)
                    val duration = file.getLength()
                    showToast(duration.toString())
                }
            }
        }

        activityBinding.btMimeType.setOnClickListener {
            if (this::fileData.isInitialized) {
                val uri = fileData.uri
                uri?.let {
                    val file = MTUri(uri)
                    val duration = file.getMimetype()
                    showToast(duration.toString())
                }
            }
        }

        activityBinding.btFileDetails.setOnClickListener {
            if (this::fileData.isInitialized) {
                val uri = fileData.uri
                uri?.let {
                    val file = MTUri(uri)
                    val duration = file.getUriDetails()
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