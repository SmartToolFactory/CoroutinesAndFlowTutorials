package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database

import androidx.lifecycle.LiveData
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.Measurement
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.MeasurementDao

class MeasurementRepository(private val measurementDao: MeasurementDao) {


    suspend fun insertMeasurementAsync(measurement: Measurement): Long {
        return measurementDao.insertAsync(measurement)
    }

    suspend fun getMeasurementsAsync(): List<Measurement> {
        return measurementDao.getMeasurementsAsync()
    }

    fun getMeasurements(): LiveData<List<Measurement>> {
        return measurementDao.getMeasurements()
    }

}