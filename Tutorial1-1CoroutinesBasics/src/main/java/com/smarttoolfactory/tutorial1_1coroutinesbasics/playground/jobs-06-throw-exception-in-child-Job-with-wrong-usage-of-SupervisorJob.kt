package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.*

/**
 *     🎷🎺🎸🥁  This example is the clone of the previous example with only difference is the following line:
 *     val supervisorJob = SupervisorJob()
 *
 *     😱😱 This example illustrates the WRONG usage of SupervisorJob  ️😱😱
 *
 *
 * 🔥  In this example, exception is thrown in the child job, then what do you expect ?
 *     SupervisorJob is not work as you expected in this example.
 *     For more information please read the article by Manuel Vivo https://medium.com/androiddevelopers/exceptions-in-coroutines-ce8da1ec060c
 *
 *
 *
 *     Lets explain shortly:
 *
 *     val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob + coroutineExceptionHandler)
 *     coroutineScope.launch{}  launches a child job for supervisor job. If you throw exception in this direct child job of supervisor job, other child jobs continue to work.
 *
 *     coroutineScope.launch{
 *          launch{ }   launches a child job for child job of supervisor job. If you throw exception in this job,  since this is not the direct child of supervisor job,
 *          exception does not reach to supervisor job. Thus child2 and parent job are cancelled automatically. Supervisor job is useless in this example. Because child1 and
 *          child2 is not direct child job of SupervisorJob. The job returned by coroutineScope.launch{} is the direct child of SupervisorJob.
 *
 *     }
 * */

fun main() {

    var child1: Job? = null
    var child2: Job? = null

    val supervisorJob = SupervisorJob()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
        println("Job 1 state: ${child1?.status()}")
        println("Job 2 state: ${child2?.status()}")
        println("supervisorJob.isActive : ${supervisorJob.isActive}")
    }

    val coroutineScope = CoroutineScope(Dispatchers.IO + supervisorJob + coroutineExceptionHandler)


    coroutineScope.launch {

        child1 = launch {
            delay(100)
            throw Exception()
        }

        child2 = launch {
            delay(500)
        }

        delay(500)

    }


    Thread.sleep(2000L)
}