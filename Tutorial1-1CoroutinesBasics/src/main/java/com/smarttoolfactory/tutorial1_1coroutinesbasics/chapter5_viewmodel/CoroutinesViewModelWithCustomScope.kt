package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter5_viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * This viewModel class only for testing with [CoroutineScope] using [TestDispatcherScope]
 */
class CoroutinesViewModelWithCustomScope(private val coroutineScope: CoroutineScope) : ViewModel() {

    val result = MutableLiveData<String>()

    val resultWithRetry = MutableLiveData<String>()
    val resultWithTimeout = MutableLiveData<String>()
    val resultMultiple = MutableLiveData<String>()


    fun getMockResult(timeMillis: Long = 2000) {

        println("getMockResult() loading...")
        result.value = "Loading..."

        coroutineScope.launch {
            println("🙄 getMockResult() START ViewModel scope: $this, thread: ${Thread.currentThread().name}")
            result.value = generateMockNetworkResponseOrThrowException(timeMillis)
            println("getMockResult() ViewModel scope AFTER generateMockNetworkResponse() ${result.value}")

        }

        println("getMockResult() END OF FUN")

        /*
            Prints:

         */
    }


    /**
     * This method is for Unit-Testing exceptions
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun throwExceptionInAScope() {

        println("getMockResult() loading...")


        coroutineScope.launch {

            println("🙄 getMockResult() START ViewModel scope: $this, thread: ${Thread.currentThread().name}")

            delay(2000)
            throw RuntimeException("Exception Occurred")

        }

        println("getMockResult() END OF FUN")

    }


    fun getMockResultFromDispatcherThread(timeMillis: Long) {

        println("getMockResult() loading...")
        result.value = "Loading..."

        coroutineScope.launch {

            println("🙄 getMockResult() ViewModel scope: $this, thread: ${Thread.currentThread().name}")
            withContext(Dispatchers.Main) {
                result.value = generateMockNetworkResponseOrThrowException(timeMillis)
            }

            println("getMockResult() ViewModel scope AFTER")

        }

        println("getMockResult() END OF FUN")

        /*
            Prints:

         */

    }

    fun getMockResultWithTimeout() {

        val timeout = 1500L

        resultWithTimeout.value = "Loading..."

        coroutineScope.launch {

            /*
                Countdown timer to show ticks on screen
             */
            launch {
                withTimeout(timeout) {
                    // 10 is random number to show that it cancels after timeout
                    val times = 10
                    repeat(times) {
                        resultWithTimeout.value = "${times - it}"
                        delay(1000)
                    }
                }
            }

            // Get mock response with a timeout
            withTimeout(timeout) {
                try {
                    // generateMockNetworkResponse does not throw exception if timeout is not
                    // longer than 2000 ms
                    resultWithTimeout.value = generateMockNetworkResponseOrThrowException()
                } catch (exception: TimeoutCancellationException) {
                    resultWithTimeout.value = exception.message
                } catch (exception: Exception) {
                    resultWithTimeout.value = exception.message
                }

            }

        }
    }

    fun getMockResultWithRetry() {

        coroutineScope.launch {

            resultWithRetry.value = "Loading..."

            try {

                retry(
                    times = 3,
                    initialDelayMillis = 100,
                    maxDelayMillis = 2000,
                    block = {
                        // generateMockNetworkResponse throws exception for delay bigger than 2000
                        resultWithRetry.value = generateMockNetworkResponseOrThrowException(2100)
                    },
                    onError = {
                        resultWithRetry.value = "Retry count: $it"
                    }
                )

            } catch (exception: Exception) {
                resultWithRetry.value = "Exception ${exception.message}"
            }
        }
    }

    // retry with exponential backoff
    // inspired by https://stackoverflow.com/questions/46872242/how-to-exponential-backoff-retry-on-kotlin-coroutines

    @Throws(Exception::class)
    private suspend fun <T> retry(
        times: Int,
        initialDelayMillis: Long = 100,
        maxDelayMillis: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T?,
        onError: ((Int) -> Unit)? = null
    ): T? {
        var currentDelay = initialDelayMillis

        var retryCount = 0

        repeat(times) {

            try {
                retryCount++
                return block()
            } catch (exception: Exception) {
                exception.printStackTrace()

                onError?.run {
                    onError.invoke(retryCount)
                }

            }

            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)

        }

        throw RuntimeException("Failed after $times tries")
    }


    fun getMultipleResponsesConcurrently() {

        val listOfResponses = ArrayList<Deferred<String>>()


        resultMultiple.value = "Loading..."

        coroutineScope.launch {

            val startTime = System.currentTimeMillis()

            val city = async {
                generateRandomCity()
            }


            val emoji = async {
                generateRandomEmoji()
            }

            val response = async {
                generateMockNetworkResponseOrThrowException()
            }


            // 🔥 Alternative 1: Get results concurrently with deferred await functions
//            resultMultiple.value =
//                "${city.await()} - ${emoji.await()} - ${response.await()}," +
//                        " time: ${System.currentTimeMillis() - startTime}ms"

            // 🔥 Alternative 2: Use a list
            listOfResponses.add(city)
            listOfResponses.add(emoji)
            listOfResponses.add(response)

            val results = listOfResponses.awaitAll()
            resultMultiple.value =
                "${results[0]} - ${results[1]} - ${results[2]}," +
                        " time: ${System.currentTimeMillis() - startTime}ms"

        }

    }

/*
    Mock Response Functions
 */

    private suspend fun generateMockNetworkResponseOrThrowException(timeMillis: Long = 2000): String {

        println("🥶 generateMockNetworkResponse() thread: ${Thread.currentThread().name}")

        delay(timeMillis)

        if (timeMillis > 2000) throw RuntimeException("Threw Network Exception")

        return "Hello World"
    }

    private suspend fun generateRandomCity(): String {
        val cityList = listOf("Berlin", "New York", "London", "Paris", "Istanbul")

        val random = Random()

        delay(3000)
        return cityList[random.nextInt(cityList.size)]
    }

    private suspend fun generateRandomEmoji(): String {

        val emojiList = listOf("🤪", "🙄", "😛", "😭", "🤬", "🥶", "😱")
        delay(2500)

        val random = Random()
        return emojiList[random.nextInt(emojiList.size)]

    }

}
