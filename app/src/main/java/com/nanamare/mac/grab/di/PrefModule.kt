package com.nanamare.mac.grab.di

import com.nanamare.mac.grab.utils.PrefUtils
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val DI_PREF_UTILS = "DI_PREF_UTILS"

val prefUtilsModule = module {
    single(named(DI_PREF_UTILS)) { PrefUtils(androidApplication()) }
}