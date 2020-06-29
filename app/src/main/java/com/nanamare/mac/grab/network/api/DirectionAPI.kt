package com.nanamare.mac.grab.network.api

import com.nanamare.mac.grab.network.response.DirectionResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionAPI {
    // default is driving mode
    @GET("directions/json")
    fun getDrivingCourse(
        @Query("origin") origin: String,
        @Query("destination") destination: String
    ) : Single<DirectionResponse>
}