/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from coroutine-context-and-dispatchers.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

fun log3(msg: String) = println("[${Thread.currentThread().name}] $msg")

fun main() = runBlocking<Unit> {
    val a = async {
        log3("I'm computing a piece of the answer")
        6
    }
    val b = async {
        log3("I'm computing another piece of the answer")
        7
    }
    log3("The answer is ${a.await() * b.await()}")
}
