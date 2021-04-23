/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from flow.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {

    val numbersFlow = (1..3).asFlow() // flow of numbers 1,2,3
    val stringsFlow = flowOf("one", "two", "three") // flow of strings

    numbersFlow.zip(stringsFlow) { a, b ->
        "$a -> $b"
    } // compose a single string
        .collect {
            println(it)
        } // collect and print
}
