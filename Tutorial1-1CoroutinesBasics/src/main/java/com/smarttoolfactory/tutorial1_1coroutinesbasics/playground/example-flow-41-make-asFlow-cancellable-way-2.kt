package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

/*
Many flow operators don't do cancellation check for performance reasons.
For example, asFlow() doesn't do cancellation check.
Before each emission, checking ensureActive() makes asFlow() cancellable.

* */

fun main() = runBlocking<Unit> {
    (1..5).asFlow().onEach { currentCoroutineContext().ensureActive() }
        .collect { value ->
            if (value == 3) cancel()
            println(value)
        }
}