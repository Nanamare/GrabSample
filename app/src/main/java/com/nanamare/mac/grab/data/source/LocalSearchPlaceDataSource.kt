package com.nanamare.mac.grab.data.source

import com.nanamare.mac.grab.data.source.vo.LocationVO


interface LocalSearchPlaceDataSource {
    fun saveLocationVOList(locationVO: LocationVO, callBack: () -> Unit)
    fun loadLocationVOList(): List<LocationVO>?
    fun clearLocationVOList(callBack: () -> Unit)
}