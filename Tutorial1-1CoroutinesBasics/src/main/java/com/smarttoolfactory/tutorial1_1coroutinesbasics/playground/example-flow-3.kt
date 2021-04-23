/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from flow.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    foo2().forEach { value -> println(value) }

    runBlocking<Unit> {
        foo3().forEach { value -> println(value) }
    }
}

fun foo2(): Sequence<Int> = sequence { // sequence builder
    for (i in 1..3) {
        Thread.sleep(1000) // pretend we are computing it
        yield(i) // yield next value
    }
}


suspend fun foo3(): List<Int> {
    delay(1000) // pretend we are doing something asynchronous here
    return listOf(1, 2, 3)
}