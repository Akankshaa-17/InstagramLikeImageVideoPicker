package com.app.instagramlikeimagevideopicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.app.instagramlikeimagevideopicker.databinding.FragmentMainBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment(R.layout.fragment_main) {
    var binding: FragmentMainBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        setTabLayout()

        return binding?.root
    }

    /*private fun createTab() {
        binding?.tabPhoto?.setOnClickListener{
            parentFragmentManager.beginTransaction().replace(R.id.containerView, CameraFragment()).commit()
        }

        binding?.tabVideo?.setOnClickListener{
            parentFragmentManager.beginTransaction().replace(R.id.containerView, CaptureFragment()).commit()
        }
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


   /* private fun createTab() {
        // Initializing the ViewPagerAdapter
        val adapter = ViewPagerAdapter(parentFragmentManager)

        // add fragment to the list

        adapter.addFragment(CameraFragment(), "Photo")

        adapter.addFragment(CaptureFragment(), "Video")


        // Adding the Adapter to the ViewPager
        binding?.viewPager?.adapter = adapter

        // bind the viewPager with the TabLayout.
        binding?.tabLayout?.setupWithViewPager(binding?.viewPager)
    }*/

    private fun setTabLayout() {
        val data = resources.getStringArray(R.array.tab_array_1)

        val viewPagerAdapter =  ViewPagerAdapter(parentFragmentManager, lifecycle)
        binding?.viewPager?.adapter = viewPagerAdapter
        TabLayoutMediator(binding?.tabLayout!!, binding?.viewPager!!) { tab, position ->
            tab.text = data[position]
        }.attach()


        binding?.viewPager?.isUserInputEnabled = false
        binding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }
}