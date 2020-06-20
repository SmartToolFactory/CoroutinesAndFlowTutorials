package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.*

/**
 *
 * The coroutine's Job is part of its context, and can be retrieved from it using the coroutineContext[Job] expression:
 * println("My job is ${coroutineContext[Job]}")
 *
 * In the debug mode, it outputs something like this:
 * My job is "coroutine#1":BlockingCoroutine{Active}@6d311334
 *
 * To Enable the logging in IntelliJ toolbar menu:
 * Run -> Edit Configuration and add the following in VM options
 * -Dkotlinx.coroutines.debug
 * */

fun main() {

    // Parent Job and Coroutine Exception Handler
    val parentJob = Job()

    // CoroutineScope
    val coroutineScope = CoroutineScope(Dispatchers.IO + parentJob)


    coroutineScope.launch {
        coroutineScope.launch(CoroutineName("cocuk1")) {
            delay(500)
            println("My job is ${coroutineContext[Job]}")

        }
        coroutineScope.launch {
            delay(500)
            println("My job is ${coroutineContext[Job]}")
        }

        println("My job is ${coroutineContext[Job]}")


    }

    Thread.sleep(2000L)
}