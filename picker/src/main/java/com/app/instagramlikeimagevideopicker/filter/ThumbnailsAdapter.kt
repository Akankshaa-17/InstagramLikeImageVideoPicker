package com.app.instagramlikeimagevideopicker.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.app.instagramlikeimagevideopicker.R
import com.app.instagramlikeimagevideopicker.filter.ThumbnailsAdapter.MyViewHolder

class ThumbnailsAdapter internal constructor(private val mContext: Context, private val thumbnailItemList: List<ThumbnailItem>, private val listener: ThumbnailsAdapterListener) : RecyclerView.Adapter<MyViewHolder>() {
    private var selectedIndex = 0

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.thumbnail)
        val filterName: TextView = view.findViewById(R.id.filter_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.thumbnail_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val thumbnailItem = thumbnailItemList[position]
        holder.thumbnail.setImageBitmap(thumbnailItem.image)
        holder.thumbnail.setOnClickListener { view: View? ->
            listener.onFilterSelected(thumbnailItem.filter)
            selectedIndex = holder.adapterPosition
            notifyDataSetChanged()
        }
        holder.filterName.text = thumbnailItem.filterName
        if (selectedIndex == position) {
            holder.filterName.setTextColor(ContextCompat.getColor(mContext, R.color.filter_label_selected))
        } else {
            holder.filterName.setTextColor(ContextCompat.getColor(mContext, R.color.filter_label_normal))
        }
    }

    override fun getItemCount(): Int {
        return thumbnailItemList.size
    }

    interface ThumbnailsAdapterListener {
        fun onFilterSelected(filter: Filter)
    }
}