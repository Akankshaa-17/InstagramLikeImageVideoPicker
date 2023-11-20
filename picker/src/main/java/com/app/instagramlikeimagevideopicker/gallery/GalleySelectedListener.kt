package com.app.instagramlikeimagevideopicker.gallery

interface GalleySelectedListener {
    fun onSingleSelect(address: String)
    fun onMultiSelect(addresses: List<String>)
}