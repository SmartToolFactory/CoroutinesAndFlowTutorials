package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.*

/**
 *
 * Calling start() on a coroutine that was started with CoroutineStart.LAZY causes the lazy flag to be removed.
 * At that point, the coroutine will be eligible to be executed. Exactly when it will be executed is up to the dispatcher and platform, and it will depend on what other coroutines exist and are running.
 *
 * https://klassbook.commonsware.com/lessons/Coroutine%20Basics/lazy-then-active.html
 * */

fun main() {
    val job = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
        stallForTime2()
        println("This is executed after the previous suspend fun stallForTime2 returns")
    }

    println("Before starting the job")
    job.start()
    println("After starting the job")
    Thread.sleep(5000)
}

suspend fun stallForTime2() {
    withContext(Dispatchers.Default) {
        delay(2000L)
    }
}