package com.app.instagramlikeimagevideopicker.filter

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.zomato.photofilters.imageprocessors.Filter
import com.app.instagramlikeimagevideopicker.InstagramPicker
import com.app.instagramlikeimagevideopicker.R
import com.app.instagramlikeimagevideopicker.classes.BackgroundActivity
import com.app.instagramlikeimagevideopicker.classes.Statics
import com.app.instagramlikeimagevideopicker.filter.FiltersListFragment.FiltersListFragmentListener
import com.app.instagramlikeimagevideopicker.gallery.ImageUtils
import com.app.instagramlikeimagevideopicker.gallery.ImageUtils.getBitmapToFile
import com.app.instagramlikeimagevideopicker.gallery.ImageUtils.getResizeLandscapeImage
import com.app.instagramlikeimagevideopicker.gallery.ImageUtils.getResizeProfileImage
import java.util.*

class FilterActivity : AppCompatActivity(), FiltersListFragmentListener {
    private var imagePreview: ImageView? = null
    private var originalImage: Bitmap? = null
    private var filteredImage: Bitmap? = null
    private var finalImage: Bitmap? = null
    private var filtersListFragment: FiltersListFragment? = null

    companion object {
        var position = -1
        var picAddress: Uri? = null

        init {
            System.loadLibrary("NativeImageProcessor")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        val toolbar = findViewById<Toolbar>(R.id.filter_toolbar)
        setSupportActionBar(toolbar)
        assert(supportActionBar != null)
        supportActionBar!!.title = getString(R.string.instagrampicker_filter_title)
        val viewPager = findViewById<ViewPager>(R.id.viewpager)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        imagePreview = findViewById(R.id.image_preview)
        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)
        Handler().postDelayed({
            try {
                renderImage(picAddress)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 500)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        filtersListFragment = FiltersListFragment()
        filtersListFragment!!.setListener(this)
        adapter.addFragment(filtersListFragment!!, getString(R.string.tab_filters))
        viewPager.adapter = adapter
    }

    override fun onFilterSelected(filter: Filter) {
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        imagePreview!!.setImageBitmap(filter.processFilter(filteredImage))
        finalImage = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
    }

    internal class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_open) {
            Statics.updatedPic = null
            if (position != -1) {
                val i = Intent("ImageUpdated")
                Statics.updatedPic = filteredImage
                i.putExtra("position", position)
                sendBroadcast(i)
                finish()
            } else {
                try {
                    val b = finalImage
                    val localFile = getBitmapToFile(this@FilterActivity, b!!)
                    val uri = Uri.fromFile(localFile)
                    if (InstagramPicker.addresses == null) {
                        InstagramPicker.addresses = ArrayList()
                    }
                    var selectedPostBitmap: Bitmap? = null
                    selectedPostBitmap = if (InstagramPicker.x == 1f && InstagramPicker.y == 1f) {
                        getResizeProfileImage(this@FilterActivity, ImageUtils.ScalingLogic.CROP, uri.path!!, uri)
                    } else {
                        getResizeLandscapeImage(this@FilterActivity, ImageUtils.ScalingLogic.CROP, uri.path!!, uri)
                    }
                    val localSelectedFile = getBitmapToFile(this@FilterActivity, selectedPostBitmap!!)
                    InstagramPicker.addresses.add(Uri.fromFile(localSelectedFile).toString())
                    sendBroadcast(Intent(Statics.INTENT_FILTER_ACTION_NAME))
                    finish()
                    BackgroundActivity.instance?.activity!!.finish()
                } catch (ignored: Exception) {
                }
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Throws(Exception::class)
    private fun renderImage(uri: Uri?) {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
        imagePreview!!.setImageBitmap(originalImage)
        bitmap.recycle()
        filtersListFragment!!.prepareThumbnail(originalImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val SELECT_GALLERY_IMAGE = 101
        if (resultCode == RESULT_OK && requestCode == SELECT_GALLERY_IMAGE) {
            val bitmap = BitmapUtils.getBitmapFromGallery(this, data!!.data)
            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
            finalImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
            imagePreview!!.setImageBitmap(originalImage)
            bitmap.recycle()
            filtersListFragment!!.prepareThumbnail(originalImage)
        } else {
            onBackPressed()
        }
    }
}