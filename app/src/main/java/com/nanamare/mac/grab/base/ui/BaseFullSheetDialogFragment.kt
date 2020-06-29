package com.nanamare.mac.grab.base.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.nanamare.mac.grab.R
import com.nanamare.mac.grab.base.navigator.BaseNavigator

abstract class BaseFullSheetDialogFragment<B : ViewDataBinding>(private val layoutId: Int) :
    DialogFragment(), BaseNavigator {

    lateinit var binding: B

    var onClickListener: ((position: Int, text: String) -> Unit)? = null

    var onDismissListener: (() -> Unit)? = null

    var onBackPressed: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this

        requireDialog().run {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    android.R.color.background_light
                )
            )
            setOnShowListener { dialog ->
                val d = dialog as Dialog
                d.setOnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        onCloseClick()
                        true
                    } else {
                        false
                    }
                }
            }
            setCanceledOnTouchOutside(false)
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissListener?.invoke()
        super.onDismiss(dialog)
    }

    open fun onCloseClick() {
        dismiss()
    }

    override fun onDetach() {
        (activity as? BaseActivity<*>)?.hideLoadingPopup()
        super.onDetach()
    }

    override fun networkError(errorCode: String) {
        (activity as? BaseActivity<*>)?.networkError(errorCode)
    }

    override fun errorHandling(errorCode: String) {
        (activity as? BaseActivity<*>)?.errorHandling(errorCode)
    }

    override fun showToast(resId: Int, error: Boolean) {
        context?.let {
            showToast(it.getString(resId))
        }
    }

    override fun showToast(msg: String, error: Boolean) {
        (activity as? BaseActivity<*>)?.showToast(msg, error)
    }

    override fun showKeyboard() {
        (activity as? BaseActivity<*>)?.showKeyboard()
    }

    override fun hideKeyboard() {
        (activity as? BaseActivity<*>)?.hideKeyboard()
    }

    override fun showLoadingPopup() {
        (activity as? BaseActivity<*>)?.showLoadingPopup()
    }

    override fun hideLoadingPopup() {
        (activity as? BaseActivity<*>)?.hideLoadingPopup()
    }

}