package com.nanamare.mac.grab.data.source

import com.nanamare.mac.grab.data.source.remote.RemoteGeocodeDataSourceImpl
import com.nanamare.mac.grab.network.response.GeocodeResponse
import com.nanamare.mac.grab.network.response.PlaceResponse
import com.nanamare.mac.grab.network.response.ReverseGeocodeResponse
import io.reactivex.Single

class GeocodeRepository(private val remoteGeocodeDataSourceImpl: RemoteGeocodeDataSourceImpl) :
    GeocodeDataSource {

    override fun getLocationUseAddress(address: String): Single<GeocodeResponse> =
        remoteGeocodeDataSourceImpl.getLocationUseAddress(address)

    override fun getLocationUseLatLng(latLng: String): Single<ReverseGeocodeResponse> =
        remoteGeocodeDataSourceImpl.getLocationUseLatLng(latLng)

    override fun getPlace(address: String): Single<PlaceResponse> =
        remoteGeocodeDataSourceImpl.getPlace(address)

}