package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.*

/**
 *  🤡Cancellation of child job🤡
 * 🔥 In this case, child job is cancelled, but other child jobs and parent job are still active and continue execution.
 * */

fun main() {

    val parentJob = Job()

    val coroutineScope = CoroutineScope(Dispatchers.IO + parentJob)

    var child1: Job? = null
    var child2: Job? = null


    coroutineScope.launch {
        child1 = launch {
            delay(500)
        }
        child2 = launch {
            delay(500)
        }


        child1?.cancel()

        println("Job 1 state: ${child1?.status()}")
        println("Job 2 state: ${child2?.status()}")
        println("parentJob.isActive : ${parentJob.isActive}")
        println("coroutineScope.isActive : ${coroutineScope.isActive}")
        println("coroutineScope.isActive : $isActive")
    }


    Thread.sleep(2000L)
}