package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.*

/*
* Can you do the previous example by using supervisorScope and not using supervisorJob?
* Absolutely!
* */

fun main() = runBlocking {
    val supervisorJob = SupervisorJob()
    var firstChild: Job? = null
    var secondChild: Job? = null

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
        println("Job 1 state: ${firstChild?.status()}")
        println("Job 2 state: ${secondChild?.status()}")
        println("supervisorJob.isActive : ${supervisorJob.isActive}")
    }


    supervisorScope {
        // launch the first child -- its exception is ignored for this example (don't do this in practice!)
        firstChild = launch(coroutineExceptionHandler) {
            delay(500)
            throw AssertionError("First child is cancelled")
        }

        // launch the second child
        secondChild = launch(coroutineExceptionHandler) {

            try {
                for (i in 20 downTo 0) {
                    delay(300)
                    println("still active $i")
                }
                throw Exception("Error inside 2nd child job")
            } finally {
                println("Second child is cancelled because supervisor is cancelled")
            }
        }
    }

    println("done")
}
