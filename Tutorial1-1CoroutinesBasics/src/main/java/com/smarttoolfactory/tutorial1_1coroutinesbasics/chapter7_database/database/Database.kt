package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.*

@Database(
    entities = [Measurement::class],
    version = 1,
    exportSchema = true
)
abstract class MeasurementDatabase:RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao
}

object DatabaseFactory{

    @JvmStatic
    fun getMeasurementDatabase(application: Application): MeasurementDatabase {

        return Room.databaseBuilder(
            application,
            MeasurementDatabase::class.java,
            "measurements.db"
        ).build()

    }

}

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val measured: Double
)

@Dao
interface MeasurementDao {

    @Insert
    suspend fun insertAsync(measurement: Measurement): Long

    @Query("SELECT * FROM measurements")
    suspend fun getMeasurementsAsync(): List<Measurement>

    @Insert
    fun insert(measurement: Measurement): LiveData<Long>

    @Query("SELECT * FROM measurements")
    fun getMeasurements(): LiveData<Measurement>


}