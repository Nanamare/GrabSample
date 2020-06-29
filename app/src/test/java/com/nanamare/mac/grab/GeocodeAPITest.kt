package com.nanamare.mac.grab

import com.nanamare.mac.grab.data.source.GeocodeRepository
import com.nanamare.mac.grab.di.*
import com.nanamare.mac.grab.rule.SchedulersRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.mockito.Mock
import org.mockito.MockitoAnnotations


@RunWith(JUnit4::class)
class GeocodeAPITest : AutoCloseKoinTest() {

    @Rule
    @JvmField
    val testSchedulerRule = SchedulersRule()

    @Mock
    private lateinit var applicationContext: CarApplication

    private val geocodeRepository by inject<GeocodeRepository>()

    @Before
    fun `초기화 및 모듈 주입`() {
        MockitoAnnotations.initMocks(this)

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(applicationContext)
            modules(
                listOf(
                    networkModule,
                    dataSourceModel,
                    viewModelModule,
                    apiModule,
                    prefUtilsModule
                )
            )
        }
    }

    @Test
    fun `Search - geocode`() {
        geocodeRepository.getLocationUseAddress("london")
            .subscribe({
                Assert.assertNotNull(it)
            }, {
                Assert.fail(it.toString())
            })
    }

    @Test
    fun `Search - reverseGeocode`() {
        geocodeRepository.getLocationUseLatLng("51.509865,-0.118092")
            .subscribe({
                Assert.assertNotNull(it)
            }, {
                Assert.fail(it.toString())
            })
    }

    @Test
    fun `Search place`() {
        geocodeRepository.getPlace("harvard")
            .subscribe({
                Assert.assertNotNull(it)
            }, {
                Assert.fail(it.toString())
            })
    }


}