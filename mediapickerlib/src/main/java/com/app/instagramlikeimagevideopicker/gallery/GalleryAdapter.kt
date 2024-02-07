package com.app.instagramlikeimagevideopicker.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.app.instagramlikeimagevideopicker.InstagramPicker
import com.app.instagramlikeimagevideopicker.R
import com.app.instagramlikeimagevideopicker.extension.clickWithDebounce
import java.util.concurrent.TimeUnit


class GalleryAdapter internal constructor(private val context: Context, private val list: List<GalleryModel>, private val galleySelectedListener: GalleySelectedListener, multiSelect: Boolean) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private var count = 0
    private val selectedPics: MutableList<String>
    private val multiSelect: Boolean

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_gallery_pics, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(h: ViewHolder, position: Int) {
        val model = list[position]
        Glide.with(context).load(model.address).apply(RequestOptions().centerCrop()).into(h.pic)
        h.rlBg.visibility = if (model.isSelected) View.VISIBLE else View.GONE
        h.bgSelect.isChecked = model.isSelected
        h.clMain.clickWithDebounce {
            if (multiSelect) {
                if (count == selectedPics.size) {
                    if (model.isSelected) {
                        h.rlBg.visibility = View.GONE
                        selectedPics.remove(model.address)
                    }
                    return@clickWithDebounce
                }
                model.isSelected = !model.isSelected
                h.rlBg.visibility = if (model.isSelected) View.VISIBLE else View.GONE
                if (model.isSelected) {
                    h.bgSelect.isChecked = !h.bgSelect.isChecked
                    selectedPics.add(model.address)
                } else {
                    selectedPics.remove(model.address)
                }
                galleySelectedListener.onMultiSelect(selectedPics)
            } else {
                galleySelectedListener.onSingleSelect(model.address)
            }
        }

        if (model.address.endsWith(".jpg", true) || model.address.endsWith(".jpeg",true) || model.address.endsWith(".png",true) || model.address.endsWith(".webp", true)) {
            h.txtTime.visibility = View.GONE
        } else {
            h.txtTime.visibility = View.VISIBLE
            try {
                val mp: MediaPlayer = MediaPlayer.create(context, Uri.parse(model.address)) // Downloads is the folder and vid is video file.
                val duration = mp.duration
                val mm: Long = TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
                val ss: Long = TimeUnit.MILLISECONDS.toSeconds(duration.toLong())
                h.txtTime.text = String.format("%02d:%02d", mm, ss)
                mp.release()
            }catch (e: Exception){
                e.printStackTrace()
                h.txtTime.text = "00:00"
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val pic: AppCompatImageView = v.findViewById(R.id.row_gallery_pic)
        val bgSelect: RadioButton = v.findViewById(R.id.row_gallery_select)
        val rlBg: RelativeLayout = v.findViewById(R.id.rl_bg)
        val clMain: ConstraintLayout = v.findViewById(R.id.cl_main)
        val txtTime: AppCompatTextView = v.findViewById(R.id.txt_duration)
    }

    init {
        if (multiSelect) {
            count = InstagramPicker.numberOfPictures
        }
        this.multiSelect = multiSelect
        selectedPics = ArrayList()
        if (!multiSelect) {
            for (i in 1 until list.size) list[i].isSelected = false
        }
    }
}