package com.nanamare.mac.grab.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.securepreferences.SecurePreferences

class PrefUtils(context: Context) {

    private val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
    private val secureSharedPref = SecurePreferences(context)

    private fun getEdit() = sharedPref.edit()

    private fun getSecureEdit() = secureSharedPref.edit()

    companion object {
        const val PREF_KEY_SEARCH_LOCATION_LIST = "PREF_KEY_SEARCH_LOCATION_LIST"
    }

    fun saveLocations(locationListToJsonString: String) {
        getSecureEdit().putString(PREF_KEY_SEARCH_LOCATION_LIST, locationListToJsonString).apply()
    }

    fun loadLocations(): String {
        return secureSharedPref.getString(PREF_KEY_SEARCH_LOCATION_LIST, "") ?: ""
    }


}