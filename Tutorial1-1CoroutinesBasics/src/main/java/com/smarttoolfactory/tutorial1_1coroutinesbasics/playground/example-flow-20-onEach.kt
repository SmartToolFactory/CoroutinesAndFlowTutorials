/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from flow.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun foo20(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(1000)
        emit(i)
    }
}

fun main() = runBlocking<Unit> {

    val numbersFlow = (1..3).asFlow().onEach { delay(1000) } // flow of numbers 1,2,3

    numbersFlow
        .collect {
            println(it)
        }
}
