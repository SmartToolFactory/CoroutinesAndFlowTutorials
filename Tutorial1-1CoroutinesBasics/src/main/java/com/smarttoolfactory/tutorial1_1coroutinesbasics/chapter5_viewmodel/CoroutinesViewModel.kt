package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter5_viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

/**
 * ViewModel class for using coroutines to execute simple tasks with delay, timeout and retry logic
 *
 * * **viewModelScope** uses SupervisorJob() + Dispatchers.Main.immediate
 *
 * * [generateMockNetworkResponseOrThrowException] function has a timeout which is 2000ms after that timeout
 * it throws a [RuntimeException] to test for retry functionality. Unless timeout is set(it's 200ms)
 * it returns a mock String.
 *
 */
class CoroutinesViewModel(private val viewModelDispatcher: CoroutineDispatcher) : ViewModel() {

    val result = MutableLiveData<String>()
    val enableResultButton = MutableLiveData<Boolean>().apply {
        value = true
    }

    val resultWithRetry = MutableLiveData<String>()
    val enableRetryButton = MutableLiveData<Boolean>().apply {
        value = true
    }

    val resultWithTimeout = MutableLiveData<String>()
    val enableWithTimeoutButton = MutableLiveData<Boolean>().apply {
        value = true
    }

    val resultMultiple = MutableLiveData<String>()
    val enableMultipleButton = MutableLiveData<Boolean>().apply {
        value = true
    }

    fun getMockResult(timeMillis: Long = 2000) {

        println("1 - getMockResult() loading...")
        result.value = "Loading..."
        enableResultButton.value = false

        viewModelScope.launch {

            println("🙄 2 - getMockResult() START ViewModel scope: $this, thread: ${Thread.currentThread().name}")

            result.value = generateMockNetworkResponseOrThrowException(timeMillis)  // 4
            enableResultButton.value = true

            println("5 - getMockResult() ViewModel scope AFTER generateMockNetworkResponse() ${result.value}")

        }

        println("3 - getMockResult() END OF FUN")

        /*   1 2 4 3 5
            Prints:
            I: getMockResult() loading...
            I: 🙄 getMockResult() ViewModel scope: StandaloneCoroutine{Active}@ef7c60d, thread: main
            I: 🥶 generateMockNetworkResponse() thread: main
            I: getMockResult() END OF FUN
            I: getMockResult() ViewModel scope AFTER generateMockNetworkResponse
         */

    }

    /*
        Mock Response Functions
     */

    private suspend fun generateMockNetworkResponseOrThrowException(timeMillis: Long = 2000): String {

        println("🥶 4 - generateMockNetworkResponse() thread: ${Thread.currentThread().name}")

        delay(timeMillis)

        if (timeMillis > 2000) throw RuntimeException("Threw Network Exception")

        return "Hello World"
    }

    fun getMockResultFromDispatcherThread(timeMillis: Long) {

        println("1 - getMockResult() loading...")
        result.value = "Loading..."
        enableResultButton.value = false

        viewModelScope.launch(Dispatchers.Default) {

            println("🙄 3 - getMockResult() ViewModel scope: $this, thread: ${Thread.currentThread().name}")

            withContext(Dispatchers.Main) {
                result.value = generateMockNetworkResponseOrThrowException(timeMillis) // 4
                enableResultButton.value = true

            }

            println("5 - getMockResult() ViewModel scope AFTER")

        }

        println("2 - getMockResult() END OF FUN")

        /*
            Prints:  1 3 2 4 5   or 1 2 3 4 5

         */

    }

    fun getMockResultWithTimeout() {

        val timeout = 1500L

        resultWithTimeout.value = "Loading..."

        viewModelScope.launch(viewModelDispatcher) {

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
                    resultWithTimeout.value = "1 - " + exception.message
                } catch (exception: Exception) {
                    resultWithTimeout.value = "2 - " + exception.message
                }

            }

        }
    }

    fun getMockResultWithRetry() {

        enableRetryButton.value = false

        viewModelScope.launch(viewModelDispatcher) {

            resultWithRetry.value = "Loading..."

            try {

                retry(
                    times = 3,
                    initialDelayMillis = 100,
                    maxDelayMillis = 2000,
                    block = {
                        // generateMockNetworkResponse throws exception for delay bigger than 2000
                        resultWithRetry.value = generateMockNetworkResponseOrThrowException(2100)
                        enableRetryButton.value = true
                    },
                    onError = {
                        resultWithRetry.value = "Retry count: $it"
                    }
                )

            } catch (exception: Exception) {
                resultWithRetry.value = "Exception ${exception.message}"
                enableRetryButton.value = true
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
        enableMultipleButton.value = false

        viewModelScope.launch {

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

            enableMultipleButton.value = true

        }

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

    /**
     * This method is for Unit-Testing exceptions
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun throwExceptionInAScope(coroutineContext: CoroutineContext) {

        println("getMockResult() loading...")

        // 🔥🔥
        viewModelScope.launch(coroutineContext) {

            println("🙄 getMockResult() START ViewModel scope: $this, thread: ${Thread.currentThread().name}")

            delay(2000)
            throw RuntimeException("Exception Occurred")

        }

        println("getMockResult() END OF FUN")

    }
}

class CoroutinesViewModelFactory :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return CoroutinesViewModel(Dispatchers.Main) as T
    }

}