package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.*

/**
 *
 * 🤡 Cancellation of parent job with alternative way 2 🤡
 * 🔥 In this case, parent job is cancelled, then all child jobs cancelled automatically.
 * */

fun main() {

    // CoroutineScope
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    var child1: Job? = null
    var child2: Job? = null

    // Use
    val currentJob = coroutineScope.launch {
        child1 = launch {
            delay(500)
        }
        child2 = launch {
            delay(500)
        }
    }

    Thread.sleep(300L)

    currentJob.cancel()

    println("Job 1 state: ${child1?.status()}")
    println("Job 2 state: ${child2?.status()}")
    println("Parent job is active: ${currentJob.isActive}")

    Thread.sleep(2000L)
}