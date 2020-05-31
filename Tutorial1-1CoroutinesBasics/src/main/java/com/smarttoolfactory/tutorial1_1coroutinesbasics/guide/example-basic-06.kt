/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from basics.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking { // this: CoroutineScope
    launch {
        delay(200L)
        println("Task from runBlocking - ${Thread.currentThread().name}")
    }

    coroutineScope { // Creates a coroutine scope
        launch {
            delay(500L)
            println("Task from nested launch - ${Thread.currentThread().name}")
        }

        delay(100L)
        println("Task from coroutine scope - ${Thread.currentThread().name}") // This line will be printed before the nested launch
    }

    println("Coroutine scope is over - ${Thread.currentThread().name}") // This line is not printed until the nested launch completes
}
