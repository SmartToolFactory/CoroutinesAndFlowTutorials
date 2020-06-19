package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.*

/**
 * usage of invokeOnCompletion
 * */

fun main() {

    CoroutineScope(Dispatchers.IO + CoroutineName("Mr.Coroutine")).launch {
        delay(500)
    }.invokeOnCompletion {
        println("${Thread.currentThread().name}     \t\t\t    Inside invokeOnCompletion")
    }


    val parentJob = Job()
    CoroutineScope(Dispatchers.IO + parentJob + CoroutineName("Miss.Coroutine")).launch {
        delay(500)
    }.invokeOnCompletion {
        println("${Thread.currentThread().name}     \t\t    Inside invokeOnCompletion :    \t\t    ${it?.message} ")
    }
    parentJob.cancel(CancellationException("Resetting job"))


    val parentJob2 = Job()
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("${Thread.currentThread().name}     \t\t    Inside CoroutineExceptionHandler :      $exception ")
    }
    CoroutineScope(Dispatchers.IO + parentJob2 + coroutineExceptionHandler + CoroutineName("Senior.Coroutine")).launch {
        delay(100)
        throw Exception("hata")
    }.invokeOnCompletion {
        println("${Thread.currentThread().name}     \t\t    Inside invokeOnCompletion :   \t\t    ${it?.message}")
    }

    Thread.sleep(2000L)
}