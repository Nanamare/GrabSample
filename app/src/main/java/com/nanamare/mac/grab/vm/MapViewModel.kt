package com.nanamare.mac.grab.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.nanamare.mac.grab.base.ui.BaseViewModel
import com.nanamare.mac.grab.data.source.DirectionRepository
import com.nanamare.mac.grab.data.source.GeocodeRepository
import com.nanamare.mac.grab.data.source.enums.SearchType
import com.nanamare.mac.grab.data.source.vo.DirectionVO
import com.nanamare.mac.grab.data.source.vo.LocationVO
import com.nanamare.mac.grab.ext.nonNull
import com.nanamare.mac.grab.ext.toParam
import com.nanamare.mac.grab.network.NetworkState
import com.nanamare.mac.grab.network.response.DirectionResponse
import com.nanamare.mac.grab.network.response.GeocodeResponse
import com.nanamare.mac.grab.network.response.ReverseGeocodeResponse
import com.nanamare.mac.grab.network.response.ReverseGeocodeResponse.Result.Geometry.Location
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * GeocodeAPITest, DirectionAPITest 통과
 */
class MapViewModel(
    private val geocodeRepository: GeocodeRepository,
    private val directionRepository: DirectionRepository
) : BaseViewModel() {

    val liveDarkMode = MutableLiveData<Boolean>().nonNull().apply { value = false }

    private val _liveSelectPlaceType = MutableLiveData<String>()
    val liveSelectPlaceType: LiveData<String> get() = _liveSelectPlaceType

    val liveAllArriveTime = MutableLiveData<String>()

    var zoom: Float = 16f

    var observableDrive: Observable<Long> = Observable.interval(500, 500, TimeUnit.MILLISECONDS)

    var isAvailabilityLocation = false

    var carMarker: Marker? = null
    var carCurrLatLng: LatLng? = null
    var carPreviousLatLng: LatLng? = null

    var currLatLng: LatLng? = null

    private var _liveSearchType = MutableLiveData<SearchType>()
    val liveSearchType: LiveData<SearchType> get() = _liveSearchType

    private var _liveIsDrivingPossible = MutableLiveData<Boolean>().apply { value = false }
    val liveIsDrivingStarted get() = _liveIsDrivingPossible

    private var _liveDirectionVO = MutableLiveData<List<DirectionVO>>()
    val liveDirectionVO: LiveData<List<DirectionVO>> get() = _liveDirectionVO

    private var _liveIsEnabledDriving =
        MutableLiveData<Boolean>().nonNull().apply { value = false }
    val liveIsEnabledDriving: LiveData<Boolean> get() = _liveIsEnabledDriving

    private var _liveStartLocationVO = MutableLiveData<LocationVO>()
    val liveStartLocationVO: LiveData<LocationVO> get() = _liveStartLocationVO

    private var _liveDestinationLocationVO = MutableLiveData<LocationVO>()
    val liveDestinationLocationVO: LiveData<LocationVO> get() = _liveDestinationLocationVO

    private val _liveGeocodeState =
        MutableLiveData<NetworkState<GeocodeResponse>>().apply { value = NetworkState.init() }
    val liveGeocodeState: LiveData<NetworkState<GeocodeResponse>>
        get() = _liveGeocodeState

    private val _liveReverseGeocodeState =
        MutableLiveData<NetworkState<ReverseGeocodeResponse>>().apply {
            value = NetworkState.init()
        }
    val liveReverseGeocodeState: LiveData<NetworkState<ReverseGeocodeResponse>>
        get() = _liveReverseGeocodeState

    private val _liveDirectionState =
        MutableLiveData<NetworkState<DirectionResponse>>().apply { value = NetworkState.init() }
    val liveDirectionState: LiveData<NetworkState<DirectionResponse>>
        get() = _liveDirectionState

    fun getLocationUseAddress(address: String) {
        compositeDisposable.add(
            geocodeRepository.getLocationUseAddress(address)
                .doOnSubscribe { _liveGeocodeState.value = NetworkState.loading() }
                .doOnTerminate { _liveGeocodeState.value = NetworkState.init() }
                .subscribe({
                    _liveGeocodeState.value = NetworkState.success(it)
                }, {
                    _liveGeocodeState.value = NetworkState.error(it)
                })
        )
    }

    fun getLocationUseLatLng(latLng: String) {
        compositeDisposable.add(
            geocodeRepository.getLocationUseLatLng(latLng)
                .doOnSubscribe { _liveReverseGeocodeState.value = NetworkState.loading() }
                .doOnTerminate { _liveReverseGeocodeState.value = NetworkState.init() }
                .subscribe({
                    _liveReverseGeocodeState.value = NetworkState.success(it)
                }, {
                    _liveReverseGeocodeState.value = NetworkState.error(it)
                })
        )

    }

    fun setDeparture(location: Location?, addressName: String?) {
        location?.let {
            _liveStartLocationVO.value = LocationVO(LatLng(it.lat, it.lng), addressName)
        }
    }

    fun setDeparture(locationVO: LocationVO?) {
        locationVO?.let {
            _liveStartLocationVO.value = it
        }
    }

    fun setDestination(location: Location?, addressName: String?) {
        location?.let {
            _liveDestinationLocationVO.value = LocationVO(LatLng(it.lat, it.lng), addressName)
        }
    }

    fun setDestination(locationVO: LocationVO?) {
        locationVO?.let {
            _liveDestinationLocationVO.value = locationVO
        }
    }

    fun checkToReadyDriving(
        departureLocationVO: LocationVO?,
        destinationLocationVO: LocationVO?
    ) {
        _liveIsEnabledDriving.value = departureLocationVO != null && destinationLocationVO != null
    }

    // null 일 경우 "null" 반환
    fun startDriving() {
        compositeDisposable.add(directionRepository.getDriveCourse(
            liveStartLocationVO.value?.latLng?.toParam().toString(),
            liveDestinationLocationVO.value?.latLng?.toParam().toString()
        ).doOnSubscribe { _liveDirectionState.value = NetworkState.loading() }
            .doOnTerminate { _liveDirectionState.value = NetworkState.init() }
            .subscribe({
                _liveDirectionState.value = NetworkState.success(it)
            }, {
                _liveDirectionState.value = NetworkState.error(it)
            })
        )
    }

    fun stopDriving() {
        _liveIsEnabledDriving.value = true
        _liveIsDrivingPossible.value = false
    }

    fun saveDirectionInfo(directionsVO: List<DirectionVO>) {
        _liveDirectionVO.value = directionsVO
    }

    fun onSearchClick(searchType: SearchType) {
        _liveSearchType.value = searchType
    }

    fun onZoomInClick() {
        zoom += 1
    }

    fun onZoomOutClick() {
        zoom -= 1
    }

    fun setPlaceType(placeType: List<String?>?) {
        val place = StringBuilder()
        placeType?.forEach {
            place.append(it)
        }
        _liveSelectPlaceType.value = place.toString()
    }

    fun changeUiMode() {
        liveDarkMode.value = !liveDarkMode.value!!
    }


}