package com.app.instagramlikeimagevideopicker.gallery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropFragment
import com.yalantis.ucrop.UCropFragment.UCropResult
import com.yalantis.ucrop.UCropFragmentCallback
import com.yalantis.ucrop.model.AspectRatio
import com.yalantis.ucrop.view.CropImageView
import com.app.instagramlikeimagevideopicker.InstagramPicker
import com.app.instagramlikeimagevideopicker.R
import com.app.instagramlikeimagevideopicker.classes.BackgroundActivity
import com.app.instagramlikeimagevideopicker.classes.Statics
import com.app.instagramlikeimagevideopicker.extension.clickWithDebounce
import com.app.instagramlikeimagevideopicker.gallery.ImageUtils.getBitmapToFile
import com.app.instagramlikeimagevideopicker.gallery.ImageUtils.getResizeLandscapeImage
import com.app.instagramlikeimagevideopicker.gallery.ImageUtils.getResizeProfileImage
import java.io.File
import java.util.Objects

class MultiSelectActivity : AppCompatActivity(), UCropFragmentCallback {
    private val allImageList: ArrayList<ImageCropModel> = ArrayList()
    private val finalAddresses = ArrayList<String>()
    private var tvNext: AppCompatTextView? = null
    private var fragment: UCropFragment? = null
    private var selectedPosition = 0

    companion object {
        @JvmField
        var addresses: List<String> = arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_select)
        setOnBackPress()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(br, IntentFilter(Statics.INTENT_FILTER_ACTION_NAME),RECEIVER_EXPORTED)
        }else {
            registerReceiver(br, IntentFilter(Statics.INTENT_FILTER_ACTION_NAME))
        }
        addresses.forEach {
            val image = ImageCropModel()
            image.address = it
            image.isCropped = false
            allImageList.add(image)
        }

        tvNext = findViewById(R.id.tv_next)
        tvNext!!.clickWithDebounce { view: View? -> onNextClick() }

        openCropView(allImageList[0].address)
    }

    private fun onNextClick() {
        fragment!!.cropAndSaveImage()
    }

    private fun openCropView(imagePath: String) {
        val options = UCrop.Options()
        options.setCompressionQuality(90)
        options.withMaxResultSize(InstagramPicker.imageWidth, InstagramPicker.imageHeight)
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.color_006FD1))
        options.setToolbarTitle(getString(R.string.instagrampicker_crop_title))
        if (InstagramPicker.isDefault16And9Ratio) {
            options.setAspectRatioOptions(
                4,
                AspectRatio(null, 1f, 1f),
                AspectRatio(null, 3f, 4f),
                AspectRatio(getString(R.string.ucrop_label_original).uppercase(), CropImageView.SOURCE_IMAGE_ASPECT_RATIO, CropImageView.SOURCE_IMAGE_ASPECT_RATIO),
                AspectRatio(null, 3f, 2f),
                AspectRatio(null, 16f, 9f)
            )
        }

        val x = InstagramPicker.x
        val y = InstagramPicker.y
        val uCrop = UCrop.of(Uri.fromFile(File(imagePath)), Uri.fromFile(File(cacheDir, Statics.currentDate)))
        if (!InstagramPicker.isDefault16And9Ratio) {
            uCrop.withAspectRatio(x, y)
        }
        uCrop.withOptions(options)

        setupFragment(uCrop)
    }

    fun setupFragment(uCrop: UCrop) {
        fragment = uCrop.getFragment(uCrop.getIntent(this).extras)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, fragment!!, UCropFragment.TAG)
            .commitAllowingStateLoss()
    }

    override fun loadingProgress(showLoader: Boolean) {}

    override fun onCropFinish(result: UCropResult) {
        when (result.mResultCode) {
            RESULT_OK -> {
                val resultUri = UCrop.getOutput(result.mResultData)
                val file = File(resultUri?.path.toString())

                allImageList[selectedPosition].address = file.absolutePath
                allImageList[selectedPosition].isCropped = true

                allImageList.forEachIndexed { i: Int, model: ImageCropModel ->
                    if (!model.isCropped) {
                        selectedPosition = i
                        openCropView(model.address)
                        return@forEachIndexed
                    }
                }

                if (allImageList.filter { !it.isCropped }.size == 1) {
                    tvNext!!.text = getString(R.string.finish)
                } else if (allImageList.filter { !it.isCropped }.isEmpty()) {
                    finishClick()
                }
            }
        }
    }

    private fun finishClick() {
        if (InstagramPicker.addresses == null) {
            InstagramPicker.addresses = ArrayList()
        }
        for (i in allImageList.indices) {
            val file = File(allImageList[i].address)
            val resultUri = Uri.fromFile(file)
            val selectedPostBitmap: Bitmap? = if (InstagramPicker.x == 1f && InstagramPicker.y == 1f) {
                getResizeProfileImage(this, ImageUtils.ScalingLogic.CROP, Objects.requireNonNull(resultUri).path!!, resultUri)
            } else {
                getResizeLandscapeImage(this, ImageUtils.ScalingLogic.CROP, Objects.requireNonNull(resultUri).path!!, resultUri)
            }
            val localSelectedFile = getBitmapToFile(this, selectedPostBitmap!!)
            allImageList[i].address = localSelectedFile!!.absolutePath
            finalAddresses.add(allImageList[i].address)
        }
        InstagramPicker.addresses = finalAddresses
        sendBroadcast(Intent(Statics.INTENT_FILTER_ACTION_NAME))
        finish()
        BackgroundActivity.instance?.activity?.finish()
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
                    InstagramPicker.mListener!!.selectedPics(InstagramPicker.addresses)
                }
            } else {
                if (InstagramPicker.sListener != null) {
                    Log.e("============","addresses[0]:: ${InstagramPicker.addresses[0]}")
                    InstagramPicker.sListener!!.selectedPic(InstagramPicker.addresses[0])
                }
            }
            unregisterReceiver(this)
        }
    }
}