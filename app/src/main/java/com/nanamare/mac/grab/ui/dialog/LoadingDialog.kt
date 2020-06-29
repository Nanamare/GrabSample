package com.nanamare.mac.grab.ui.dialog

import android.view.WindowManager
import com.nanamare.mac.grab.R
import com.nanamare.mac.grab.base.ui.BaseDialogFragment
import com.nanamare.mac.grab.databinding.LoadingDialogBinding


class LoadingDialog
    : BaseDialogFragment<LoadingDialogBinding>(R.layout.loading_dialog) {

    override fun onStart() {
        super.onStart()
        val window = requireDialog().window
        val windowParams = window!!.attributes
        windowParams.dimAmount = 0.5f
        windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.attributes = windowParams
    }

    companion object {
        fun newInstance() = LoadingDialog()
    }

}