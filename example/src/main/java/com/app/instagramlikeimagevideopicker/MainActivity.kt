package com.app.instagramlikeimagevideopicker

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.instagramlikeimagevideopicker.classes.MultiListener
import com.app.instagramlikeimagevideopicker.classes.SingleListener
import com.app.instagramlikeimagevideopicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.mainButton.setOnClickListener {

            //For single video/image selection
            InstagramPicker
                .Builder(this)
                .setType(MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE)
                .setCropXRatio(1)
                .setCropYRatio(1)
                .setDefault16And9Ratio(false)
                .setSingleListener(object : SingleListener {
                override fun selectedPic(address: String) {
                    b.mainPreview.setImageURI(Uri.parse(address))
                }
            }).build().show()


            //For multiple video/image selection
            /*InstagramPicker
                .Builder(this)
                .setType(MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE)
                .setCropXRatio(1)
                .setCropYRatio(1)
                .setDefault16And9Ratio(true)
                .setNumberOfPictures(4)
                .setMultiSelect(true)
                .setMultiSelectListener(object : MultiListener{
                    override fun selectedPics(addresses: List<String>) {
                        b.mainPreview.setImageURI(Uri.parse(addresses[0]))
                    }
                }).build().show()*/

        }

    }
}
