package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database

import android.app.Application
import androidx.lifecycle.*
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.DatabaseFactory
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.Measurement
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MeasurementViewModel(private val measurementsUseCase: MeasurementsUseCase) : ViewModel() {

    /**
     * Scope to test with context with IO thread
     */
    private val myCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * 2-way binding for user input
     */
    val input = MutableLiveData<String>()

    /**
     * Result of insert action to display id of row
     */
    private val _insertResult = MutableLiveData<String>()
    val insertResult: LiveData<String>
        get() = _insertResult

    private val _resultWithSuspend = MutableLiveData<String>()
    val resultWithSuspend: LiveData<String>
        get() = _resultWithSuspend

    /**
     * Listens changes in database and reflects on UI on a change in db
     */
    val resultWithLiveData: LiveData<String>

    /**
     * Query event to get values from database
     */
    private val queryWithLiveDataBuilder = MutableLiveData<Event<Boolean>>()

    var resultWithLiveDataBuilder = queryWithLiveDataBuilder.switchMap {
        setLiveDataBuilder()
    }

    init {

        // 🔥🔥 Updates whenever database is updated
        resultWithLiveData = measurementsUseCase.getMeasurements()
            .map { measurements ->
                try {
                    if (measurements.isNullOrEmpty()) {
                        "Transformations.map(): Empty"
                    } else {
                        "Transformations.map(): Total count: ${measurements.size}, las value: ${measurements.last()}"
                    }
                } catch (e: Exception) {
                    "Transformations.map( exception: ${e.message}"
                }
            }
    }

    /**
     * Creates a liveDataBuilder that returns a String
     */
    private fun setLiveDataBuilder(): LiveData<String> {

        return liveData {

            println("😎 liveDataBuilder() scope: $this, thread: ${Thread.currentThread().name}")

            try {
                val measurements = measurementsUseCase.getMeasurementsAsync()
                emit("Total count: ${measurements.size}, Measurement first: ${measurements.last()}")

            } catch (e: Exception) {
                emit("getMeasurementsWithSuspend() error: ${e.message}")
            }
        }

    }

    fun insertMeasurementWithSuspend(text: String) {

        viewModelScope.launch {

            try {

                val doubleValue = text.toDoubleOrNull() ?: 0.0
                val measurement = Measurement(measured = doubleValue)

                // Add Measurement to Database
                val measurementId = measurementsUseCase.insertMeasurementAsync(measurement)
                // Set text of successful
                _insertResult.value =
                    "Inserted with id #$measurementId, value: ${measurement.measured}"

            } catch (e: Exception) {
                _insertResult.value = "Insert failed with exception: ${e.message}"
            }
        }
    }

    /**
     * Gets measurements from database using suspend modifier and coroutines
     */
    fun getMeasurementsWithSuspend() {

        viewModelScope.launch {
            try {
                println("🙄 getMeasurementsWithSuspend() scope: $this, thread: ${Thread.currentThread().name}")

                val measurements = measurementsUseCase.getMeasurementsAsync()
                _resultWithSuspend.value =
                    "Total count: ${measurements.size}, Measurement last value: ${measurements.last()}"

            } catch (e: Exception) {
                _resultWithSuspend.value = "getMeasurementsWithSuspend() error: ${e.message}"
                println("getMeasurementsWithSuspend() error: ${e.message}")
            }
        }
    }

    fun getMeasurementWitLiveDataBuilder() {
        queryWithLiveDataBuilder.value = Event(true)
    }

}


class MeasurementViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val measurementDao = DatabaseFactory.getMeasurementDatabase(application).measurementDao()

        val measurementUseCase = MeasurementsUseCase(MeasurementRepository(measurementDao))

        return MeasurementViewModel(measurementUseCase) as T
    }

}