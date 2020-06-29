package com.nanamare.mac.grab.ext

import android.annotation.SuppressLint
import android.view.View
import androidx.databinding.BindingAdapter
import com.jakewharton.rxbinding3.view.clicks
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
@BindingAdapter(value = ["onShortBlockClick"])
fun View.setOnShortBlockClick(listener: View.OnClickListener) {
    clicks().throttleFirst(250L, TimeUnit.MILLISECONDS)
        .subscribe {
            listener.onClick(this)
        }
}

@BindingAdapter(value = ["enabled"])
fun View.setEnabled(enabled: Boolean) {
    isEnabled = enabled
}