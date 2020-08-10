package com.smarttoolfactory.tutorial1_1coroutinesbasics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.Measurement
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.MeasurementDao
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.MeasurementDatabase
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MeasurementDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    private val measurementList: List<Measurement> by lazy {

        val measurement1 = Measurement(1, measured = 1.1)
        val measurement2 = Measurement(2, measured = 2.2)
        val measurement3 = Measurement(3, measured = 3.3)

        listOf(measurement1, measurement2, measurement3)
    }

    private lateinit var database: MeasurementDatabase

    /**
     * This is the SUT
     */
    private lateinit var measurementDao: MeasurementDao

    @Test
    fun shouldInsertSingleMeasurement() = runBlockingTest {

        // GIVEN
        val initialCount = measurementDao.getMeasurementCount()

        // WHEN
        val insertedId = measurementDao.insertAsync(measurementList.first())

        // THEN
        val measurementCount = measurementDao.getMeasurementCount()

        println("Initial count: $initialCount, insertId: $insertedId, measurementCount: $measurementCount")

        Truth.assertThat(initialCount).isEqualTo(0)
        Truth.assertThat(insertedId).isEqualTo(1)
        Truth.assertThat(measurementCount).isEqualTo(1)
    }

    @Test
    fun shouldInsertMultipleMeasurements() = runBlockingTest {

        // GIVEN
        val initialCount = measurementDao.getMeasurementCount()

        // WHEN
        val ids = measurementDao.insertMultipleAsync(measurementList)

        // THEN
        val measurementCount = measurementDao.getMeasurementCount()
        Truth.assertThat(initialCount).isEqualTo(0)
        Truth.assertThat(measurementCount).isEqualTo(3)
        Truth.assertThat(ids).containsAtLeastElementsIn(listOf<Long>(1, 2, 3))
    }

    @Test
    fun givenDBEmptyShouldReturnEmptyList() = runBlockingTest {

        // GIVEN
        val measurementCount = measurementDao.getMeasurementCount()

        // WHEN
        val measurementList = measurementDao.getMeasurementsAsync()

        // THEN
        Truth.assertThat(measurementCount).isEqualTo(0)
        Truth.assertThat(measurementList).hasSize(0)
    }

    @Test
    fun giveDBPopulatedShouldReturnCorrectMeasurements() = runBlockingTest {

        // GIVEN
        val expected = measurementList[0]
        measurementDao.insertAsync(expected)

        // WHEN
        val measurements = measurementDao.getMeasurementsAsync()

        // THEN
        val actual = measurements[0]
        Truth.assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun givenDBEmptyShouldReturnNullWithId() = runBlockingTest {

        // GIVEN
        val measurementCount = measurementDao.getMeasurementCount()

        // WHEN
        val measurement = measurementDao.getMeasurementsAsyncWithId(1)

        // THEN
        Truth.assertThat(measurementCount).isEqualTo(0)
        Truth.assertThat(measurement).isNull()
    }


    @Test
    fun givenDBPopulatedShouldReturnCorrectMeasurementWithId() = runBlockingTest {

        // GIVEN
        val expected = measurementList[0]
        measurementDao.insertAsync(expected)

        // WHEN
        val actual = measurementDao.getMeasurementsAsyncWithId(1)

        // THEN
        Truth.assertThat(actual).isEqualTo(expected)
    }


    @Test
    fun givenEveryMeasurementsDeletedShouldReturnEmptyList() = runBlockingTest {

        // GIVEN
        measurementDao.insertMultipleAsync(measurementList)
        val initialMeasurementCount = measurementDao.getMeasurementCount()

        // WHEN
        measurementDao.deleteAll()

        // THEN
        val measurementCount = measurementDao.getMeasurementCount()
        Truth.assertThat(initialMeasurementCount).isEqualTo(3)
        Truth.assertThat(measurementCount).isEqualTo(0)
    }


    @Before
    fun setUp() {

        // using an in-memory database because the information stored here disappears after test
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), MeasurementDatabase::class.java
        )
            // allowing main thread queries, just for testing
//            .allowMainThreadQueries()
            .build()

        measurementDao = database.measurementDao()

    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        database.close()
    }
}