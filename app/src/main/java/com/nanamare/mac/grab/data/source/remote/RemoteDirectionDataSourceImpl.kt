package com.nanamare.mac.grab.data.source.remote

import com.nanamare.mac.grab.data.source.DirectionDataSource
import com.nanamare.mac.grab.ext.networkDispatchToMainThread
import com.nanamare.mac.grab.network.api.DirectionAPI
import com.nanamare.mac.grab.network.response.DirectionResponse
import io.reactivex.Single

class RemoteDirectionDataSourceImpl(private val directionAPI: DirectionAPI) : DirectionDataSource {

    override fun getDriveCourse(origin: String, destination: String): Single<DirectionResponse> =
        directionAPI.getDrivingCourse(origin, destination).networkDispatchToMainThread()

}