package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.DatabaseFactory
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter7_database.database.Measurement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * In this tutorial database suspend functions with launch run concurrently even though all
 * of the dispatchers of [launch] is [Dispatchers.Main]
 */
class Activity7_2DatabaseThreading : AppCompatActivity() {

    val measurementDao by lazy {
        DatabaseFactory.getMeasurementDatabase(application).measurementDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scope = GlobalScope

//        println("START in thread: ${Thread.currentThread().name}")
//        Thread {
//            println("FIRST launch Start in thread: ${Thread.currentThread().name}")
//            addMeasurement1()
//            println("FIRST launch END in thread: ${Thread.currentThread().name}")
//        }.start()
//
//        println("FIRST END in thread: ${Thread.currentThread().name}")
//
//        Thread {
//            println("SECOND launch Start in thread: ${Thread.currentThread().name}")
//            addMeasurement2()
//            println("SECOND launch END in thread: ${Thread.currentThread().name}")
//        }.start()
//        println("END in thread: ${Thread.currentThread().name}")


        scope.launch(Dispatchers.Main) {

            println("START in thread: ${Thread.currentThread().name}")
            scope.launch(Dispatchers.Main) {

                println("FIRST launch Start in thread: ${Thread.currentThread().name}")
                addMeasurementSuspend1()
                println("FIRST launch END in thread: ${Thread.currentThread().name}")

                scope.launch(Dispatchers.Main) {
                    println("INNER LAUNCH")
                }
            }

            println("FIRST END in thread: ${Thread.currentThread().name}")

            scope.launch(Dispatchers.Main) {
                println("SECOND launch Start in thread: ${Thread.currentThread().name}")
                addMeasurementSuspend2()
                println("SECOND launch END in thread: ${Thread.currentThread().name}")
            }
            println("END in thread: ${Thread.currentThread().name}")
        }

        /*
            Prints:
            I: START in thread: main
            I: FIRST END in thread: main
            I: END in thread: main
            I: FIRST launch Start in thread: main
            I: SECOND launch Start in thread: main
            I: 🔥 First function add: Measurement 0, value: 0.0 in thread: main
            I: 🍏 Second function add: Measurement 0, value: 11.0 in thread: main
            I: 🔥 First function add: Measurement 0, value: 1.0 in thread: main
            I: 🍏 Second function add: Measurement 0, value: 12.0 in thread: main
            I: 🔥 First function add: Measurement 0, value: 2.0 in thread: main
            I: 🍏 Second function add: Measurement 0, value: 13.0 in thread: main
            I: 🔥 First function add: Measurement 0, value: 3.0 in thread: main
            I: 🍏 Second function add: Measurement 0, value: 14.0 in thread: main
            I: 🔥 First function add: Measurement 0, value: 4.0 in thread: main
            I: 🍏 Second function add: Measurement 0, value: 15.0 in thread: main
            I: 🔥 First function add: Measurement 0, value: 5.0 in thread: main
            I: 🍏 Second function add: Measurement 0, value: 16.0 in thread: main
            I: 🔥 First function add: Measurement 0, value: 6.0 in thread: main
            I: 🍏 Second function add: Measurement 0, value: 17.0 in thread: main
            I: 🔥 First function add: Measurement 0, value: 7.0 in thread: main
            I: 🍏 Second function add: Measurement 0, value: 18.0 in thread: main
            I: 🔥 First function add: Measurement 0, value: 8.0 in thread: main
            I: 🍏 Second function add: Measurement 0, value: 19.0 in thread: main
            I: 🔥 First function add: Measurement 0, value: 9.0 in thread: main
            I: 🍏 Second function add: Measurement 0, value: 20.0 in thread: main
            I: SECOND launch END in thread: main
            I: 🔥 First function add: Measurement 0, value: 10.0 in thread: main
            I: FIRST launch END in thread: main
            I: INNER LAUNCH

         */

    }

    private fun addMeasurement1() {
        for (i in 0..10) {
            val measurement = Measurement(measured = i.toDouble())
            measurementDao.insert(measurement)
            println("🔥 First function add: $measurement in thread: ${Thread.currentThread().name}")
        }
    }

    private fun addMeasurement2() {
        for (i in 11..20) {
            val measurement = Measurement(measured = i.toDouble())
            measurementDao.insert(measurement)
            println("🍏 Second function add: $measurement in thread: ${Thread.currentThread().name}")
        }
    }

    private suspend fun addMeasurementSuspend1() {
        for (i in 0..10) {
            val measurement = Measurement(measured = i.toDouble())
            measurementDao.insertAsync(measurement)
            println("🔥 First function add: $measurement in thread: ${Thread.currentThread().name}")
        }
    }

    private suspend fun addMeasurementSuspend2() {
        for (i in 11..20) {
            val measurement = Measurement(measured = i.toDouble())
            measurementDao.insertAsync(measurement)
            println("🍏 Second function add: $measurement in thread: ${Thread.currentThread().name}")
        }
    }


}