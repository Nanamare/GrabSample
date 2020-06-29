package com.nanamare.mac.grab.di


import com.nanamare.mac.grab.vm.MapViewModel
import com.nanamare.mac.grab.vm.SearchLocationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MapViewModel(get(), get()) }
    viewModel { SearchLocationViewModel(get(), get()) }
}
