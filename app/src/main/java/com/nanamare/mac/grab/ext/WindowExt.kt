package com.nanamare.mac.grab.ext

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

fun Window.enableTransparentStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val winParams = attributes
        winParams.flags =
            winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        attributes = winParams
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}