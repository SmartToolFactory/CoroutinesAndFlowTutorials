/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from flow.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

fun foo31a(): Flow<Int> = (1..3).asFlow()

fun main() = runBlocking<Unit> {
    try {
        foo31a().collect { value -> println(value) }
    } finally {
        println("Done")
    }
}