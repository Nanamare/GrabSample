package com.nanamare.mac.grab.base.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.nanamare.mac.grab.base.navigator.BaseNavigator

abstract class BaseCustomView<B : ViewDataBinding>
    : FrameLayout, BaseNavigator {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
        getAttrs(attrs, defStyleAttr)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
        getAttrs(attrs, defStyleAttr, defStyleRes)
    }

    @SuppressLint("CustomViewStyleable")
    private fun getAttrs(attrs: AttributeSet?) {
        setTypeArray(context.obtainStyledAttributes(attrs, getCustomViewStyle()))
    }

    @SuppressLint("CustomViewStyleable")
    private fun getAttrs(attrs: AttributeSet?, defStyle: Int, defStyleRes: Int = 0) {
        setTypeArray(
            context.obtainStyledAttributes(
                attrs,
                getCustomViewStyle(),
                defStyle,
                defStyleRes
            )
        )
    }

    private fun initView() {
        binding = DataBindingUtil.bind(LayoutInflater.from(context)
            .inflate(getLayoutId(), this@BaseCustomView, false).apply {
                addView(this)
            })!!
    }


    abstract fun setTypeArray(typedArray: TypedArray)

    abstract fun getLayoutId(): Int

    abstract fun getCustomViewStyle(): IntArray

    protected lateinit var binding: B

    fun setLifecycleOwnerToDataBinding(owner: LifecycleOwner) {
        binding.lifecycleOwner = owner
    }

    override fun networkError(errorCode: String) {
        context?.let {
            showToast(errorCode)
        }
    }

    override fun showToast(resId: Int, error: Boolean) {
        context?.let {
            showToast(it.getString(resId))
        }
    }

    override fun errorHandling(errorCode: String) {
        (context as? BaseActivity<*>)?.errorHandling(errorCode)
    }

    override fun showToast(msg: String, error: Boolean) {
        (context as? BaseActivity<*>)?.showToast(msg, error)
    }

    override fun showKeyboard() {
        (context as? BaseActivity<*>)?.showKeyboard()
    }

    override fun hideKeyboard() {
        (context as? BaseActivity<*>)?.hideKeyboard()
    }

    override fun showLoadingPopup() {
        (context as? BaseActivity<*>)?.showLoadingPopup()
    }

    override fun hideLoadingPopup() {
        (context as? BaseActivity<*>)?.hideLoadingPopup()
    }

}