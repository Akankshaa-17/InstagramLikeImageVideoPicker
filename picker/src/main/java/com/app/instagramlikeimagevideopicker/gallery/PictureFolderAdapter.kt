package com.app.instagramlikeimagevideopicker.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.app.instagramlikeimagevideopicker.R
import com.app.instagramlikeimagevideopicker.extension.clickWithDebounce
import com.app.instagramlikeimagevideopicker.gallery.PictureFolderAdapter.FolderHolder
import java.util.*

/**
 * An adapter for populating RecyclerView with items representing folders that contain images
 */
class PictureFolderAdapter
/**
 * @param folders     An ArrayList of String that represents paths to folders on the external storage that contain pictures
 * @param folderContx The Activity or fragment Context
 * @param listen      interFace for communication between adapter and fragment or activity
 */(
    private val folders: ArrayList<ImageFolder>, private val folderContx: Context,
    private val listenToClick: ItemClickListener,
) : RecyclerView.Adapter<FolderHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderHolder {
        val inflater = LayoutInflater.from(parent.context)
        val cell = inflater.inflate(R.layout.item_folder_gallery, parent, false)
        return FolderHolder(cell)
    }

    override fun onBindViewHolder(holder: FolderHolder, position: Int) {
        val (path, folderName, numberOfPics, firstPic) = folders[position]
        Glide.with(folderContx).load(firstPic).apply(RequestOptions().centerCrop()).into(holder.folderPic)
        //setting the number of images
        //val text = "" + folderName /*+ " (" + folder.getNumberOfPics() + ")"*/
        val folderSizeString = "$numberOfPics Media"
        holder.folderSize.text = folderSizeString
        holder.folderName.apply {
            text = if (folderName.startsWith("/")) {
                folderName.substringAfterLast("/")
            } else {
                folderName
            }
        }/*($numberOfPics)*/
        holder.folderPic.clickWithDebounce { v: View? -> listenToClick.onPicClicked(path,  if (folderName.startsWith("/")) {
            folderName.substringAfterLast("/")
        } else {
            folderName
        }) }
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    inner class FolderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var folderPic: ImageView = itemView.findViewById(R.id.folderPic)
        var folderName: TextView = itemView.findViewById(R.id.folderName)

        //set textview for foldersize
        var folderSize: TextView = itemView.findViewById(R.id.folderSize)
        var folderCard: CardView = itemView.findViewById(R.id.folderCard)

    }
}