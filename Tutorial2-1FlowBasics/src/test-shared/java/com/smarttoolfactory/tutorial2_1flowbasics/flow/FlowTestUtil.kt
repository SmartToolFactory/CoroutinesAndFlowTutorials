package com.smarttoolfactory.tutorial2_1flowbasics.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.coroutineContext


class FlowTestObserver<T>(
    private val coroutineScope: CoroutineScope,
    private val flow: Flow<T>,
    private val waitTimeOut: Boolean = false
) {
    private val testValues = mutableListOf<T>()
    private var error: Throwable? = null

    @Volatile
    private var isInitialized = false

    private var isCompleted = false

    private lateinit var job: Job


    private suspend fun initializeAndJoin() {

        println("🏢  TestObserver in initializeWithNoTimeOut(), in thread: ${Thread.currentThread().name}")

        job = createJob(coroutineScope)

        // Wait this job after end of possible delays
        job.join()

        println("👍 TestObserver initializeAndJoin() Job canceled: ${job.isCancelled}")

    }


    private suspend fun initialize() {

        if (!isInitialized) {
            isInitialized = true

            if (waitTimeOut) {
                withTimeout(Long.MAX_VALUE) {
                    println("🏠 TestObserver in withTimeOut(), in thread: ${Thread.currentThread().name}")
                    job = createJob(this)
                }
            } else {
                initializeAndJoin()
            }

            println("🎃 FlowTestObserver initialize() ENDED!, in thread: ${Thread.currentThread().name}")
            require(job != null)
            require(isInitialized)

        }


    }

    private fun createJob(scope: CoroutineScope): Job {
        return flow
            .onStart {
                println("😍 FlowTestObserver init() onStart, in thread: ${Thread.currentThread().name}")
            }
            .onCompletion {
                println("💀 FlowTestObserver init() onCompletion, in thread: ${Thread.currentThread().name}")
                isCompleted = true
            }
            .catch { throwable ->
                println("🤬 FlowTestObserver init() catch, in thread: ${Thread.currentThread().name}")
                error = throwable
            }
            .onEach {
                println("🍏 FlowTestObserver init() collect, in thread: ${Thread.currentThread().name}")
                testValues.add(it)
            }
            .launchIn(scope)
    }


    suspend fun assertNoValues(): FlowTestObserver<T> {

        initialize()

        println("⚠️ assertNoValues: ${testValues.size}, in thread: ${Thread.currentThread().name}")

        if (testValues.isNotEmpty()) throw AssertionError(
            "Assertion error! Actual size ${testValues.size}"
        )
        return this
    }

    suspend fun assertValueCount(count: Int): FlowTestObserver<T> {

        initialize()

        println("⚠️ assertValueCount(): ${testValues.size}, in thread: ${Thread.currentThread().name}")

        if (count < 0) throw AssertionError(
            "Assertion error! Value count cannot be smaller than zero"
        )
        if (count != testValues.size) throw AssertionError(
            "Assertion error! Expected $count while actual ${testValues.size}"
        )
        return this
    }

    suspend fun assertValues(vararg values: T): FlowTestObserver<T> {

        initialize()

        println("⚠️ assertValues(): ${testValues.size}, in thread: ${Thread.currentThread().name}")


        if (!testValues.containsAll(values.asList()))
            throw  AssertionError("Assertion error! At least one value does not match")
        return this
    }

    suspend fun assertValues(predicate: (List<T>) -> Boolean): FlowTestObserver<T> {

        initialize()

        if (!predicate(testValues))
            throw  AssertionError("Assertion error! At least one value does not match")
        return this
    }

    suspend fun assertError(throwable: Throwable): FlowTestObserver<T> {

        initialize()

        val errorNotNull = exceptionNotNull()

        if (!(errorNotNull::class.java == throwable::class.java &&
                    errorNotNull.message == throwable.message)
        )
            throw AssertionError("Assertion Error! throwable: $throwable does not match $errorNotNull")
        return this
    }

    suspend fun assertError(errorClass: Class<Throwable>): FlowTestObserver<T> {

        initialize()

        val errorNotNull = exceptionNotNull()

        if (errorNotNull::class.java != errorClass)
            throw  AssertionError("Assertion Error! errorClass $errorClass does not match ${errorNotNull::class.java}")
        return this
    }

    suspend fun assertError(predicate: (Throwable) -> Boolean): FlowTestObserver<T> {

        initialize()

        val errorNotNull = exceptionNotNull()

        if (!predicate(errorNotNull))
            throw AssertionError("Assertion Error! Exception for $errorNotNull")
        return this
    }

    suspend fun assertNoError(): FlowTestObserver<T> {

        initialize()

        if (error != null)
            throw AssertionError("Assertion Error! Exception occurred $error")

        return this
    }

    suspend fun assertNull(): FlowTestObserver<T> {

        initialize()

        testValues.forEach {
            if (it != null) throw AssertionError("Assertion Error! There are more than one item that is not null")
        }

        return this
    }

    suspend fun assertCompleted(): FlowTestObserver<T> {

        initialize()

        if (!isCompleted) throw AssertionError("Assertion Error! Job is not completed yet!")
        return this
    }

    suspend fun assertNotCompleted(): FlowTestObserver<T> {

        initialize()

        if (isCompleted) throw AssertionError("Assertion Error! Job is completed!")
        return this
    }

    suspend fun values(predicate: (List<T>) -> Unit): FlowTestObserver<T> {
        predicate(testValues)
        return this
    }

    suspend fun values(): List<T> {

        initialize()

        return testValues
    }


    private fun exceptionNotNull(): Throwable {

        if (error == null)
            throw  AssertionError("There is no exception")

        return error!!
    }

    fun dispose() {
        job.cancel()
    }
}

fun <T> Flow<T>.test(scope: CoroutineScope): FlowTestObserver<T> {

    println("😱 TestObserver EXTENSION test() INIT in thread: ${Thread.currentThread().name}")

//    return withContext(coroutineContext) {
//        FlowTestObserver(this, this@test)
//    }

    return FlowTestObserver(scope, this@test)
}

/**
 * Test function that awaits with time out until each delay method is run and then
 */
suspend fun <T> Flow<T>.testAfterDelay(
    scope: CoroutineScope,
    predicate: suspend FlowTestObserver<T>.() -> Unit

): Job {
    return scope.launch(coroutineContext) {

        println("😱 TestObserver EXTENSION testDeclarative() INIT in thread: ${Thread.currentThread().name}")
        FlowTestObserver(this, this@testAfterDelay).predicate()
    }
}
