package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database

import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.Measurement
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MeasurementUseCaseTest {

    private val measurementRepository = mockk<MeasurementRepository>()
    private lateinit var measurementUseCase: MeasurementUseCase

    private val measurementList: List<Measurement> by lazy {

        val measurement1 = Measurement(1, measured = 1.1)
        val measurement2 = Measurement(2, measured = 2.2)
        val measurement3 = Measurement(3, measured = 3.3)

        listOf(measurement1, measurement2, measurement3)
    }

    @Test
    fun `given data returned from, should return data`() = runBlockingTest {

        // GIVEN
        coEvery { measurementRepository.getMeasurementsAsync() } returns measurementList

        // WHEN
        val expected = measurementUseCase.getMeasurementsAsync()

        // THEN
        Truth.assertThat(expected).containsExactlyElementsIn(measurementList)
        coVerify(exactly = 1) { measurementRepository.getMeasurementsAsync() }
    }

    @BeforeEach
    fun setUp() {
        measurementUseCase = MeasurementUseCase(measurementRepository)
    }

    @AfterEach
    fun tearDown() {
        clearMocks(measurementRepository)
    }
}