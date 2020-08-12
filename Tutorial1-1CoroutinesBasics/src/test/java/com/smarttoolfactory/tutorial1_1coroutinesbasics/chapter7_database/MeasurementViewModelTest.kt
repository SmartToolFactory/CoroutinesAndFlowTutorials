package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.Measurement
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.getOrAwaitValue
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.rules.TestCoroutineRule
import io.mockk.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class MeasurementViewModelTest {

    /**
     * Not using this causes java.lang.RuntimeException: Method getMainLooper in android.os.Looper
     * not mocked when <code>this.observeForever(observer)</code> is called
     */
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    // 🔥 This mock is relaxed because in init block of ViewModel it's method is called, and not required for the tests
    // And throws exception even it's set with every{}
    private val measurementUseCase = mockk<MeasurementUseCase>(relaxed = true)
    private lateinit var measurementViewModel: MeasurementViewModel

    private val measurementList: List<Measurement> by lazy {

        val measurement1 = Measurement(1, measured = 1.1)
        val measurement2 = Measurement(2, measured = 2.2)
        val measurement3 = Measurement(3, measured = 3.3)

        listOf(measurement1, measurement2, measurement3)
    }


    @Test
    fun `given data returned from, should return data`() = runBlockingTest {

        // GIVEN
        coEvery { measurementUseCase.getMeasurementsAsync() } returns measurementList
        val actual =
            "Total count: ${measurementList.size}, Measurement last value: ${measurementList.last()}"

        // WHEN
        measurementViewModel.getMeasurementsWithSuspend()

        // THEN
        val expected = measurementViewModel.resultWithSuspend.getOrAwaitValue()
        Truth.assertThat(expected).isEqualTo(actual)
        coVerify(exactly = 1) { measurementUseCase.getMeasurementsAsync() }
    }

    @Before
    fun setUp() {
        measurementViewModel = MeasurementViewModel(measurementUseCase)
    }

    @After
    fun tearDown() {
        clearMocks(measurementUseCase)
    }
}