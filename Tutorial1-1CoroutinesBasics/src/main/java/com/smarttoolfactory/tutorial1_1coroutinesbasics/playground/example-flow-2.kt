/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from flow.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

fun main() {

    val seqFromChunks = sequence {
        yield(1)
        println("test 1")
        yieldAll((2..5).toList())
        println("test 2") // this line and the below lines is not executed.
        yield(6)

        yieldAll(listOf(7, 8, 9))
        yieldAll(generateSequence(10) { it + 2 })
    }

    println(seqFromChunks.take(5).toList())

    println("---------------------------------")

    val seqFromChunks2 = sequence {
        yield(1)
        println("test 1")
        yieldAll((2..5).toList())
        println("test 2")
        yield(6)

        yieldAll(listOf(7, 8, 9))
        println("test 3")
        yieldAll(generateSequence(10) { it + 2 })
        println("test 4")  // this line is not executed.
    }

    println(seqFromChunks2.take(10).toList())
}

