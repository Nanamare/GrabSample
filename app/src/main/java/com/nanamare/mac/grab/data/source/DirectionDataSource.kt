package com.nanamare.mac.grab.data.source

import com.nanamare.mac.grab.network.response.DirectionResponse
import io.reactivex.Single

interface DirectionDataSource {
    fun getDriveCourse(origin: String, destination: String): Single<DirectionResponse>
}