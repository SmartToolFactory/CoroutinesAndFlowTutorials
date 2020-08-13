package com.smarttoolfactory.tutorial2_1flowbasics.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class FlowTestObserver<T>(
    coroutineScope: CoroutineScope,
    private val flow: Flow<T>
) {
    private val testValues = mutableListOf<T>()
    private var error: Throwable? = null

    private val job: Job = coroutineScope.launch {
        flow
            .catch { throwable ->
                error = throwable
            }
            .collect {
                testValues.add(it)
            }
    }

//    private val job: Job = flow
//        .catch { throwable ->
//            error = throwable
//        }
//        .onEach { testValues.add(it) }
//        .launchIn(scope = coroutineScope)


    fun assertNoValues(): FlowTestObserver<T> {
        if (testValues.isNotEmpty()) throw AssertionError(
            "Assertion error! Actual size ${testValues.size}"
        )
        return this
    }

    fun assertValueCount(count: Int): FlowTestObserver<T> {
        if (count < 0) throw AssertionError(
            "Assertion error! Value count cannot be smaller than zero"
        )
        if (count != testValues.size) throw AssertionError(
            "Assertion error! Expected $count while actual ${testValues.size}"
        )
        return this
    }

    fun assertValues(vararg values: T): FlowTestObserver<T> {
        if (!testValues.containsAll(values.asList()))
            throw  AssertionError("Assertion error! At least one value does not match")
        return this
    }

//    fun assertValues(predicate: List<T>.() -> Boolean): TestObserver<T> {
//        testValues.predicate()
//        return this
//    }
//
//    fun values(predicate: List<T>.() -> Unit): TestObserver<T> {
//        testValues.predicate()
//        return this
//    }

    fun assertValues(predicate: (List<T>) -> Boolean): FlowTestObserver<T> {
        if (!predicate(testValues))
            throw  AssertionError("Assertion error! At least one value does not match")
        return this
    }

    fun assertError(throwable: Throwable): FlowTestObserver<T> {

        val errorNotNull = exceptionNotNull()

        if (!(errorNotNull::class.java == throwable::class.java &&
                    errorNotNull.message == throwable.message)
        )
            throw AssertionError("Assertion Error! throwable: $throwable does not match $errorNotNull")

        return this
    }

    fun assertError(errorClass: Class<Throwable>): FlowTestObserver<T> {

        val errorNotNull = exceptionNotNull()

        if (errorNotNull::class.java != errorClass)
            throw  AssertionError("Assertion Error! errorClass $errorClass does not match ${errorNotNull::class.java}")

        return this
    }

    fun assertError(predicate: (Throwable) -> Boolean): FlowTestObserver<T> {

        val errorNotNull = exceptionNotNull()

        if (!predicate(errorNotNull))
            throw AssertionError("Assertion Error! Exception for $errorNotNull")

        return this
    }

    fun values(predicate: (List<T>) -> Unit): FlowTestObserver<T> {
        predicate(testValues)
        return this
    }

    fun values(): List<T> {
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
    return FlowTestObserver(scope, this)
}