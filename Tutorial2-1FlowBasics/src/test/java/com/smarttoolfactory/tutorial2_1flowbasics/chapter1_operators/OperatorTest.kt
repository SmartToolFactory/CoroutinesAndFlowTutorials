package com.smarttoolfactory.tutorial2_1flowbasics.chapter1_operators

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OperatorTest {


    @Test
    fun test() = runBlockingTest {

        val subject = Channel<Int>()
        val values = mutableListOf<Int>()

        val job = launch {
            subject.consumeAsFlow()
                .collect {
                    values.add(it)
                }
        }
        assertEquals(emptyList<Int>(), values)
        subject.send(1)

        assertEquals(listOf(1), values)
        job.cancel()
    }
}