package com.app.instagramlikeimagevideopicker

import android.app.Activity
import android.content.Intent
import com.app.instagramlikeimagevideopicker.classes.MultiListener
import com.app.instagramlikeimagevideopicker.classes.SingleListener
import com.app.instagramlikeimagevideopicker.gallery.SelectActivity

class InstagramPicker(private val activity: Activity) {

    fun show(cropXRatio: Int, cropYRatio: Int, listener: SingleListener) {
        addresses = ArrayList()
        x = cropXRatio.toFloat()
        y = cropYRatio.toFloat()
        if (cropXRatio == 1 && cropYRatio == 1) {
            imageWidth = PROFILE_IMAGE_SIZE
            imageHeight = PROFILE_IMAGE_SIZE
        } else {
            imageWidth = LANDSCAPE_IMAGE_WIDTH
        }
        isDefault16And9Ratio = true
        multiSelect = false
        sListener = listener
        val intent = Intent(activity, SelectActivity::class.java)
        activity.startActivity(intent)
    }

    /*fun show(cropXRatio: Int, cropYRatio: Int, isDefault16And9Ratio: Boolean, listener: SingleListener) {
        addresses = ArrayList()
        x = cropXRatio.toFloat()
        y = cropYRatio.toFloat()
        if (cropXRatio == 1 && cropYRatio == 1) {
            imageWidth = PROFILE_IMAGE_SIZE
            imageHeight = PROFILE_IMAGE_SIZE
        } else {
            imageWidth = LANDSCAPE_IMAGE_WIDTH
        }
        Companion.isDefault16And9Ratio = isDefault16And9Ratio
        multiSelect = false
        sListener = listener
        val intent = Intent(activity, SelectActivity::class.java)
        activity.startActivity(intent)
    }*/

    fun show(mediaTypeEnum: MediaTypeEnum = MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, cropXRatio: Int, cropYRatio: Int, isDefault16And9Ratio: Boolean, listener: SingleListener) {
        addresses = ArrayList()
        x = cropXRatio.toFloat()
        y = cropYRatio.toFloat()
        if (cropXRatio == 1 && cropYRatio == 1) {
            imageWidth = PROFILE_IMAGE_SIZE
            imageHeight = PROFILE_IMAGE_SIZE
        } else {
            imageWidth = LANDSCAPE_IMAGE_WIDTH
        }
        Companion.isDefault16And9Ratio = isDefault16And9Ratio
        multiSelect = false
        sListener = listener
        getMediaTypeEnum = mediaTypeEnum
        val intent = Intent(activity, SelectActivity::class.java)
        activity.startActivity(intent)
//        activity.registerReceiver(br, IntentFilter(Statics.INTENT_FILTER_ACTION_NAME))
    }

    /*fun show(mediaTypeEnum: MediaTypeEnum = MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, isMultiSelect: Boolean, cropXRatio: Int = 0, cropYRatio: Int = 0, numberOfPictures: Int, listener: SingleListener) {
        var numberOfPictures = numberOfPictures
        addresses = ArrayList()
        if (numberOfPictures < 2) numberOfPictures = 2 else if (numberOfPictures > 1000) numberOfPictures = 1000
        x = cropXRatio.toFloat()
        y = cropYRatio.toFloat()
        if (cropXRatio == 1 && cropYRatio == 1) {
            imageWidth = PROFILE_IMAGE_SIZE
            imageHeight = PROFILE_IMAGE_SIZE
        } else {
            imageWidth = LANDSCAPE_IMAGE_WIDTH
        }
        isDefault16And9Ratio = true
        sListener = listener
        getMediaTypeEnum = mediaTypeEnum
        multiSelect = isMultiSelect
        Companion.numberOfPictures = if (getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE) {
            1
        } else if (getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE) {
            numberOfPictures
        } else {
            1
        }
        val intent = Intent(activity, SelectActivity::class.java)
        activity.startActivity(intent)
    }*/

    fun show(mediaTypeEnum: MediaTypeEnum = MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, cropXRatio: Int = 0, cropYRatio: Int = 0, listener: SingleListener) {
        var numberOfPictures = numberOfPictures
        addresses = ArrayList()
        if (numberOfPictures < 2) numberOfPictures = 2 else if (numberOfPictures > 1000) numberOfPictures = 1000
        x = cropXRatio.toFloat()
        y = cropYRatio.toFloat()
        if (cropXRatio == 1 && cropYRatio == 1) {
            imageWidth = PROFILE_IMAGE_SIZE
            imageHeight = PROFILE_IMAGE_SIZE
        } else {
            imageWidth = LANDSCAPE_IMAGE_WIDTH
        }
        isDefault16And9Ratio = true
        sListener = listener
        getMediaTypeEnum = mediaTypeEnum
        multiSelect = false
        Companion.numberOfPictures = if (getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE) {
            1
        } else if (getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE) {
            numberOfPictures
        } else {
            1
        }
        val intent = Intent(activity, SelectActivity::class.java)
        activity.startActivity(intent)
    }


    /*fun show(cropXRatio: Int, cropYRatio: Int, numberOfPictures: Int, listener: MultiListener) {
        var numberOfPictures = numberOfPictures
        addresses = ArrayList()
        if (numberOfPictures < 2) numberOfPictures = 2 else if (numberOfPictures > 1000) numberOfPictures = 1000
        x = cropXRatio.toFloat()
        y = cropYRatio.toFloat()
        if (cropXRatio == 1 && cropYRatio == 1) {
            imageWidth = PROFILE_IMAGE_SIZE
            imageHeight = PROFILE_IMAGE_SIZE
        } else {
            imageWidth = LANDSCAPE_IMAGE_WIDTH
        }
        multiSelect = true
        isDefault16And9Ratio = true
        Companion.numberOfPictures = numberOfPictures
        mListener = listener
        val intent = Intent(activity, SelectActivity::class.java)
        activity.startActivity(intent)
    }*/

    fun show(mediaTypeEnum: MediaTypeEnum = MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, cropXRatio: Int, cropYRatio: Int, numberOfPictures: Int, listener: MultiListener) {
        var numberOfPictures = numberOfPictures
        addresses = ArrayList()
        if (numberOfPictures < 2) numberOfPictures = 2 else if (numberOfPictures > 1000) numberOfPictures = 1000
        x = cropXRatio.toFloat()
        y = cropYRatio.toFloat()
        if (cropXRatio == 1 && cropYRatio == 1) {
            imageWidth = PROFILE_IMAGE_SIZE
            imageHeight = PROFILE_IMAGE_SIZE
        } else {
            imageWidth = LANDSCAPE_IMAGE_WIDTH
        }
        getMediaTypeEnum = mediaTypeEnum
        multiSelect = true
        isDefault16And9Ratio = true
        Companion.numberOfPictures = numberOfPictures
        mListener = listener
        val intent = Intent(activity, SelectActivity::class.java)
        activity.startActivity(intent)
    }



    @Throws(Throwable::class)
    protected fun finalize() {

    }

    companion object {
        var x = 0f
        var y = 0f
        var multiSelect = false
        var numberOfPictures = 1
        var imageWidth = 0
        var imageHeight = 0
        var PROFILE_IMAGE_SIZE = 800
        var LANDSCAPE_IMAGE_WIDTH = 1080
        var LANDSCAPE_IMAGE_HEIGHT = 607
        var isDefault16And9Ratio = false
        var addresses: ArrayList<String> = arrayListOf()
        var getMediaTypeEnum: MediaTypeEnum? = null
        var mListener: MultiListener? = null
        var sListener: SingleListener? = null
    }
}