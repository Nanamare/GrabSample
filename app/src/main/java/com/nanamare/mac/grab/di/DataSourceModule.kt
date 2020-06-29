package com.nanamare.mac.grab.di

import com.nanamare.mac.grab.data.source.DirectionRepository
import com.nanamare.mac.grab.data.source.GeocodeRepository
import com.nanamare.mac.grab.data.source.LocalSearchPlaceRepository
import com.nanamare.mac.grab.data.source.local.RecentSearchDataSourceImpl
import com.nanamare.mac.grab.data.source.remote.RemoteDirectionDataSourceImpl
import com.nanamare.mac.grab.data.source.remote.RemoteGeocodeDataSourceImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataSourceModel = module {
    single { RemoteGeocodeDataSourceImpl(get(named(DI_API_NO_AUTH))) }
    single { GeocodeRepository(get()) }

    single { RemoteDirectionDataSourceImpl(get(named(DI_API_NO_AUTH))) }
    single { DirectionRepository(get()) }

    single { RecentSearchDataSourceImpl(get(named(DI_PREF_UTILS))) }
    single { LocalSearchPlaceRepository(get()) }
}