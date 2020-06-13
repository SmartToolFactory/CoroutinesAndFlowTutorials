package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.*

/**
 * 🐲 Cancellation of CoroutineScope 🐲
 * If you cancel a CoroutineScope, then all coroutines executing inside this scope is cancelled.
 * */

fun main() {
    cancelParentJob2()
}

fun cancelParentJob2() {
    val coroutineScope = CoroutineScope(Dispatchers.Unconfined)

    val job1 = coroutineScope.launch {
        delay(500)
    }
    val job2 = coroutineScope.launch {
        delay(500)
    }

    coroutineScope.cancel()

    println("Job 1 state: ${job1.status()}")
    println("Job 2 state: ${job2.status()}")
    println("Is coroutineScope active: ${coroutineScope.isActive}")
}


fun Job.status(): String = when {
    isCancelled -> "cancelled"
    isActive -> "Active"
    isCompleted -> "Complete"
    else -> "Nothing"
}