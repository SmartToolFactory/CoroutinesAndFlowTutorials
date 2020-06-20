package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.*

/*
* *  Can you do the previous example by using supervisorScope and not using supervisorJob?
* *  Absolutely!
* */



fun main() {

    runBlocking {

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

        supervisorScope {
            // creates 1st child Job for SupervisorJob
            child1 = launch(Dispatchers.IO + coroutineExceptionHandler) {
                delay(100)
                throw Exception("Error happened in child 1 suddenly!")
            }

            // creates 2nd child Job for SupervisorJob
            child2 = launch(Dispatchers.IO + coroutineExceptionHandler) {
                delay(500)
            }
        }

    }
}