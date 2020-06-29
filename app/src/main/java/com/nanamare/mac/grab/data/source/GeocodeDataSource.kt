package com.nanamare.mac.grab.data.source

import com.nanamare.mac.grab.network.response.GeocodeResponse
import com.nanamare.mac.grab.network.response.PlaceResponse
import com.nanamare.mac.grab.network.response.ReverseGeocodeResponse
import io.reactivex.Single

interface GeocodeDataSource {
    fun getLocationUseAddress(address: String): Single<GeocodeResponse>
    fun getLocationUseLatLng(latLng: String): Single<ReverseGeocodeResponse>
    fun getPlace(address: String): Single<PlaceResponse>
}