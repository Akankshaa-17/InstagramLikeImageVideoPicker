package com.app.instagramlikeimagevideopicker.widget.autoscrollpager

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

/**
 * Created by HB
 */
abstract class InfinitePagerAdapter : PagerAdapter() {


    abstract val actualCount: Int

    override fun getCount(): Int {
        return Integer.MAX_VALUE
    }

    private fun getListPosition(position: Int): Int {
        return position % actualCount
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object` === view
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return onBind(container, getListPosition(position))
    }

    abstract fun onBind(container: ViewGroup, position: Int): Any


}
