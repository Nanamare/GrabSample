package com.nanamare.mac.grab.base.ui

import android.content.DialogInterface
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nanamare.mac.grab.base.navigator.BaseNavigator

abstract class BaseBottomSheetDialogFragment<B : ViewDataBinding>(private val layoutId: Int) :
    BottomSheetDialogFragment(), BaseNavigator {

    lateinit var binding: B

    var onClickListener: ((position: Int, text: String) -> Unit)? = null

    var onDismissListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissListener?.invoke()
        super.onDismiss(dialog)
    }

    fun onCloseClick() {
        dismiss()
    }


    override fun networkError(errorCode: String) {
        if (isAdded) {
            (activity as? BaseActivity<*>)?.networkError(errorCode)
        }
    }

    override fun showToast(resId: Int, error: Boolean) {
        if (isAdded) {
            context?.let {
                showToast(it.getString(resId))
            }
        }
    }

    override fun showToast(msg: String, error: Boolean) {
        if (isAdded) {
            (activity as? BaseActivity<*>)?.showToast(msg)
        }
    }

    override fun showLoadingPopup() {
        if (isAdded) {
            (activity as? BaseActivity<*>)?.showLoadingPopup()
        }
    }

    override fun hideLoadingPopup() {
        if (isAdded) {
            (activity as? BaseActivity<*>)?.hideLoadingPopup()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        manager.beginTransaction().let {
            it.add(this, tag)
            it.commitNowAllowingStateLoss()
        }
    }

    override fun hideKeyboard() {
        if (isAdded) {
            (activity as? BaseActivity<*>)?.hideKeyboard()
        }
    }

    override fun errorHandling(errorCode: String) {
        if (isAdded) {
            (activity as? BaseActivity<*>)?.errorHandling(errorCode)
        }
    }

    override fun showKeyboard() {
        if (isAdded) {
            (activity as? BaseActivity<*>)?.showKeyboard()
        }
    }

}