package com.nanamare.mac.grab.utils

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

object SimpleAnimator {

    fun carStartAnim(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 500
            interpolator = LinearInterpolator()
        }
    }

}