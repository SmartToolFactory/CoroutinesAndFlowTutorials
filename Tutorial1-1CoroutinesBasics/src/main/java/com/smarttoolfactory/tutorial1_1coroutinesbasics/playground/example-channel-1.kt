/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from channels.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
Rendezvous channel example
*/


fun main() = runBlocking {
    val channel = Channel<String>()

    launch {
        println("Sending A1")
        channel.send("A1")
        println("Sending A2")
        channel.send("A2")
        println("A done")
    }

    launch {
        println("Sending B1")
        channel.send("B1")
        println("B done")
    }

    launch {
        repeat(3) {
            println("Calling receive()")
            val x = channel.receive()
            println("receive is done $x")
        }
    }

    println("Done!")
}

/*
* Output :

Done!
Sending A1
Sending B1
Calling receive()
receive is done A1
Calling receive()
receive is done B1
Calling receive()
Sending A2
A done
B done
receive is done A2

* */