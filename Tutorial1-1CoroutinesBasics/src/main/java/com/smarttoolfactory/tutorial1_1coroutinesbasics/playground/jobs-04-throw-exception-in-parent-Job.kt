package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.*

/**
 * 💀Throw exception in parent job💀
 * 🔥 In this case, exception is thrown in the parent job, then all child jobs cancelled automatically.
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


    // Use
    coroutineScope.launch {

        child1 = launch {
            delay(500)
        }
        child2 = launch {
            delay(500)
        }

        delay(200)
        throw RuntimeException()

    }


    Thread.sleep(2000L)
}