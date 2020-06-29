package com.nanamare.mac.grab.base.ui

import android.content.Intent
import android.os.Bundle
import com.nanamare.mac.grab.R
import com.nanamare.mac.grab.databinding.DefaultErrorActivityBinding

class BaseDefaultErrorActivity: BaseActivity<DefaultErrorActivityBinding>(R.layout.default_error_activity) {

    private val lastActivityIntent by lazy { intent.getParcelableExtra<Intent>(EXTRA_INTENT) }
    val error by lazy { intent.getStringExtra(EXTRA_ERROR_TEXT) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.view = this@BaseDefaultErrorActivity
    }

    fun onRefresh() {
        startActivity(lastActivityIntent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        const val EXTRA_INTENT = "EXTRA_INTENT"
        const val EXTRA_ERROR_TEXT = "EXTRA_ERROR_TEXT"
        const val BUILD_TYPE = "debug"
    }


}