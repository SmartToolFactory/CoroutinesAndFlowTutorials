/*
 * Copyright 2016-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

// This file was automatically generated from flow.md by Knit tool. Do not edit.
package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    launch {
        (1..3).asFlow().onEach { delay(100) }
            .onEach { event -> println("Event: $event") }
            .collect() // <--- Launching the flow in a separate coroutine
    }
    println("Done")
}

/*
launchIn() is a shorthand for
    scope.launch{
        flow.collect()
    }
* */