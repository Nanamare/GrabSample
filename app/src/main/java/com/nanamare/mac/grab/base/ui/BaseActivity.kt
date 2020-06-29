package com.nanamare.mac.grab.base.ui

import android.app.Activity
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.nanamare.mac.grab.R
import com.nanamare.mac.grab.base.navigator.BaseNavigator
import com.nanamare.mac.grab.ext.dip
import com.nanamare.mac.grab.ui.dialog.LoadingDialog
import io.reactivex.disposables.CompositeDisposable


abstract class BaseActivity<B : ViewDataBinding>(@LayoutRes private val layoutId: Int) :
    AppCompatActivity(),
    BaseNavigator {

    protected lateinit var binding: B

    protected val compositeDisposable = CompositeDisposable()
    private var loadingPopup: LoadingDialog? = null
    private var toast: Toast? = null
    private val toastLayout by lazy {
        layoutInflater.inflate(
            R.layout.custom_toast,
            binding.root as ViewGroup,
            false
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun networkError(errorCode: String) {

    }

    override fun errorHandling(errorCode: String) {

    }

    override fun showToast(resId: Int, error: Boolean) {
        showToast(getString(resId), error)
    }

    override fun showToast(msg: String, error: Boolean) {
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        toastLayout.findViewById<TextView>(R.id.tv_toast_text).run {
            width = size.x
            text = msg
            val colorId = if (error) {
                R.color.error
            } else {
                R.color.point_5FA9D0
            }
            setBackgroundColor(ResourcesCompat.getColor(resources, colorId, theme))
        }
        Handler(Looper.getMainLooper()).post {
            if (toast != null) {
                toast?.cancel()
            }
            toast = Toast(applicationContext).apply {
                setGravity(Gravity.FILL_HORIZONTAL or Gravity.TOP, 0, dip(50))
                duration = Toast.LENGTH_SHORT
                view = toastLayout
            }
            toast?.show()
        }
    }

    override fun showKeyboard() {
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            0
        )
    }

    override fun hideKeyboard() {
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            (currentFocus
                ?: View(this)).windowToken, 0
        )
    }

    override fun showLoadingPopup() {
        if (loadingPopup == null) {
            loadingPopup = LoadingDialog.newInstance().apply {
                isCancelable = false
                show(supportFragmentManager, loadingPopup)
            }
        }
    }

    override fun hideLoadingPopup() {
        if (loadingPopup != null) {
            loadingPopup?.dismissAllowingStateLoss()
            loadingPopup = null
        }
    }

    companion object {
        @JvmStatic
        val TAG: String = BaseActivity::class.java.simpleName
    }
}
