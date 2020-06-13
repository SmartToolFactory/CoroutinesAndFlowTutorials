package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.*

/**
 * 💀 Throw exception in child job💀
 * 🔥 In this case, exception is thrown in the child job, then parent job and all other child jobs cancelled automatically.
 * */

fun main() {

    var child1: Job? = null
    var child2: Job? = null

    // Parent Job and Coroutine Exception Handler
    val parentJob = Job()

    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
        println("Job 1 state: ${child1?.status()}")
        println("Job 2 state: ${child2?.status()}")
        println("Parent job is active: ${parentJob.isActive}")
    }

    // CoroutineScope
    val coroutineScope = CoroutineScope(Dispatchers.IO + parentJob + handler)


    coroutineScope.launch {

        child1 = coroutineScope.launch {
            delay(100)
            throw Exception()
        }

        child2 = coroutineScope.launch {
            delay(500)
        }

        delay(500)

    }


    Thread.sleep(2000L)
}