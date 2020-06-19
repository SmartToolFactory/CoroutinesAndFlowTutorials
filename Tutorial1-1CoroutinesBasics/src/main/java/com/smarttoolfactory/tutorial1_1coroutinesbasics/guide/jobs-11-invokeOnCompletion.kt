package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.*

/**
 * usage of invokeOnCompletion
 *
 * You can call invokeOnCompletion() on a Job and register a lambda to be evaluated when the job is completed for any reason.
 * The parameter passed to the lambda will be:
 *  🏁  null if the job completed normally
 *  🚩  a CancellationException (or subclass) if the job was canceled
 *  ‍☠ ️some other type of exception if the job failed
 *
 *  https://klassbook.commonsware.com/lessons/Coroutine%20Basics/invokeOnCompletion.html
 * */

fun main() {

    val job = GlobalScope.launch(Dispatchers.IO) {
        withTimeout(2000L) {
            println("This is executed before the delay")
            stallForTime()
            println("This is executed after the delay")
        }
    }

    job.invokeOnCompletion { cause -> println("We were canceled due to ---> $cause  <---") }

    println("This is executed immediately")

    Thread.sleep(5000)
}

suspend fun stallForTime() {
    withContext(Dispatchers.Default) {
        delay(10000L)
    }
}