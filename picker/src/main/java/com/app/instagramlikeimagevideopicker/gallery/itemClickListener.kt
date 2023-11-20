package com.app.instagramlikeimagevideopicker.gallery

/**
 * Author CodeBoy722
 */
interface itemClickListener {
    //    void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics);
    fun onPicClicked(pictureFolderPath: String?, folderName: String?)
}