package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

/*
* Since collect() is a suspending function, it can only be called from a coroutine or another suspending function.
* This is why you wrap the code with runBlocking().
* */
fun main() {
    val namesFlow = flow {
        val names = listOf("Jody", "Steve", "Lance", "Joe")
        for (name in names) {
            delay(1000)
            emit(name)
        }
    }

    runBlocking {
        namesFlow.collect { println(it) }
    }
}

