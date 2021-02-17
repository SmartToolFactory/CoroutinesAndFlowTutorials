package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.*

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

    val coroutineScope = CoroutineScope(supervisorJob + coroutineExceptionHandler)

    // launch the first child -- its exception is ignored for this example (don't do this in practice!)
    firstChild = coroutineScope.launch {
        delay(500)
        throw AssertionError("First child is cancelled")
    }

    // launch the second child
    secondChild = coroutineScope.launch {

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


    // wait until the first child fails & completes
    secondChild.join()
}
