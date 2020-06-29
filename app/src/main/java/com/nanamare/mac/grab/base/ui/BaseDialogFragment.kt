package com.nanamare.mac.grab.base.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.nanamare.mac.grab.base.navigator.BaseNavigator

abstract class BaseDialogFragment<B : ViewDataBinding>(@LayoutRes private val layoutId: Int) :
    DialogFragment(), BaseNavigator {

    lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
            it.commitAllowingStateLoss() // 동기적으로 하고싶을 때는 commitNowAllowingStateLoss()
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
