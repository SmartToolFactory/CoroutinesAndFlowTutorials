package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

/*
Many flow operators don't do cancellation check for performance reasons.
For example, asFlow() doesn't do cancellation check.
asFlow().cancellable() makes asFlow() cancellable.
* */

fun main() = runBlocking<Unit> {
    (1..5).asFlow().cancellable().collect { value ->
        if (value == 3) cancel()
        println(value)
    }
}