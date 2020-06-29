package com.nanamare.mac.grab.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.nanamare.mac.grab.R
import com.nanamare.mac.grab.base.ui.BaseActivity
import com.nanamare.mac.grab.data.source.enums.SearchType
import com.nanamare.mac.grab.data.source.vo.DirectionVO
import com.nanamare.mac.grab.databinding.MainActivityBinding
import com.nanamare.mac.grab.ext.dip
import com.nanamare.mac.grab.ext.enableTransparentStatusBar
import com.nanamare.mac.grab.ext.nonNull
import com.nanamare.mac.grab.network.NetworkState
import com.nanamare.mac.grab.network.response.DirectionResponse
import com.nanamare.mac.grab.ui.dialog.SearchPlaceDialog
import com.nanamare.mac.grab.ui.dialog.SelectPlaceBottomDialog
import com.nanamare.mac.grab.utils.PolylineEncoding
import com.nanamare.mac.grab.utils.SimpleAnimator
import com.nanamare.mac.grab.utils.getCarBitmap
import com.nanamare.mac.grab.vm.MapViewModel
import com.tedpark.tedpermission.rx2.TedRx2Permission
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainActivityBinding>(R.layout.main_activity), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMapLongClickListener {

    private lateinit var googleMap: GoogleMap

    private lateinit var locationCallback: LocationCallback
    private val fusedLocationProviderClient by lazy { FusedLocationProviderClient(this) }

    private val mapViewModel by viewModel<MapViewModel>()

    private val selectBottomDialog by lazy { SelectPlaceBottomDialog.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initMap()
        binding.mapVM = mapViewModel
        mapViewModel.run {
            liveDarkMode.nonNull().observe(this@MainActivity, Observer {
                if (!::googleMap.isInitialized) return@Observer
                if (!::googleMap.isInitialized) return@Observer
                if (it) {
                    setDarkMode(
                        googleMap,
                        MapStyleOptions.loadRawResourceStyle(
                            this@MainActivity,
                            R.raw.google_map_dark_mode_style
                        )
                    )
                } else {
                    setDarkMode(
                        googleMap,
                        null
                    )
                }
            })

            liveGeocodeState.observe(this@MainActivity, Observer {
                when (it) {
                    is NetworkState.Init -> hideLoadingPopup()
                    is NetworkState.Loading -> showLoadingPopup()
                    is NetworkState.Success -> it.item.toString()
                    is NetworkState.Error -> Log.e(TAG, it.throwable.toString())
                }
            })

            liveStartLocationVO.observe(this@MainActivity, Observer {
                if (selectBottomDialog.isAdded) {
                    selectBottomDialog.onCloseClick()
                }
                showToast(getString(R.string.toast_complete_departure))
                checkToReadyDriving(liveStartLocationVO.value, liveDestinationLocationVO.value)
            })

            liveDestinationLocationVO.observe(this@MainActivity, Observer {
                if (selectBottomDialog.isAdded) {
                    selectBottomDialog.onCloseClick()
                }
                showToast(getString(R.string.toast_complete_destination))
                checkToReadyDriving(liveStartLocationVO.value, liveDestinationLocationVO.value)
            })

            liveDirectionState.observe(this@MainActivity, Observer {
                if (!::googleMap.isInitialized) return@Observer
                when (it) {
                    is NetworkState.Init -> hideLoadingPopup()
                    is NetworkState.Loading -> showLoadingPopup()
                    is NetworkState.Success -> {
                        val directionVO: MutableList<DirectionVO> = getDirectionsVO(it.item.routes)
                        saveDirectionInfo(directionVO)
                        val polylines = directionVO.flatMap { it.latLng }
                        if (polylines.isNotEmpty()) {
                            drawOverViewPolyline(polylines)
                            addStartEndMarker(polylines[0], polylines[polylines.size - 1])
                            startDrivingAnim(liveDirectionVO.value, polylines[0])
                        } else {
                            showToast(getString(R.string.toast_no_driving_route))
                        }
                    }
                    is NetworkState.Error -> Log.e(TAG, it.throwable.toString())
                }
            })

            liveIsDrivingStarted.nonNull().observe(this@MainActivity, Observer {
                if (!it) {
                    clearMap()
                }
            })

            liveSearchType.observe(this@MainActivity, Observer {
                it?.let {
                    val searchType: SearchType = when (it) {
                        SearchType.SOURCE -> SearchType.SOURCE
                        SearchType.DESTINATION -> SearchType.DESTINATION
                    }
                    SearchPlaceDialog.getInstance(searchType,
                        onPlaceClickListener = { locationVO ->
                            when (searchType) {
                                SearchType.SOURCE -> mapViewModel.setDeparture(locationVO)
                                SearchType.DESTINATION -> mapViewModel.setDestination(locationVO)
                            }
                            addOneMarker(locationVO.latLng)
                            moveCamera(locationVO.latLng)
                        })
                        .show(supportFragmentManager, "")
                }
            })

        }

    }

    private fun addStartEndMarker(departure: LatLng, destination: LatLng): Pair<Marker, Marker> {
        return googleMap.addMarker(MarkerOptions().position(departure)).apply {
            title = getString(R.string.departure)
            snippet = getString(R.string.marker_snippet_departure)
            setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        } to googleMap.addMarker(MarkerOptions().position(destination)).apply {
            title = getString(R.string.destination)
            snippet = getString(R.string.marker_snippet_destination)
            setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        }
    }

    private fun startDrivingAnim(
        directionsVO: List<DirectionVO>?,
        latLng: LatLng
    ) {
        directionsVO?.let {
            if (it.isNotEmpty()) {
                // 첫번째 위치로 미리 갱신
                mapViewModel.carCurrLatLng = latLng
                mapViewModel.carMarker = addCarMarker(latLng)
            }
            updateCarMarker(it)
        } ?: run {
            mapViewModel.liveIsDrivingStarted.value = false
        }
    }

    private fun updateCarMarker(directionsVO: List<DirectionVO>) {
        val allLatLngs = directionsVO.flatMap { it.latLng }
        val allLatLngLength = allLatLngs.size
        if (allLatLngLength == 1) {
            if (mapViewModel.carCurrLatLng == allLatLngs[0]) {
                showToast(getString(R.string.toast_no_route_same_departure_destination))
                return
            }
        }
        mapViewModel.liveIsDrivingStarted.value = true
        compositeDisposable.add(
            mapViewModel.observableDrive
                .take(allLatLngLength.toLong())
                .takeWhile { mapViewModel.liveIsDrivingStarted.value == true }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { cnt ->
                    if (mapViewModel.carMarker != null) {
                        if (mapViewModel.carPreviousLatLng == null) {
                            mapViewModel.carPreviousLatLng = mapViewModel.carCurrLatLng
                            moveCamera(mapViewModel.carCurrLatLng, mapViewModel.zoom)
                        } else {
                            SimpleAnimator.carStartAnim().apply {
                                mapViewModel.carPreviousLatLng = mapViewModel.carCurrLatLng
                                mapViewModel.carCurrLatLng = allLatLngs[cnt.toInt()]
                                addUpdateListener { animator ->
                                    if (mapViewModel.carCurrLatLng != null && mapViewModel.carPreviousLatLng != null) {
                                        val factor = animator.animatedFraction.toDouble()
                                        val nextLocation = LatLng(
                                            factor * mapViewModel.carCurrLatLng!!.latitude + (1 - factor) * mapViewModel.carPreviousLatLng!!.latitude,
                                            factor * mapViewModel.carCurrLatLng!!.longitude + (1 - factor) * mapViewModel.carPreviousLatLng!!.longitude
                                        )
                                        moveCamera(nextLocation, mapViewModel.zoom)
                                        mapViewModel.carMarker?.position = nextLocation
                                    }
                                }

                                if (cnt.toInt() == allLatLngLength - 1) {
                                    // 드라이브 끝났을 떄는 초기화 및 마지막 애니메이션까지 그려줌
                                    Handler().postDelayed({
                                        mapViewModel.stopDriving()
                                        showToast(getString(R.string.toast_finish_driving))
                                    }, 500)
                                }
                            }.start()
                        }
                    }
                }
        )
    }

    private fun clearMap() {
        mapViewModel.liveAllArriveTime.value = ""
        mapViewModel.carPreviousLatLng = null
        if (::googleMap.isInitialized) {
            googleMap.clear()
        }
    }

    private fun addCarMarker(latLng: LatLng): Marker? {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
            getCarBitmap(
                resources,
                R.drawable.icon_car_maker, dip(28), dip(28)
            )
        )
        return googleMap.addMarker(MarkerOptions().position(latLng).icon(bitmapDescriptor))
    }

    private fun drawOverViewPolyline(routes: List<LatLng>) {
        googleMap.clear()
        googleMap.addPolyline(PolylineOptions().addAll(routes)).apply {
            color = ResourcesCompat.getColor(resources, R.color.point_E47B75, theme)
            isClickable = true
        }
    }

    private fun getDirectionsVO(routes: List<DirectionResponse.Route?>?): MutableList<DirectionVO> {
        val directionVO: MutableList<DirectionVO> = mutableListOf()
        routes?.forEach {
            it?.legs?.forEach {
                // direction api 기본값 legs 는 하나
                mapViewModel.liveAllArriveTime.value = it?.duration?.text
                it?.steps?.forEachIndexed { idx, it ->
                    it?.let {
                        val latLngs = PolylineEncoding.decode(it.polyline.points)
                        directionVO.add(
                            DirectionVO(
                                latLngs,
                                it.duration?.text,
                                it.distance?.text,
                                idx
                            )
                        )
                    }
                }
            }
        }
        return directionVO
    }

    private fun checkPermissions() {
        compositeDisposable.add(TedRx2Permission.with(this)
            .setDeniedCloseButtonText(getString(R.string.cancel))
            .setGotoSettingButtonText(getString(R.string.setting))
            .setDeniedTitle(getString(R.string.request_gps_permission))
            .setDeniedMessage(getString(R.string.desc_gps_permission))
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .request()
            .subscribe({
                if (it.isGranted) {
                    setLocationListener()
                    getCurrentLocation()
                } else {
                    showToast(getString(R.string.desc_gps_permission))
                }
            }) {

            })
    }

    @SuppressLint("MissingPermission")
    private fun setLocationListener() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                mapViewModel.currLatLng.let { latLng ->
                    locationResult?.let { locationResult ->
                        for (location in locationResult.locations) {
                            Log.d(
                                TAG,
                                "location lat : ${location.latitude} lng : ${location.longitude}"
                            )
                            if (latLng == null) {
                                mapViewModel.currLatLng =
                                    LatLng(location.latitude, location.longitude)
                                moveCamera(mapViewModel.currLatLng, mapViewModel.zoom)
                                animateCamera(mapViewModel.currLatLng)
                                // 첫 1회만 찾아오고, 사용하지 않으니 제거
                                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                            }
                        }
                    }
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
                super.onLocationAvailability(locationAvailability)
                mapViewModel.isAvailabilityLocation =
                    locationAvailability?.isLocationAvailable ?: false
            }
        }

        // 정확한 시간은 setFastestInterval, 그보다 빨리 찾아진 경우에도 사용하려면 setFastestInterval 사용
        val locationRequest = LocationRequest().setInterval(10000).setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    private fun animateCamera(latLng: LatLng?) {
        latLng?.let {
            val cameraPosition = CameraPosition.Builder().target(it).zoom(16f).build()
            googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    cameraPosition
                )
            )
        } ?: throw NullPointerException(getString(R.string.error_no_location))
    }

    /**
     * Direct Move
     */
    private fun moveCamera(latLng: LatLng?, zoom: Float) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    private fun moveCamera(latLng: LatLng?) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        mapViewModel.run {
            googleMap.setOnMyLocationButtonClickListener(this@MainActivity)
            googleMap.setOnMyLocationClickListener(this@MainActivity)
            googleMap.setOnMapLongClickListener(this@MainActivity)
            googleMap.isMyLocationEnabled = true
        }
    }

    private fun initView() {
        window.enableTransparentStatusBar()
    }

    private fun initMap() {
        clearMap()
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_map) as SupportMapFragment
        mapFragment.getMapAsync(this@MainActivity)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            this.googleMap = it
            googleMap.setPadding(dip(20), dip(20), dip(20), dip(20))
            checkPermissions()
        }
    }

    private fun setDarkMode(
        googleMap: GoogleMap,
        loadRawResourceStyle: MapStyleOptions?
    ) {
        try {
            googleMap.setMapStyle(loadRawResourceStyle)
        } catch (e: Resources.NotFoundException) {
            showToast(getString(R.string.error_failed_parsing_google_map_dark_mode_style))
        }
    }

    // 현재 위치 버튼 리스너
    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    // 현재 위치 클릭 마커 콜백
    override fun onMyLocationClick(location: Location) {
        Log.d(TAG, "$location")
    }

    // 안내중에는 사용 불가능
    override fun onMapLongClick(latLng: LatLng?) {
        if (mapViewModel.liveIsDrivingStarted.value == true) return
        Log.d(TAG, latLng.toString())
        latLng?.let {
            addOneMarker(it)
            selectBottomDialog.show(supportFragmentManager, selectBottomDialog.tag)
            selectBottomDialog.searchLocation(it)
        } ?: throw NullPointerException(getString(R.string.error_no_location))
    }

    private fun addOneMarker(latLng: LatLng) {
        googleMap.clear()
        googleMap.addMarker(MarkerOptions().position(latLng).flat(true))
    }

    companion object {
        @JvmStatic
        val TAG: String = MainActivity::class.java.simpleName
    }
}