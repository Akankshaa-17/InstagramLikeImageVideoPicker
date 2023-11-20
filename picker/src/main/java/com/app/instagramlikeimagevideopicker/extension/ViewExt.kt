package com.app.instagramlikeimagevideopicker.extension

import android.os.SystemClock
import android.view.View

fun View.clickWithDebounce(debounceTime: Long = 700L, action: (view: View) -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action(v)

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}