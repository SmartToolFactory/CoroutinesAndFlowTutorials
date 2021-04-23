/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from channels.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking { // coroutine 1

    val channel = Channel<Int>()
    launch { // coroutine 2
        // this might be heavy CPU-consuming computation or async logic, we'll just send five squares
        for (x in 1..5) {
            println("send $x ")
            channel.send(x * x)
        }

    }
    // here we print five received integers:
    repeat(5) {
        delay(3000)
        println(channel.receive())
    }
    println("Done!")
}

