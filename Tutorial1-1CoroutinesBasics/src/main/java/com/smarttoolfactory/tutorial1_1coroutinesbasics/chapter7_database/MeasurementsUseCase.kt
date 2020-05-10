package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database

import androidx.lifecycle.LiveData
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.Measurement

class MeasurementsUseCase(private val measurementRepository: MeasurementRepository) {

    suspend fun insertMeasurementAsync(measurement: Measurement): Long {
        return measurementRepository.insertMeasurementAsync(measurement)
    }

    suspend fun getMeasurementsAsync(): List<Measurement> {
        return measurementRepository.getMeasurementsAsync()
    }

    fun getMeasurements(): LiveData<List<Measurement>> {
        return measurementRepository.getMeasurements()
    }

}