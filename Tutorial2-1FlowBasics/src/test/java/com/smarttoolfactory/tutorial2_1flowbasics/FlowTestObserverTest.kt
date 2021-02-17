package com.smarttoolfactory.tutorial2_1flowbasics

import com.smarttoolfactory.tutorial2_1flowbasics.flow.test
import com.smarttoolfactory.tutorial2_1flowbasics.flow.testAfterDelay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.yield
import org.junit.Rule
import org.junit.Test

class FlowTestObserverTest {

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    @Test
    fun `test TestFlowObserver`() = testCoroutineRule.runBlockingTest {
        val observer = flow { emit(12) }.test(this, true)
        observer
            .assertValues(12)
            .dispose()

        flow { emit(12) }.testAfterDelay(this) {
            assertValues(12)
        }
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
        observer.assertNoValue()
        subject.send(12)
        observer.assertValues(12)
        observer.dispose()

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

        flow.test(this, true)
            .assertValueCount(3)
            .dispose()

//        flow.testAfterDelay(this) {
//            assertValueCount(3)
//        }
    }

    @Test
    fun testInfiniteChannelFlow() = testCoroutineRule.runBlockingTest {
        val flow: Flow<Int> = channelFlow {
            delay(10_000)
            yield()

            offer(1)
            send(2)
            sendBlocking(3)

            awaitClose()
        }


//        flow.testAfterDelay(this) {
//            assertValues(1)
//            assertNotComplete()
//        }

        /*
            😱 TestObserver EXTENSION testDeclarative() INIT in thread: main @coroutine#2
            🏠 TestObserver in withTimeOut(), in thread: main @coroutine#2
            😍 FlowTestObserver init() onStart, in thread: main @coroutine#3
            🏭 FlowTestObserver createJob() job canceled: false in thread: main @coroutine#2
            🍏 FlowTestObserver init() collect, in thread: main @coroutine#3
            🍏 FlowTestObserver init() collect, in thread: main @coroutine#3
            🍏 FlowTestObserver init() collect, in thread: main @coroutine#3
            💀 FlowTestObserver init() onCompletion, in thread: main @coroutine#3


            😱 TestObserver EXTENSION test() INIT in thread: main @coroutine#1
            🏠 TestObserver in withTimeOut(), in thread: main @coroutine#1
            😍 FlowTestObserver init() onStart, in thread: main @coroutine#2
            🏭 FlowTestObserver createJob() job canceled: false in thread: main @coroutine#1
            🍏 FlowTestObserver init() collect, in thread: main @coroutine#2
            🍏 FlowTestObserver init() collect, in thread: main @coroutine#2
            🍏 FlowTestObserver init() collect, in thread: main @coroutine#2
            💀 FlowTestObserver init() onCompletion, in thread: main @coroutine#2
         */

        flow.test(this, true)
            .assertValues(1, 2, 3)
            .assertNotComplete()
            .dispose()
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
            .test(this, true)

        observer.assertNoValue()
        subject.send(1)
        observer.assertNoValue()
        advanceTimeBy(500)
        observer.assertNoValue()
        advanceTimeBy(500)
//        observer
//            .assertValues(1)
//            .dispose()
    }
}