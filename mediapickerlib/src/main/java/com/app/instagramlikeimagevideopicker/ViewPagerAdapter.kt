package com.app.instagramlikeimagevideopicker

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.instagramlikeimagevideopicker.capture.CaptureFragment
import com.app.instagramlikeimagevideopicker.gallery.CameraFragment

private const val NUM_TABS = 2

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return CameraFragment()
            1 -> return CaptureFragment()
        }
        return CameraFragment()
    }
}
