package com.nanamare.mac.grab.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nanamare.mac.grab.base.ui.BaseViewModel
import com.nanamare.mac.grab.data.source.GeocodeRepository
import com.nanamare.mac.grab.data.source.LocalSearchPlaceRepository
import com.nanamare.mac.grab.data.source.SearchPlaceDataSourceFactory
import com.nanamare.mac.grab.data.source.vo.LocationVO
import com.nanamare.mac.grab.network.NetworkState
import com.nanamare.mac.grab.network.response.PlaceResponse

class SearchLocationViewModel(
    private val geocodeRepository: GeocodeRepository,
    private val localSearchPlaceRepository: LocalSearchPlaceRepository
) : BaseViewModel() {

    var liveSearchItems =
        MutableLiveData<LiveData<PagedList<PlaceResponse.Result>>>()

    val liveLocalLocations = MutableLiveData<List<LocationVO>>()

    private val _livePlaceState = MutableLiveData<NetworkState<PlaceResponse>>()
    val livePlaceState: LiveData<NetworkState<PlaceResponse>> get() = _livePlaceState

    val liveKeyword = MutableLiveData<String>()

    private val _liveIsResultZero = MutableLiveData<Boolean>()
    val liveIsResultZero: LiveData<Boolean> get() = _liveIsResultZero

    fun onSearchClick() {
        _liveIsResultZero.value = false
        liveSearchItems.value = LivePagedListBuilder(
            SearchPlaceDataSourceFactory(
                geocodeRepository,
                liveKeyword.value ?: error("empty keyword"),
                _livePlaceState,
                compositeDisposable
            ), 10
        ).setBoundaryCallback(object : PagedList.BoundaryCallback<PlaceResponse.Result>() {
            override fun onZeroItemsLoaded() {
                super.onZeroItemsLoaded()
                _liveIsResultZero.value = true
            }
        }).build()
    }

    fun saveLocations(locationVO: LocationVO) {
        localSearchPlaceRepository.saveLocationVOList(locationVO) {
            // saved callback
        }
    }

    fun loadLocalLocations() {
        liveLocalLocations.value = localSearchPlaceRepository.loadLocationVOList()?.reversed()
    }

    fun clearLocalLocations() {
        localSearchPlaceRepository.clearLocationVOList {
            liveLocalLocations.value = emptyList()
        }
    }

}
