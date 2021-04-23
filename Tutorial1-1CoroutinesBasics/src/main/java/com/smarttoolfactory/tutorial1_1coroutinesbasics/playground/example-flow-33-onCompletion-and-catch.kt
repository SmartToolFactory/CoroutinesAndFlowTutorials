/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from flow.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun foo33(): Flow<Int> = flow {
    for (i in 1..3) {
        emit(i)
    }
}

fun main() = runBlocking<Unit> {
    foo33()
        .onEach {
            check(it <= 1) { "Collected $it" }
        }
        .onCompletion { cause -> if (cause != null) println("Flow completed exceptionally - $cause") }
        .catch { cause -> println("Caught exception - $cause") }
        .collect { value -> println(value) }
}            
