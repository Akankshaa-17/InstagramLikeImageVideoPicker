package com.app.instagramlikeimagevideopicker.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.app.instagramlikeimagevideopicker.R
import com.app.instagramlikeimagevideopicker.databinding.FragmentAllAlbumDialogBinding
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [AllAlbumBottomSheetDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllAlbumBottomSheetDialogFragment : BottomSheetDialogFragment(), ItemClickListener {

    private var binding: FragmentAllAlbumDialogBinding? = null

    companion object {
        private var itemClickListener: ItemClickListener? = null
        fun newInstance(selectedIdList: ArrayList<ImageFolder>, itemClickListener1: ItemClickListener) = AllAlbumBottomSheetDialogFragment().apply {
            this.arguments = Bundle().apply {
                this.putParcelableArrayList("album_list", selectedIdList)
                itemClickListener = itemClickListener1
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_album_dialog, container, false)
        return binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponent()

        binding!!.tvCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun initComponent() {
        val albumList = arguments?.getParcelableArrayList<ImageFolder>("album_list")

        binding!!.rvAlbum.layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        val adapter = PictureFolderAdapter(albumList!!, requireActivity(), this)
        binding!!.rvAlbum.adapter = adapter
    }

    override fun onPicClicked(pictureFolderPath: String?, folderName: String?) {
        itemClickListener!!.onPicClicked(pictureFolderPath,folderName)
        dismiss()
    }

}