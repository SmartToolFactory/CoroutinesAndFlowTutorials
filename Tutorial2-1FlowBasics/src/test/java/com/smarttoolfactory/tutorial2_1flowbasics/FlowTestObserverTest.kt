package com.smarttoolfactory.tutorial2_1flowbasics

import com.smarttoolfactory.tutorial2_1flowbasics.flow.test
import com.smarttoolfactory.tutorial2_1flowbasics.flow.testAfterDelay
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FlowTestObserverTest {

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    fun <T> Flow<T>.testMe(scope: CoroutineScope): MyTestObserver<T> {
        return MyTestObserver(scope, this)
    }

    class MyTestObserver<T>(
        scope: CoroutineScope,
        flow: Flow<T>
    ) {
        private val values = mutableListOf<T>()
        private val job: Job = scope.launch {
            flow.collect { values.add(it) }
        }

        fun assertNoValues(): MyTestObserver<T> {
            assertEquals(emptyList<T>(), this.values)
            return this
        }

        fun assertValues(vararg values: T): MyTestObserver<T> {
            assertEquals(values.toList(), this.values)
            return this
        }

        fun finish() {
            job.cancel()
        }
    }

    @Test
    fun `test TestFlowObserver`() = testCoroutineRule.runBlockingTest {
        val observer = flow { emit(12) }.test(this)
        observer
            .assertValues(12)
            .dispose()

//        flow { emit(12) }.testAfterDelay(this){
//            assertValues(12)
//        }
    }


    @Test
    fun `test TestFlowObserver with Subject`() = testCoroutineRule.runBlockingTest {

        println("RUNNING test() in thread: ${Thread.currentThread().name}")

        val subject = Channel<Int>()

//        subject.consumeAsFlow().testAfterDelay(this) {
//            assertNoValues()
//
//            subject.send(12)
//
//            assertValues(1)
//        }

        val observer = subject.consumeAsFlow().test(this)
        observer.assertNoValues()
        subject.send(12)
        observer.assertValues(12)
//        observer.dispose()

    }

    @Test
    fun `test TestFlowObserver with delay`() = testCoroutineRule.runBlockingTest {

        println("RUNNING test() in thread: ${Thread.currentThread().name}")

        val flow = flow {
            delay(500)
            emit(1)
            delay(500)
            emit(2)
            delay(500)
            emit(3)
        }

        flow.test(this)
            .assertValueCount(3)
            .dispose()

//        flow.testAfterDelay(this) {
//            assertValueCount(3)
//        }

    }

    /**
     * ❌ This test fails if flowOn(Dispatchers.IO) or  flowOn(Dispatchers.Default) exists
     */
    @Test
    fun `test TestFlowObserver with debounce`() = testCoroutineRule.runBlockingTest {

        println("RUNNING test() in thread: ${Thread.currentThread().name}")

        val subject = Channel<Int>()
        val observer = subject.consumeAsFlow()
//            .flowOn(Dispatchers.IO)
            .map {
                it
            }
            .debounce(1000)
            .flowOn(Dispatchers.Default)
            .test(this)

        observer.assertNoValues()
        subject.send(1)
        observer.assertNoValues()
        advanceTimeBy(500)
        observer.assertNoValues()
        advanceTimeBy(500)
        observer
            .assertValues(1)
            .dispose()
    }
}