package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.*

/**
 *  🤡Cancellation of child job🤡
 * 🔥 In this case, child job is cancelled, but other child jobs and parent job are still active and continue execution.
 * */

fun main() {

    // Parent Job and Coroutine Exception Handler
    val parentJob = Job()

    // CoroutineScope
    val coroutineScope = CoroutineScope(Dispatchers.IO + parentJob)

    var child1: Job? = null
    var child2: Job? = null


    coroutineScope.launch {
        child1 = coroutineScope.launch {
            delay(500)
        }
        child2 = coroutineScope.launch {
            delay(500)
        }


        child1?.cancel()

        println("Job 1 state: ${child1?.status()}")
        println("Job 2 state: ${child2?.status()}")
        println("Parent job is active: ${coroutineScope.isActive}")
        println("Parent job is active: $isActive")
        println("Parent job is active: ${parentJob.isActive}")
    }


    Thread.sleep(2000L)
}