package com.app.instagramlikeimagevideopicker.gallery

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.database.MergeCursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import com.yalantis.ucrop.view.CropImageView
import com.app.instagramlikeimagevideopicker.InstagramPicker
import com.app.instagramlikeimagevideopicker.MediaTypeEnum
import com.app.instagramlikeimagevideopicker.R
import com.app.instagramlikeimagevideopicker.classes.BackgroundActivity
import com.app.instagramlikeimagevideopicker.classes.Statics
import com.app.instagramlikeimagevideopicker.databinding.FragmentGalleryBinding
import com.app.instagramlikeimagevideopicker.extension.clickWithDebounce
import com.app.instagramlikeimagevideopicker.gallery.AllAlbumBottomSheetDialogFragment.Companion.newInstance
import com.app.instagramlikeimagevideopicker.gallery.ImageUtils.getBitmapToFile
import com.app.instagramlikeimagevideopicker.gallery.ImageUtils.getResizeLandscapeImage
import com.app.instagramlikeimagevideopicker.gallery.ImageUtils.getResizeProfileImage
import java.io.File
import java.util.Objects


class GalleryFragment : Fragment() {
    private var adapter: GalleryAdapter? = null
    private var multiSelect = false
    private val data: MutableList<GalleryModel> = ArrayList()
    private var selectedPic = ""
    private var selectedPics: List<String>? = null
    private var albumList = ArrayList<ImageFolder>()
    private lateinit var binding: FragmentGalleryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY) {
            binding.videoView.visibility = View.GONE
        } else if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY) {
            binding.galleryView.visibility = View.GONE
        }
        if (InstagramPicker.multiSelect) {
            binding.galleryMultiselect.visibility = View.VISIBLE
        } else {
            binding.galleryMultiselect.visibility = View.GONE
        }

        binding.galleryMultiselect.clickWithDebounce {
            val positionView = (Objects.requireNonNull(
                binding.galleryRecycler.layoutManager) as GridLayoutManager).findFirstVisibleItemPosition()
            multiSelect = !multiSelect
            binding.galleryMultiselect.setImageResource(if (multiSelect) R.mipmap.ic_multi_enable else R.mipmap.ic_multi_disable)
            adapter = GalleryAdapter(requireActivity(), data, object : GalleySelectedListener {
                override fun onSingleSelect(address: String) {
                    try {
                        if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY) {
                            Glide.with(requireActivity()).load(address).apply(RequestOptions().centerCrop()).into(binding.galleryView)
                        } else if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY) {
                            binding.videoView.setVideoURI(Uri.parse(address))
                            binding.videoView.start()
                        } else {
                            if (address.endsWith(".jpg", true) || address.endsWith(".jpeg", true) || address.endsWith(".png", true) || address.endsWith(".webp", true)) {
                                binding.galleryView.visibility = View.VISIBLE
                                binding.videoView.visibility = View.GONE
                                Glide.with(requireActivity()).load(address).apply(RequestOptions().centerCrop()).into(binding.galleryView)
                            } else {
                                binding.galleryView.visibility = View.GONE
                                binding.videoView.visibility = View.VISIBLE
                                binding.videoView.setVideoURI(Uri.parse(address))
                                binding.videoView.start()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    selectedPic = address
                }

                override fun onMultiSelect(addresses: List<String>) {
                    if (addresses.isNotEmpty()) {
                        selectedPic = ""
                        try {
                            Glide.with(requireActivity()).load(addresses[addresses.size - 1]).apply(RequestOptions().centerCrop()).into(
                                binding.galleryView)
                        } catch (ignored: Exception) {
                        }
                        selectedPics = addresses
                    }
                }
            }, multiSelect)
            binding.galleryRecycler.adapter = adapter
            adapter!!.notifyDataSetChanged()
            binding.galleryRecycler.layoutManager!!.scrollToPosition(positionView)
        }

        binding.galleryRecycler.layoutManager = GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)
        adapter = GalleryAdapter(requireActivity(), data, object : GalleySelectedListener {
            override fun onSingleSelect(address: String) {
                try {
                    if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY) {
                        Glide.with(requireActivity()).load(address).apply(RequestOptions().centerCrop()).into(binding.galleryView)
                    } else if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY) {
                        binding.videoView.setVideoURI(Uri.parse(address))
                        binding.videoView.start()
                    } else {
                        if (address.endsWith(".jpg", true) || address.endsWith(".jpeg", true) || address.endsWith(".png", true) || address.endsWith(".webp", true)) {
                            binding.galleryView.visibility = View.VISIBLE
                            binding.videoView.visibility = View.GONE
                            Glide.with(requireActivity()).load(address).apply(RequestOptions().centerCrop()).into(binding.galleryView)
                        } else {
                            binding.videoView.visibility = View.VISIBLE
                            binding.galleryView.visibility = View.GONE
                            binding.videoView.setVideoURI(Uri.parse(address))
                            binding.videoView.start()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                selectedPic = address
            }

            override fun onMultiSelect(addresses: List<String>) {
                selectedPic = ""
                if (addresses.isNotEmpty()) {
                    try {
                        Glide.with(requireActivity()).load(addresses[0]).apply(RequestOptions().centerCrop()).into(binding.galleryView)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    selectedPics = addresses
                }
            }
        }, multiSelect)
        binding.galleryRecycler.adapter = adapter
        albumList = picturePaths
        binding.tvCenterTitle.clickWithDebounce { showAllAlbum() }
        binding.tvNext.clickWithDebounce { nextBtnClick() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP && data != null) {
            val resultUri = UCrop.getOutput(data)
            val selectedPostBitmap: Bitmap? = if (InstagramPicker.x == 1f && InstagramPicker.y == 1f) {
                getResizeProfileImage(requireActivity(), ImageUtils.ScalingLogic.CROP, resultUri?.path!!, resultUri)
            } else {
                getResizeLandscapeImage(requireActivity(), ImageUtils.ScalingLogic.CROP, resultUri?.path!!, resultUri)
            }
            val localSelectedFile = getBitmapToFile(requireActivity(), selectedPostBitmap!!)
            if (InstagramPicker.addresses == null) {
                InstagramPicker.addresses = ArrayList()
            }
            InstagramPicker.addresses.add(localSelectedFile!!.absolutePath)
            requireActivity().sendBroadcast(Intent(Statics.INTENT_FILTER_ACTION_NAME))
            BackgroundActivity.instance?.activity!!.finish()
        }
    }

    private fun nextBtnClick() {
        if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY) {
            getImage()
        } else if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY) {
            if (selectedPic.isNotEmpty()) {
                InstagramPicker.addresses.add(selectedPic)
                requireActivity().sendBroadcast(Intent(Statics.INTENT_FILTER_ACTION_NAME))
                BackgroundActivity.instance?.activity!!.finish()
            }
        } else {
            if (selectedPic.isNotEmpty()) {
                if (selectedPic.endsWith(".jpg", true) || selectedPic.endsWith(".jpeg", true) || selectedPic.endsWith("png", true) || selectedPic.endsWith(".webp", true)) {
                    getImage()
                } else {
                    InstagramPicker.addresses.add(selectedPic)
                    requireActivity().sendBroadcast(Intent(Statics.INTENT_FILTER_ACTION_NAME))
                    BackgroundActivity.instance?.activity!!.finish()
                }
            }
        }
    }

    private fun getImage() {
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        options.setCompressionQuality(90)
        options.withMaxResultSize(InstagramPicker.imageWidth, InstagramPicker.imageHeight)
        options.setToolbarTitle(getString(R.string.instagrampicker_crop_title))
        options.setActiveControlsWidgetColor(ContextCompat.getColor(requireActivity(), R.color.color_006FD1))
        if (InstagramPicker.isDefault16And9Ratio) {
            options.setAspectRatioOptions(4, AspectRatio(null, 1f, 1f), AspectRatio(null, 3f, 4f),
                AspectRatio(getString(R.string.ucrop_label_original).uppercase(), CropImageView.SOURCE_IMAGE_ASPECT_RATIO,
                    CropImageView.SOURCE_IMAGE_ASPECT_RATIO), AspectRatio(null, 3f, 2f), AspectRatio(null, 16f, 9f))
        }
        val x = InstagramPicker.x
        val y = InstagramPicker.y
        if (selectedPic.isNotEmpty()) {
            val uCrop = UCrop.of(Uri.fromFile(File(selectedPic)), Uri.fromFile(File(requireActivity().cacheDir, Statics.currentDate)))
            if (!InstagramPicker.isDefault16And9Ratio) {
                uCrop.withAspectRatio(x, y)
            }
            uCrop.withOptions(options)
            uCrop.start(requireActivity(), this)
        } else if (selectedPics?.size == 1) {
            val uCrop = UCrop.of(Uri.fromFile(File(selectedPics!![0])), Uri.fromFile(File(requireActivity().cacheDir, Statics.currentDate)))
            if (!InstagramPicker.isDefault16And9Ratio) {
                uCrop.withAspectRatio(x, y)
            }
            uCrop.withOptions(options)
            uCrop.start(requireActivity(), this)
        } else if (selectedPics?.size!! > 1) {
            val i = Intent(activity, MultiSelectActivity::class.java)
            MultiSelectActivity.addresses = selectedPics!!
            startActivity(i)
            requireActivity().overridePendingTransition(R.anim.bottom_up_anim, R.anim.bottom_down_anim)
        }
    }

    private fun showAllAlbum() {
        val bottomSheetDialog = newInstance(albumList, object : itemClickListener {
            override fun onPicClicked(pictureFolderPath: String?, folderName: String?) {
                data.clear()
                adapter!!.notifyDataSetChanged()
                getAllImagesByFolder(pictureFolderPath!!)
                binding.tvCenterTitle.text = folderName
            }
        })
        bottomSheetDialog.show(childFragmentManager, "custom bottom sheet")
    }

    private fun getAllImagesByFolder(path: String) {

        val allUris = if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY) {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Files.getContentUri("external")
            }
        }

        val projection = when (InstagramPicker.getMediaTypeEnum) {
            MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, MediaTypeEnum.IMAGE_GALLERY -> {
                arrayOf(MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.SIZE)
            }

            MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE, MediaTypeEnum.VIDEO_GALLERY -> {
                arrayOf(MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.VideoColumns.SIZE)
            }

            else -> {
                arrayOf(MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.SIZE)
            }
        }

        val dateAdded = when (InstagramPicker.getMediaTypeEnum) {
            MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, MediaTypeEnum.IMAGE_GALLERY -> {
                MediaStore.Images.Media.DATE_ADDED
            }

            MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE, MediaTypeEnum.VIDEO_GALLERY -> {
                MediaStore.Video.Media.DATE_ADDED
            }

            else -> {
                MediaStore.Files.FileColumns.DATE_ADDED + " ASC"
            }
        }

        val selection = when (InstagramPicker.getMediaTypeEnum) {
            MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, MediaTypeEnum.IMAGE_GALLERY -> {
                MediaStore.Images.Media.DATA + " like ? "
            }

            MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE, MediaTypeEnum.VIDEO_GALLERY -> {
                MediaStore.Video.Media.DATA + " like ? "
            }

            else -> {
                MediaStore.Files.FileColumns.DATA + " like ? "
            }
        }

        val allData = when (InstagramPicker.getMediaTypeEnum) {
            MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, MediaTypeEnum.IMAGE_GALLERY -> {
                MediaStore.Images.Media.DATA
            }

            MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE, MediaTypeEnum.VIDEO_GALLERY -> {
                MediaStore.Video.Media.DATA
            }

            else -> {
                MediaStore.Files.FileColumns.DATA
            }
        }

        val cursor = requireActivity().contentResolver.query(allUris, projection, selection, arrayOf("%$path%"), dateAdded)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val dataPath = cursor.getString(cursor.getColumnIndexOrThrow(allData))
                    if (dataPath.endsWith(".mp4", true) || dataPath.endsWith(".3gp", true) || dataPath.endsWith(".mov",
                            true) || dataPath.endsWith(".jpg", true) || dataPath.endsWith(".jpeg", true) || dataPath.endsWith(".png",
                            true) || dataPath.endsWith(".webp", true)) {
                        var isSelected = false
                        if (selectedPics != null && selectedPics!!.isNotEmpty()) {
                            isSelected = selectedPics!!.contains(dataPath)
                        }
                        val model = GalleryModel(dataPath, isSelected)
                        data.add(0, model)
                        adapter!!.notifyItemInserted(data.size)
                    }
                } while (cursor.moveToNext())
                if (data[0].address.isNotEmpty()) {
                    selectedPic = data[0].address
                    try {
                        if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY) {
                            Glide.with(requireActivity()).load(selectedPic).apply(RequestOptions().centerCrop()).into(binding.galleryView)
                        } else if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY) {
                            binding.videoView.setVideoURI(Uri.parse(selectedPic))
                            binding.videoView.start()
                        } else {
                            if (selectedPic.endsWith(".jpg", true) || selectedPic.endsWith(".jpeg", true) || selectedPic.endsWith(".png",
                                    true) || selectedPic.endsWith(".webp", true)) {
                                binding.galleryView.visibility = View.VISIBLE
                                binding.videoView.visibility = View.GONE
                                Glide.with(requireActivity()).load(selectedPic).apply(RequestOptions().centerCrop()).into(
                                    binding.galleryView)
                            } else {
                                binding.videoView.visibility = View.VISIBLE
                                binding.galleryView.visibility = View.GONE
                                binding.videoView.setVideoURI(Uri.parse(selectedPic))
                                binding.videoView.start()
                            }
                        }
                    } catch (ignored: Exception) {
                    }
                }
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCursor(projection: Array<String>, selection: String?, selectionArgs: Array<String>?): Cursor? {
        return when (InstagramPicker.getMediaTypeEnum) {
            MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, MediaTypeEnum.IMAGE_GALLERY -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MergeCursor(arrayOf(
                        requireActivity().contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,
                            selectionArgs, null),
                        requireActivity().contentResolver.query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, projection, selection,
                            selectionArgs, null)))
                } else {
                    requireActivity().contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,
                        selectionArgs, null)
                }
            }

            MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE, MediaTypeEnum.VIDEO_GALLERY -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MergeCursor(arrayOf(
                        requireActivity().contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection,
                            selectionArgs, null),
                        requireActivity().contentResolver.query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, projection, selection,
                            selectionArgs, null)))
                } else {
                    requireActivity().contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection,
                        selectionArgs, null)
                }
            }

            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MergeCursor(arrayOf(
                        requireActivity().contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,
                            selectionArgs, null),
                        requireActivity().contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection,
                            selectionArgs, null),
                        requireActivity().contentResolver.query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, projection, selection,
                            selectionArgs, null),
                        requireActivity().contentResolver.query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, projection, selection,
                            selectionArgs, null)))
                } else {
                    MergeCursor(arrayOf(
                        requireActivity().contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,
                            selectionArgs, null),
                        requireActivity().contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, selection,
                            selectionArgs, null)))
                }
            }
        }
    }


    //if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview//String folderpaths =  datapath.replace(name,"");

    /**
     * 1
     *
     * @return gets all folders with pictures on the device and loads each of them in a custom object imageFolder
     * the returns an ArrayList of these custom objects
     */
    private val picturePaths: ArrayList<ImageFolder>
        get() {
            val picFolders = ArrayList<ImageFolder>()
            val picPaths = ArrayList<String>()

            val projection = if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.IMAGE_GALLERY) {
                arrayOf(MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID)
            } else if (InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE || InstagramPicker.getMediaTypeEnum == MediaTypeEnum.VIDEO_GALLERY) {
                arrayOf(MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME, MediaStore.Video.Media.BUCKET_ID)
            } else {
                if (Build.VERSION.SDK_INT >= 29) {
                    arrayOf(MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DISPLAY_NAME,
                        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME, MediaStore.Files.FileColumns.BUCKET_ID,
                        MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.RELATIVE_PATH)
                } else {
                    arrayOf(MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns._ID)
                }
            }

            val allData = when (InstagramPicker.getMediaTypeEnum) {
                MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, MediaTypeEnum.IMAGE_GALLERY -> {
                    MediaStore.Images.Media.DATA
                }

                MediaTypeEnum.VIDEO_GALLERY_AND_CAPTURE, MediaTypeEnum.VIDEO_GALLERY -> {
                    MediaStore.Video.Media.DATA
                }

                else -> {
                    MediaStore.Files.FileColumns.DATA
                }
            }

            val cursor = getCursor(projection, null, null)
            try {
                cursor?.moveToFirst()
                do {
                    val folds = ImageFolder()

                    val filePath = cursor?.getString(cursor.getColumnIndexOrThrow(allData))
                    val folder = filePath?.substringBeforeLast('/')

                    val dataPath = cursor?.getString(cursor.getColumnIndexOrThrow(allData))

                    val folderPaths = dataPath?.substringBeforeLast('/').toString()


                    if (!picPaths.contains(folderPaths)) {
                        picPaths.add(folderPaths)
                        folds.path = folderPaths
                        folds.folderName = folder.toString()
                        folds.firstPic = dataPath.toString()
                        //if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                        folds.addPics()
                        picFolders.add(folds)
                    } else {
                        for (i in picFolders.indices) {
                            val folds1 = picFolders[i]
                            if (folds1.path == folderPaths) {
                                folds1.firstPic = dataPath.toString()
                                folds1.addPics()
                            }
                        }

                    }
                } while (cursor?.moveToNext() == true)
                cursor?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (picFolders.isNotEmpty()) {
                picFolders.sortBy { i ->
                    if (i.folderName.startsWith("/")) {
                        i.folderName.substringAfterLast("/")
                    } else {
                        i.folderName
                    }
                }
                getAllImagesByFolder(picFolders[0].path)
                if (picFolders[0].folderName.startsWith("/")) {
                    binding.tvCenterTitle.text = picFolders[0].folderName.substringAfterLast('/')
                } else {
                    binding.tvCenterTitle.text = picFolders[0].folderName
                }
            }
            return picFolders
        }
}