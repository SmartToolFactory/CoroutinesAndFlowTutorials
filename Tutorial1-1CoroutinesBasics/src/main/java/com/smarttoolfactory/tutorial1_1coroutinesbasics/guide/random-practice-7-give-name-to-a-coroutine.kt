package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 *
 * 1 - Give name to a coroutine         ->      CoroutineName("my-custom-name")
 * 2 - Print name of the coroutine      ->      Thread.currentThread().name     -   or  -    ${coroutineContext[Job]}
 *
 * To Enable the logging in IntelliJ toolbar menu:
 * Run -> Edit Configuration and add the following in VM options
 * -Dkotlinx.coroutines.debug
 * */

fun main() = runBlocking {
    println(Thread.currentThread().name)

    val job = launch(CoroutineName("my-custom-name")) {
        println(Thread.currentThread().name)
        println("${coroutineContext[Job]}")

    }

    job.join()
}