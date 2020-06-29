package com.nanamare.mac.grab.base.navigator

interface BaseNavigator {

    fun showLoadingPopup()

    fun hideLoadingPopup()

    fun networkError(errorCode: String = "")

    fun showToast(resId: Int, error: Boolean = false)

    fun showToast(msg: String, error: Boolean = false)

    fun errorHandling(errorCode: String = "")

    fun hideKeyboard()

    fun showKeyboard()

}