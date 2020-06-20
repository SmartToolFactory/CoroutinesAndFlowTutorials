package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    launch {
        println("5")
    }

    launch {
        println("6")
    }

    for (i in 7..10) {
        println(i.toString())
    }

    launch {
        println("4")
    }

    launch {
        println("3")
    }

    for (i in 11..14) {
        println(i.toString())
    }
}