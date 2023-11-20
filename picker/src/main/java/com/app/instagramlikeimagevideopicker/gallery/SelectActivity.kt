package com.app.instagramlikeimagevideopicker.gallery

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.app.instagramlikeimagevideopicker.InstagramPicker
import com.app.instagramlikeimagevideopicker.MainFragment
import com.app.instagramlikeimagevideopicker.MediaTypeEnum
import com.app.instagramlikeimagevideopicker.R
import com.app.instagramlikeimagevideopicker.capture.CaptureFragment
import com.app.instagramlikeimagevideopicker.classes.BackgroundActivity
import com.app.instagramlikeimagevideopicker.classes.InstaPickerSharedPreference
import com.app.instagramlikeimagevideopicker.classes.Statics

class SelectActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQ = 236
    private val STORAGE_PERMISSION_REQ = 326

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        BackgroundActivity.instance?.setActivity(this)
        setOnBackPress()
        registerReceiver(br, IntentFilter(Statics.INTENT_FILTER_ACTION_NAME))
        val bnv = findViewById<BottomNavigationView>(R.id.select_bnv)
        when (InstagramPicker.getMediaTypeEnum) {
            MediaTypeEnum.IMAGE_CAPTURE, MediaTypeEnum.VIDEO_CAPTURE, MediaTypeEnum.IMAGE_VIDEO_CAPTURE -> {
                bnv.visibility = View.GONE
                requestCamera()
            }
            MediaTypeEnum.IMAGE_VIDEO_GALLERY, MediaTypeEnum.IMAGE_GALLERY, MediaTypeEnum.VIDEO_GALLERY -> {
                bnv.visibility = View.GONE
                requestStorage()
            }
            MediaTypeEnum.IMAGE_VIDEO_GALLERY_AND_CAPTURE, MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE -> {
                bnv.visibility = View.VISIBLE
                bnv.setOnItemSelectedListener { mi: MenuItem ->
                    val itemId = mi.itemId
                    if (itemId == R.id.bnv_gallery) {
                        requestStorage()
                    } else if (itemId == R.id.bnv_camera) {
                        requestCamera()
                    }
                    true
                }
                bnv.setOnItemReselectedListener { mi: MenuItem? -> }
                requestStorage()
                bnv.setOnItemReselectedListener { mi: MenuItem -> if (mi.itemId == R.id.bnv_camera && checkPermission() != PackageManager.PERMISSION_GRANTED) requestCamera() else if (mi.itemId == R.id.bnv_gallery && checkStoragePermission() != PackageManager.PERMISSION_GRANTED) requestStorage() }
            }
            else -> {}
        }

    }

    private fun openCamera() {
        when (InstagramPicker.getMediaTypeEnum) {
            MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, MediaTypeEnum.IMAGE_CAPTURE -> {
                supportFragmentManager.beginTransaction().replace(R.id.select_container, CameraFragment()).commit()
            }
            MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE, MediaTypeEnum.VIDEO_CAPTURE -> {
                supportFragmentManager.beginTransaction().replace(R.id.select_container, CaptureFragment()).commit()
            }
            MediaTypeEnum.IMAGE_VIDEO_CAPTURE, MediaTypeEnum.IMAGE_VIDEO_GALLERY_AND_CAPTURE -> {
                supportFragmentManager.beginTransaction().replace(R.id.select_container, MainFragment()).commit()
            }
            else -> {
                supportFragmentManager.beginTransaction().replace(R.id.select_container, MainFragment()).commit()
            }
        }
    }

    private fun openGallery() {
        supportFragmentManager.beginTransaction().replace(R.id.select_container, GalleryFragment()).commit()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(): Int {
        return ContextCompat.checkSelfPermission(this@SelectActivity,
                Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(this@SelectActivity,
                Manifest.permission.RECORD_AUDIO)
      //  return ActivityCompat.checkSelfPermission(this@SelectActivity, Manifest.permission.CAMERA)
    }

    private fun checkStoragePermission(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(this@SelectActivity,
                Manifest.permission.READ_MEDIA_IMAGES) + ContextCompat.checkSelfPermission(this@SelectActivity,
                Manifest.permission.READ_MEDIA_VIDEO) + ContextCompat.checkSelfPermission(this@SelectActivity,
                Manifest.permission.RECORD_AUDIO)
        } else {
            ActivityCompat.checkSelfPermission(this@SelectActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(this@SelectActivity,
                Manifest.permission.RECORD_AUDIO) /*+ ContextCompat.checkSelfPermission(
                this@SelectActivity,
            )*/
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@SelectActivity, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQ)
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this@SelectActivity,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.RECORD_AUDIO),
                STORAGE_PERMISSION_REQ)

        } else {
            ActivityCompat.requestPermissions(this@SelectActivity, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO), STORAGE_PERMISSION_REQ)
        }
    }

    private fun showExplanation() {
        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@SelectActivity)
        builder.setTitle(getString(R.string.camera_permission_title))
        builder.setMessage(getString(R.string.camera_permission_message))
        builder.setPositiveButton(
            getString(R.string.camera_permission_positive)) { dialog: DialogInterface?, which: Int -> requestPermission() }
        builder.setNegativeButton(
            getString(R.string.camera_permission_negative)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showStorageExplanation() {
        val alertDialog: AlertDialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@SelectActivity)
        builder.setTitle(getString(R.string.storage_permission_title))
        builder.setMessage(getString(R.string.storage_permission_message))
        builder.setPositiveButton(
            getString(R.string.storage_permission_positive)) { dialog: DialogInterface?, which: Int -> requestStoragePermission() }
        builder.setNegativeButton(
            getString(R.string.storage_permission_negative)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun requestCamera() {
        if (checkPermission() != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@SelectActivity, Manifest.permission.CAMERA) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this@SelectActivity, Manifest.permission.RECORD_AUDIO)) {
                showExplanation()
            } else if (!InstaPickerSharedPreference.getInstance(this@SelectActivity).cameraPermission) {
                requestPermission()
                InstaPickerSharedPreference.getInstance(this@SelectActivity).setCameraPermission()
            } else {
                showToast(getString(R.string.camera_permission_deny))
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        } else {
            openCamera()
        }
    }

    private fun requestStorage() {
        if (checkStoragePermission() != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@SelectActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this@SelectActivity,
                    Manifest.permission.READ_MEDIA_IMAGES) || ActivityCompat.shouldShowRequestPermissionRationale(this@SelectActivity,
                    Manifest.permission.READ_MEDIA_VIDEO) || /*ActivityCompat.shouldShowRequestPermissionRationale(
                    this@SelectActivity,
                ) ||*/ ActivityCompat.shouldShowRequestPermissionRationale(this@SelectActivity, Manifest.permission.RECORD_AUDIO)) {
                showStorageExplanation()
            } else if (!InstaPickerSharedPreference.getInstance(this@SelectActivity).storagePermission) {
                requestStoragePermission()
                InstaPickerSharedPreference.getInstance(this@SelectActivity).setStoragePermission()
            } else {
                showToast(getString(R.string.storage_permission_deny))
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        } else {
            openGallery()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_REQ) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
        } else if (requestCode == STORAGE_PERMISSION_REQ) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                onBackPressed()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setOnBackPress() {
        Log.e("============","setOnBackPress")
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                unregisterReceiver(br)
            }
        })
    }

    private var br: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (InstagramPicker.multiSelect) {
                if (InstagramPicker.mListener != null) {
                    InstagramPicker.mListener?.selectedPics(InstagramPicker.addresses)
                }
            } else {
                if (InstagramPicker.sListener != null) {
                    InstagramPicker.sListener?.selectedPic(InstagramPicker.addresses[0])
                }
            }
            unregisterReceiver(this)
        }
    }
}