package com.nanamare.mac.grab.data.source.remote

import com.nanamare.mac.grab.data.source.GeocodeDataSource
import com.nanamare.mac.grab.ext.networkDispatchToMainThread
import com.nanamare.mac.grab.network.api.GeocodeAPI
import com.nanamare.mac.grab.network.response.GeocodeResponse
import com.nanamare.mac.grab.network.response.PlaceResponse
import com.nanamare.mac.grab.network.response.ReverseGeocodeResponse
import io.reactivex.Single

class RemoteGeocodeDataSourceImpl(private val geocodeAPI: GeocodeAPI) : GeocodeDataSource {

    override fun getLocationUseAddress(address: String): Single<GeocodeResponse> =
        geocodeAPI.getLocationUseAddress(address).networkDispatchToMainThread()

    override fun getPlace(address: String): Single<PlaceResponse> =
        geocodeAPI.getPlace(address).networkDispatchToMainThread()

    override fun getLocationUseLatLng(latLng: String): Single<ReverseGeocodeResponse> =
        geocodeAPI.getLocationUseLatLng(latLng).networkDispatchToMainThread()
}