package com.smarttoolfactory.tutorial1_1coroutinesbasics.guide

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/*
*   try catch can be used rather then using SupervisorJob or SupervisorScope in order to prevent cancellation of other child jobs.
* */

fun main() {

    /**
     * Exception is caught by try catch
     * */
    GlobalScope.launch {
        try {
            println("1 - Throwing exception from launch")
            throw IndexOutOfBoundsException()
            println("1 - Unreached")
        } catch (e: IndexOutOfBoundsException) {
            println("1 - Caught IndexOutOfBoundsException")
        }
    }

    /*
    * Exception is thrown and crash occurs.
    * */
    GlobalScope.launch {
        println("2 - Throwing exception from launch")
        throw Exception("ERROR!!!")
        println("2 - Unreached")
    }
    /*val deferred = GlobalScope.async {
        println("Throwing exception from async")
        throw ArithmeticException()
        println("Unreached")
    }*/

    Thread.sleep(2000)
}
