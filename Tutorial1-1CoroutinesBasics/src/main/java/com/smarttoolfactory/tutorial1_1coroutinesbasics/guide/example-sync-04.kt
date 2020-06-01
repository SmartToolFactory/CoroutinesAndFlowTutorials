/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from shared-mutable-state-and-concurrency.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

suspend fun massiveRun4(action: suspend () -> Unit) {
    val n = 100  // number of coroutines to launch
    val k = 1000 // times an action is repeated by each coroutine
    val time = measureTimeMillis {
        coroutineScope { // scope for coroutines 
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
    println("Completed ${n * k} actions in $time ms")
}

val counterContext4 = newSingleThreadContext("CounterContext")
var counter4 = 0

fun main() = runBlocking {
    withContext(Dispatchers.Default) {
        massiveRun4 {
            // confine each increment to a single-threaded context
            withContext(counterContext4) {
                counter4++
            }
        }
    }
    println("Counter = $counter4")
}
