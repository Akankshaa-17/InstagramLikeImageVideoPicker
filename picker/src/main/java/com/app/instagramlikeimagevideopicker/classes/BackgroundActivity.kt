package com.app.instagramlikeimagevideopicker.classes

import androidx.appcompat.app.AppCompatActivity

class BackgroundActivity {
    var activity: AppCompatActivity? = null
        private set

    fun setActivity(activity: AppCompatActivity) {
        this.activity = activity
    }

    companion object {
        var instance: BackgroundActivity? = null
            get() {
                if (field == null) field = BackgroundActivity()
                return field
            }
            private set
    }
}