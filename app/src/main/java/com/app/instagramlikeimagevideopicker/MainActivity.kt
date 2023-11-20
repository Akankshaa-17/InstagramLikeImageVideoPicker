package com.app.instagramlikeimagevideopicker

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.instagramlikeimagevideopicker.classes.SingleListener
import com.app.instagramlikeimagevideopicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.mainButton.setOnClickListener {
            InstagramPicker(this).show(MediaTypeEnum.IMAGE_GALLERY_AND_CAPTURE, 1, 1, false, object : SingleListener {
                override fun selectedPic(address: String) {
                    b.mainPreview.setImageURI(Uri.parse(address))
                }
            })
        }

    }
}
