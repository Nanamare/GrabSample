package com.nanamare.mac.grab.data.source

import com.nanamare.mac.grab.data.source.remote.RemoteDirectionDataSourceImpl
import com.nanamare.mac.grab.network.response.DirectionResponse
import io.reactivex.Single

class DirectionRepository(private val remoteDirectionDataSourceImpl: RemoteDirectionDataSourceImpl) :
    DirectionDataSource {
    override fun getDriveCourse(origin: String, destination: String): Single<DirectionResponse> =
        remoteDirectionDataSourceImpl.getDriveCourse(origin, destination)
}