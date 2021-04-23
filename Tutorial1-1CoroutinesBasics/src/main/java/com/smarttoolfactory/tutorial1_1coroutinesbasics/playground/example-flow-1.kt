/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from flow.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

fun main() {

    withoutSequence()

    withSequence()
}

private fun withoutSequence() {
    val result = listOf("a", "b", "ac", "d", "e", "f", "g", "h", "i", "j", "ak")
        .filter {
            println("filter: $it")
            it.startsWith("a", ignoreCase = true)
        }
        .map {
            println("map: $it")
            it.toUpperCase()
        }
        .take(2)
        .toList()

    println("size: ${result.size}")
}

private fun withSequence() {
    val result = listOf("a", "b", "ac", "d", "e", "f", "g", "h", "i", "j", "ak")
        .asSequence()
        .filter {
            println("filter: $it")
            it.startsWith("a", ignoreCase = true)
        }
        .map {
            println("map: $it")
            it.toUpperCase()
        }
        .take(2)
        .toList()

    println("size: ${result.size}")
}