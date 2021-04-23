/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from channels.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
    for (x in 1..5) send(x * x)
}

fun main() = runBlocking {

    // 1st way of sending integers to the channel.
    /*val squares = Channel<Int>()
    launch {
        for (x in 1..5)
            squares.send(x * x)
        squares.close() // we're done sending
    }*/
    // 2nd way of sending integers to the channel.
    val squares = produceSquares()


    // 1st way of receiving integers from the channel.
    squares.consumeEach {
        delay(3000)
        println(it)
    }
    // 2nd way of receiving integers from the channel.
    /*repeat(5) {
        delay(3000)
        println(squares.receive())
    }*/

    println("Done!")
}

