package com.nanamare.mac.grab.data.source.vo

import com.google.android.gms.maps.model.LatLng

data class DirectionVO(val latLng: List<LatLng>, val time: String?, val distance: String?, val key: Int)