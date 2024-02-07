package com.app.instagramlikeimagevideopicker.classes

import android.graphics.Bitmap
import java.text.SimpleDateFormat
import java.util.*

object Statics {
    var updatedPic: Bitmap? = null
    const val INTENT_FILTER_ACTION_NAME = "instagrampicker_refresh"
    val currentDate: String
        get() {
            val d = Date()
            return SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(d)
        }
}