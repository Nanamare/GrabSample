package com.nanamare.mac.grab.data.source.vo

import com.google.android.gms.maps.model.LatLng

data class LocationVO(val latLng: LatLng, val addressName: String? = "", val name: String? = "")