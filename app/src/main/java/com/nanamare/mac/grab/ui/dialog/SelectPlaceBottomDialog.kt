package com.nanamare.mac.grab.ui.dialog

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.nanamare.mac.grab.R
import com.nanamare.mac.grab.base.ui.BaseBottomSheetDialogFragment
import com.nanamare.mac.grab.databinding.SelectPlaceBottomSheetDialogBinding
import com.nanamare.mac.grab.network.NetworkState
import com.nanamare.mac.grab.vm.MapViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SelectPlaceBottomDialog :
    BaseBottomSheetDialogFragment<SelectPlaceBottomSheetDialogBinding>(R.layout.select_place_bottom_sheet_dialog) {

    private val mapViewModel by sharedViewModel<MapViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            mapVM = mapViewModel
            mapViewModel.liveReverseGeocodeState.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is NetworkState.Init -> hideLoadingPopup()
                    is NetworkState.Loading -> showLoadingPopup()
                    is NetworkState.Success -> {
                        if (!it.item.results.isNullOrEmpty()) {
                            // 비어 있지 않으면 가장 먼저 것으로 넣어줌
                            result = it.item.results[0]
                            mapViewModel.setPlaceType(it.item.results[0]?.types)

                        }
                    }
                    is NetworkState.Error -> Log.e(TAG, it.throwable.toString())
                }
            })
        }
    }

    fun searchLocation(latLng: LatLng) {
        mapViewModel.getLocationUseLatLng("${latLng.latitude},${latLng.longitude}")
    }

    companion object {
        fun getInstance() = SelectPlaceBottomDialog()

        @JvmStatic
        val TAG: String = SelectPlaceBottomDialog::class.java.simpleName
    }
}