package com.app.instagramlikeimagevideopicker

import android.app.Activity
import android.content.Intent
import com.app.instagramlikeimagevideopicker.classes.MultiListener
import com.app.instagramlikeimagevideopicker.classes.SingleListener
import com.app.instagramlikeimagevideopicker.gallery.SelectActivity

class InstagramPicker(private val activity: Activity) {
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

    class Builder(val activity: Activity) {

        private var instagramPicker : InstagramPicker = InstagramPicker(activity)

        fun setType(mediaTypeEnum: MediaTypeEnum ) = apply { getMediaTypeEnum = mediaTypeEnum }

        fun setCropXRatio(cropXRatio: Int) = apply { x = cropXRatio.toFloat() }

        fun setCropYRatio(cropYRatio: Int) = apply { y = cropYRatio.toFloat() }

        fun setDefault16And9Ratio(default16And9Ratio: Boolean) = apply { isDefault16And9Ratio = default16And9Ratio }

        fun setSingleListener(singleListener: SingleListener) = apply { sListener = singleListener }

        fun setMultiSelect(isMultiSelect: Boolean) = apply { multiSelect = isMultiSelect }

        fun setNumberOfPictures(nos: Int) = apply { numberOfPictures = nos }

        fun setMultiSelectListener(multiSelectListener: MultiListener) = apply { mListener = multiSelectListener }

        fun build() = instagramPicker
    }


    fun show() {
        if (numberOfPictures < 2) numberOfPictures = 2 else if (numberOfPictures > 1000) numberOfPictures = 1000
        numberOfPictures = when (getMediaTypeEnum) {
            MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE -> {
                1
            }
            MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE -> {
                numberOfPictures
            }
            else -> {
                1
            }
        }
        if (x.toInt() == 1 && y.toInt() == 1) {
            imageWidth = PROFILE_IMAGE_SIZE
            imageHeight = PROFILE_IMAGE_SIZE
        } else {
            imageWidth = LANDSCAPE_IMAGE_WIDTH
        }
        val intent = Intent(activity, SelectActivity::class.java)
        activity.startActivity(intent)
    }

    @Throws(Throwable::class)
    protected fun finalize() {

    }
}