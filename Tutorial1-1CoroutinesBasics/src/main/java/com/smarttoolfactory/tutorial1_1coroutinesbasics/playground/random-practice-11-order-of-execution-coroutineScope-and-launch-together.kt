package com.smarttoolfactory.tutorial1_1coroutinesbasics.playground

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/*
* Clone of coroutineScope-1.kt
*
* 🔥 launch returns immediately but inside of launch not eecuted immediately. non-blocking code after launch is executed firstly, then inside of launch is executed.
* 🐲 coroutineScope is a blocking code. Code execution does not go to following line,
* waits for completion of inside of coroutineScope and coroutines started before coroutineScope. Thus printed 3 1 2.
*
* https://stackoverflow.com/questions/53535977/coroutines-runblocking-vs-coroutinescope/53536713#53536713  önce buna bak
* */
fun main() = runBlocking {
    launch {
        println("1")
    }

    coroutineScope {
        launch {
            println("2")
        }

        println("3")
    }

    coroutineScope {
        launch {
            println("4")
        }

        println("5")
    }

    launch {
        println("6")
    }

    for (i in 7..100) {
        println(i.toString())
    }

    println("101")
}