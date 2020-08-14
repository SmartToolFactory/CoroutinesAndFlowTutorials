package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.*

/*
*   Can you do the previous example by using supervisorScope and not using supervisorJob?
*   Absolutely!
*
*   This example is a simulation of Activity4SupervisorJob.kt example.
*
*   🔥 a Job is created as a direct child of SupervisorJob as the following:
*   🔥 CoroutineScope(Dispatchers.Default).launch(parentSupervisorJob  + coroutineExceptionHandler)
*
*   🤙 child1 and child2 is the child of child of SupervisorJob.
*
*   🐲 When child2 throws an exception, SupervisorJob won’t propagate the exception up in the hierarchy and will let the child2 handle it.
*   🐲 Thus 1st child job and parent job continue to work.
*
*   Please read Manuel Vivo's article for further information:
*   https://medium.com/androiddevelopers/exceptions-in-coroutines-ce8da1ec060c
*
* */

fun main() = runBlocking {

    var child1: Job? = null
    var child2: Job? = null

    // Parent Job and Coroutine Exception Handler
    val parentSupervisorJob = SupervisorJob()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
        println("Job 1 state: ${child1?.status()}")
        println("Job 2 state: ${child2?.status()}")
        println("supervisorJob.isActive : ${parentSupervisorJob.isActive}")
    }


    supervisorScope {
        child1 = launch(Dispatchers.Default + coroutineExceptionHandler) {
            delay(1000)
        }

        supervisorScope {
            child2 = launch(Dispatchers.Default + coroutineExceptionHandler) {
                delay(500)
                throw RuntimeException("Child 2 threw RuntimeException")
            }
        }
    }

    Thread.sleep(2000L)
}
