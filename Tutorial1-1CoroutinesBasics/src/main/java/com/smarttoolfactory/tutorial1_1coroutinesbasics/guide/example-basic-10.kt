package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main() {
    // action1()  // Try me by uncommenting this line and commenting other line.
    // action2()  // Try me by uncommenting this line and commenting other line.
    action3()   // Try me by uncommenting this line and commenting other line.
}

fun action1() {
    val time = measureTimeMillis {
        runBlocking {
            launch(Dispatchers.Default) {
                delay(1000)
                println("Work 0 done - ${Thread.currentThread().name}")  // this line is not called
            }
        }
    }
    println("Done in $time ms")
}

fun action2() {
    val time = measureTimeMillis {
        runBlocking {
            GlobalScope.launch {
                delay(1000)
                println("Work 0 done - ${Thread.currentThread().name}")  // this line is not called
            }
        }
    }
    println("Done in $time ms")
}

fun action3() {
    val time = measureTimeMillis {
        runBlocking {
            val jobs = mutableListOf<Job>()
            for (i in 1..2) {
                jobs += GlobalScope.launch {
                    delay(1000)
                    println("Work $i done - ${Thread.currentThread().name}")  // this line is called
                }
            }
            jobs.forEach { it.join() }
        }
    }
    println("Done in $time ms")
}