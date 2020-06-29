package com.nanamare.mac.grab.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun getCarBitmap(
    resources: Resources,
    resId: Int,
    width: Int,
    height: Int
): Bitmap {
    val bitmap = BitmapFactory.decodeResource(resources, resId)
    return Bitmap.createScaledBitmap(bitmap, width, height, false)
}