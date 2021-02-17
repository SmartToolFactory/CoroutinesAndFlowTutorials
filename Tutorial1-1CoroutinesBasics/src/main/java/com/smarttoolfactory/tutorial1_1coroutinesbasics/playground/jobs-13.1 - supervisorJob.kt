package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.*

/*
*   Look at also the following examples:
*       jobs-06-throw-exception-in-child-Job-with-SupervisorJob
*       example-supervision-01.kt
*       example-supervision-02.kt
*       example-supervision-03.kt
* */



fun main() {

    var child1: Job? = null
    var child2: Job? = null

    // Parent Job
    val supervisorJob = SupervisorJob()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
        println("Job 1 state: ${child1?.status()}")
        println("Job 2 state: ${child2?.status()}")
        println("supervisorJob.isActive : ${supervisorJob.isActive}")
    }


    val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob + coroutineExceptionHandler)

    // creates 1st child Job for SupervisorJob
    child1 = coroutineScope.launch {
        delay(100)
        throw Exception("Error happened in child 1 suddenly!")
    }

    // creates 2nd child Job for SupervisorJob
    child2 = coroutineScope.launch {
        delay(500)
    }

    Thread.sleep(2000L)
}