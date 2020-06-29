package com.nanamare.mac.grab.data.source

import com.nanamare.mac.grab.data.source.local.RecentSearchDataSourceImpl
import com.nanamare.mac.grab.data.source.vo.LocationVO

class LocalSearchPlaceRepository(
    private val recentSearchDataSourceImpl: RecentSearchDataSourceImpl
) : LocalSearchPlaceDataSource {

    override fun saveLocationVOList(locationVO: LocationVO, callBack: () -> Unit) =
        recentSearchDataSourceImpl.saveLocationVOList(locationVO, callBack)

    override fun loadLocationVOList(): List<LocationVO>? =
        recentSearchDataSourceImpl.loadLocationVOList()

    override fun clearLocationVOList(callBack: () -> Unit) =
        recentSearchDataSourceImpl.clearLocationVOList(callBack)
}