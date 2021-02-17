package com.smarttoolfactory.tutorial2_1flowbasics.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)

open class BaseCoroutineJUnit5Test {

    val testCoroutineDispatcher = TestCoroutineDispatcher()
    val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)


    @BeforeEach
    open fun setUp() {
        // provide the scope explicitly, in this example using a constructor parameter
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @AfterEach
    open fun tearDown() {
        Dispatchers.resetMain()
        try {
            testCoroutineDispatcher.cleanupTestCoroutines()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}