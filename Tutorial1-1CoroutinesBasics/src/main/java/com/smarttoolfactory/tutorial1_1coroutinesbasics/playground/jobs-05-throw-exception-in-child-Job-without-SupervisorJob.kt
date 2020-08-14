package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.*

/**
 * 💀 Throw exception in child job💀
 * 🔥 In this case, exception is thrown in the first child job, then parent job and second child job ARE cancelled automatically.
 *
 *     ❤️ If you want parent job and second child job to continue work even if the first child throws an exception, then you have 2 choices
 *     1 - You can use try catch inside child1's launch scope. If you throw exception inside try catch inside child1's launch scope,
 *     parent job and second child job are not affected from this exception and continue to work
 *     2 - You can create a SupervisorJob object, and give this object to context of parent job.
 *     Then exception is catched by CoroutineExceptionHandler. Parent job and second child job are not affected from this exception and continue to work.
 *     ❤️
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

        child1 = launch {
            delay(100)
            throw Exception("Errorrr!!!")
        }

        child2 = launch {
            delay(500)
        }

        delay(500)

    }


    Thread.sleep(2000L)
}