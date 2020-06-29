package com.nanamare.mac.grab.base.ui


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.nanamare.mac.grab.base.navigator.BaseNavigator
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment<B : ViewDataBinding>(@LayoutRes private val layoutId: Int) : Fragment(),
    BaseNavigator {

    protected lateinit var binding: B
    protected val compositeDisposable = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        super.onDestroyView()
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
        (activity as? BaseActivity<*>)?.showToast(msg)
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

    override fun onDetach() {
        (activity as? BaseActivity<*>)?.hideLoadingPopup()
        super.onDetach()
    }

}
