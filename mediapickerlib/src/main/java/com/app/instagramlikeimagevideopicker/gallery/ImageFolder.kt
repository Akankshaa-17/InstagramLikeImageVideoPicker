package com.app.instagramlikeimagevideopicker.gallery

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Custom Class that holds information of a folder containing images
 * on the device external storage, used to populate our RecyclerView of
 * picture folders
 */
@Parcelize
data class ImageFolder(
    var path: String = "",
    var folderName: String = "",
    var numberOfPics: Int = 0,
    var firstPic: String = ""

) : Parcelable {

    fun addPics() {
        numberOfPics++
    }
}