/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from flow.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

/*
*  To get all the values in the stream as they're emitted, use collect()
* */

fun main() = runBlocking<Unit> {
    println("Calling foo...")
    val flow = foo5()

    println("Calling collect...")
    flow.collect { value -> println(value) }

    println("Calling collect again...")
    flow.collect { value -> println(value) }
}

fun foo5(): Flow<Int> = flow {
    println("Flow started")
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}
