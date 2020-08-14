package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground


import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() = runBlocking<Unit> {
    val time = measureTimeMillis {
        val one = async(start = CoroutineStart.LAZY) { fun1() }
        val two = async(start = CoroutineStart.LAZY) { fun2() }
        // some computation
        one.start() // start the first one
        two.start() // start the second one
        println("The answer is ${one.await() + two.await()}")
    }
    println("Completed in $time ms")
}

suspend fun fun1(): Int {
    delay(1000L) // pretend we are doing something useful here
    return 13
}

suspend fun fun2(): Int {
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}
