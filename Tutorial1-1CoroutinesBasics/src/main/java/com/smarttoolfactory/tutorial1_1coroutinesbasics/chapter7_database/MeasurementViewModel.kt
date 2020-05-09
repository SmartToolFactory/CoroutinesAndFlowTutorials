package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database

import android.app.Application
import androidx.lifecycle.*
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.DatabaseFactory
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.Measurement
import kotlinx.coroutines.launch

class MeasurementViewModel(private val measurementsUseCase: MeasurementsUseCase) : ViewModel() {

    private val _text = MutableLiveData<String>()
     val measurement: LiveData<String>
        get() = _text

    private val _insertResult = MutableLiveData<String>()
    val insertResult: LiveData<String>
        get() = _insertResult


    fun insertMeasurementAsync(text: String) {

        viewModelScope.launch {

            val measurement = try {
                val doubleValue = text.toDoubleOrNull() ?: 0.0
                val measurement = Measurement(measured = doubleValue)

                // Add Measurement to Database
                val measurementId = measurementsUseCase.insertMeasurementAsync(measurement)
                // Set text of successful
                _insertResult.value = "Inserted with id #$measurementId"


            } catch (e: Exception) {
                _insertResult.value = "Insert failed with exception: ${e.message}"
            }

        }

    }

    fun insertMeasurement(text: String) {

        val measurement = try {
            val doubleValue = text.toDoubleOrNull() ?: 0.0
            val measurement = Measurement(measured = doubleValue)

            // Add Measurement to Database
            val measurementId = measurementsUseCase.insertMeasurement(measurement)
            // Set text of successful
            _insertResult.value = "Inserted with id #${measurementId.value}"


        } catch (e: Exception) {
            _insertResult.value = "Insert failed with exception: ${e.message}"
        }
    }


    fun getMeasurements() {

    }

    fun getMeasurementsWithSuspend() {

    }


}


class MeasurementModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val measurementDao = DatabaseFactory.getMeasurementDatabase(application).measurementDao()

        val measurementUseCase = MeasurementsUseCase(MeasurementRepository(measurementDao))

        return MeasurementViewModel(measurementUseCase) as T
    }

}