package com.app.instagramlikeimagevideopicker.filter

import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import com.app.instagramlikeimagevideopicker.R
import com.app.instagramlikeimagevideopicker.filter.ThumbnailsAdapter.ThumbnailsAdapterListener
import java.util.*

class FiltersListFragment : Fragment(), ThumbnailsAdapterListener {
    private var mAdapter: ThumbnailsAdapter? = null
    private var thumbnailItemList: MutableList<ThumbnailItem>? = null
    private var listener: FiltersListFragmentListener? = null
    fun setListener(listener: FiltersListFragmentListener?) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_filters_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        thumbnailItemList = ArrayList()
        mAdapter = ThumbnailsAdapter(requireActivity(), thumbnailItemList!!, this)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        val space = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8f,
            resources.displayMetrics
        ).toInt()
        recyclerView.addItemDecoration(SpacesItemDecoration(space))
        recyclerView.adapter = mAdapter
        prepareThumbnail(null)
        return view
    }

    fun prepareThumbnail(bitmap: Bitmap?) {
        val r = Runnable {
            val thumbImage: Bitmap? = if (bitmap == null) {
                return@Runnable
            } else {
                Bitmap.createScaledBitmap(bitmap, 100, 100, false)
            }
            if (thumbImage == null) return@Runnable
            ThumbnailsManager.clearThumbs()
            thumbnailItemList!!.clear()
            val thumbnailItem = ThumbnailItem()
            thumbnailItem.image = thumbImage
            thumbnailItem.filterName = getString(R.string.filter_normal)
            ThumbnailsManager.addThumb(thumbnailItem)
            val filters = FilterPack.getFilterPack(activity)
            for (filter in filters) {
                val tI = ThumbnailItem()
                tI.image = thumbImage
                tI.filter = filter
                tI.filterName = filter.name
                ThumbnailsManager.addThumb(tI)
            }
            thumbnailItemList!!.addAll(ThumbnailsManager.processThumbs(requireActivity()))
            requireActivity().runOnUiThread { mAdapter!!.notifyDataSetChanged() }
        }
        Thread(r).start()
    }

    override fun onFilterSelected(filter: Filter) {
        if (listener != null) listener!!.onFilterSelected(filter)
    }

    interface FiltersListFragmentListener {
        fun onFilterSelected(filter: Filter)
    }
}