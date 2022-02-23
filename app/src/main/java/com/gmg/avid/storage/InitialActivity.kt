package com.gmg.avid.storage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gmg.avid.storage.databinding.ActivityInitialBinding
import com.gmg.avid.storage.tech.common.MTStorageAppModule

class InitialActivity : AppCompatActivity() {

    private val activityBinding: ActivityInitialBinding by lazy {
        ActivityInitialBinding.inflate(layoutInflater)
    }
    private var isFunctionCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MTStorageAppModule.initializeApp(this.applicationContext)
        setContentView(activityBinding.root)
        setListener()
        permissionCheck()
    }

    private fun setListener() {
        activityBinding.btFilePathLogic.setOnClickListener {
            if (checkIfAlreadyHavePermission()) {
                val intent = Intent(this, FilePathActivity::class.java)
                startActivity(intent)
            } else {
                isFunctionCalled = true
                permissionCheck()
            }
        }

        activityBinding.btUriLogic.setOnClickListener {
            if (checkIfAlreadyHavePermission()) {
                val intent = Intent(this, UriPathActivity::class.java)
                startActivity(intent)
            } else {
                isFunctionCalled = true
                permissionCheck()
            }
        }

        activityBinding.btMediaMultiLogic.setOnClickListener {
            if (checkIfAlreadyHavePermission()) {
                val intent = Intent(this, MultiFileOperationActivity::class.java)
                startActivity(intent)
            } else {
                isFunctionCalled = true
                permissionCheck()
            }
        }

        activityBinding.btMediaIdLogic.setOnClickListener {
            if (checkIfAlreadyHavePermission()) {
                val intent = Intent(this, MediaIdActivity::class.java)
                startActivity(intent)
            } else {
                isFunctionCalled = true
                permissionCheck()
            }
        }
    }

    private fun permissionCheck() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyHavePermission()) {
                requestForSpecificPermission()
            }
        }
    }

    private fun checkIfAlreadyHavePermission(): Boolean {
        val writeResult =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readResult =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return writeResult == PackageManager.PERMISSION_GRANTED && readResult == PackageManager.PERMISSION_GRANTED
    }

    private fun requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 101)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            101 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isFunctionCalled) {
                    isFunctionCalled = false
                    showToast("permission granted, pls try again operation")
                } else {
                    showToast("permission granted")
                }
            } else {
                if (isFunctionCalled) {
                    isFunctionCalled = false
                    showToast("permission required this operation")
                } else {
                    showToast("permission required")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


}