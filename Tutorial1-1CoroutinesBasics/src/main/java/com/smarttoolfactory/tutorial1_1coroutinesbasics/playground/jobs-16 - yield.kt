package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

/*
*  Look at also the example-exceptions-03.kt
*
*
*  It temporarily deprioritises the current long running CPU task, giving other tasks(coroutines) a fair opportunity to run.
*
*  If the work you’re doing is 1) CPU heavy, 2) may exhaust the thread pool and 3) you want to allow the thread to do other work without having to add more threads to the pool, then use yield().
*
* */

fun main() = runBlocking {


    val job1 = launch {
        repeat(10) {
            delay(1000)
            println("$it. step done in job 1 ")
            yield()
        }
    }

    val job2 = launch {
        repeat(10) {
            delay(1000)
            println("$it. step done in job 2 ")
            yield()
        }
    }

    job1.join()
    job2.join()
    println("done")
}
