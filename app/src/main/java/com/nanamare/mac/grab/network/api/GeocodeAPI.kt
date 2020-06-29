package com.nanamare.mac.grab.network.api

import com.nanamare.mac.grab.network.response.GeocodeResponse
import com.nanamare.mac.grab.network.response.PlaceResponse
import com.nanamare.mac.grab.network.response.ReverseGeocodeResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface GeocodeAPI {
    @GET("geocode/json")
    fun getLocationUseAddress(@Query("address") address: String): Single<GeocodeResponse>

    @GET("geocode/json")
    fun getLocationUseLatLng(@Query("latlng") latLng: String): Single<ReverseGeocodeResponse>

    // Get detail place more than  Geocode API
    @GET("place/textsearch/json")
    fun getPlace(@Query("query") address: String) : Single<PlaceResponse>
}