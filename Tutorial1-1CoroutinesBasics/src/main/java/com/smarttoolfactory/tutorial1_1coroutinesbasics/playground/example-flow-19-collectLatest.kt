/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from flow.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis


fun foo19(): Flow<Int> = flow {
    for (i in 1..3) {
        println("- value : $i - inside flow. delaying 1 second")
        delay(1000) // pretend we are asynchronously waiting 100 ms
        println("- value : $i - inside flow. Emitting")
        emit(i) // emit next value
    }
}

fun main() = runBlocking<Unit> {
    val time = measureTimeMillis {
        foo19()
            .collectLatest { value ->
                println("- value : $value - inside collect. delaying 5 second")
                delay(5000) // pretend we are processing it for 300 ms
                println("- value : $value - inside collect. completed")
            }
    }
    println("Collected in $time ms")
}
