package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.*

@Database(
    entities = [Measurement::class],
    version = 1,
    exportSchema = true
)
abstract class MeasurementDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao
}

object DatabaseFactory {

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
    @ColumnInfo
    val measured: Double
) {

    override fun toString(): String {
        return "Measurement $id, value: $measured"
    }
}

@Dao
interface MeasurementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(measurement: Measurement): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsync(measurement: Measurement): Long

    @Insert
    suspend fun insertMultipleAsync(measurements: List<Measurement>): List<Long>

    /**
     * Returns empty list if there are no measurement in DB
     */
    @Query("SELECT * FROM measurements")
    suspend fun getMeasurementsAsync(): List<Measurement>

    /**
     * Returns null if there are no measurement in DB
     */
    @Query("SELECT * FROM measurements WHERE id =:measurementId")
    suspend fun getMeasurementsAsyncWithId(measurementId: Long): Measurement?

    @Query("SELECT COUNT(*) FROM measurements")
    suspend fun getMeasurementCount(): Int

    @Query("DELETE FROM measurements")
    suspend fun deleteAll()

    @Query("SELECT * FROM measurements")
    fun getMeasurements(): LiveData<List<Measurement>>


}