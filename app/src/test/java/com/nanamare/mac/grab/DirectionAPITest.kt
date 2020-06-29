package com.nanamare.mac.grab

import com.nanamare.mac.grab.data.source.DirectionRepository
import com.nanamare.mac.grab.di.*
import com.nanamare.mac.grab.rule.SchedulersRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.mockito.Mock
import org.mockito.MockitoAnnotations


class DirectionAPITest : AutoCloseKoinTest() {

    @Rule
    @JvmField
    val testSchedulerRule = SchedulersRule()

    @Mock
    private lateinit var applicationContext: CarApplication

    private val directionRepository by inject<DirectionRepository>()

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
    fun `드라이브 모드로 주행 정보 검색`() {
        // 미국 -> 멕시코 이동
        directionRepository.getDriveCourse("37.09024,-95.712891", "23.634501,-102.552784")
            .subscribe({
                Assert.assertNotNull(it)
            }, {
                Assert.fail(it.toString())
            })
    }

}