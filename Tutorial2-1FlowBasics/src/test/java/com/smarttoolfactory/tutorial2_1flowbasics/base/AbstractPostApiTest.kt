package com.smarttoolfactory.tutorial2_1flowbasics.base

import com.smarttoolfactory.tutorial2_1flowbasics.convertFromJsonToObjectList
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostDTO
import com.smarttoolfactory.tutorial2_1flowbasics.getResourceAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.io.IOException

/**
 * Abstract class fo testing api with [MockWebServer] and [JUnit5]
 */
abstract class AbstractPostApiTest {

    val testCoroutineDispatcher = TestCoroutineDispatcher()
    val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)


    lateinit var mockWebServer: MockWebServer

    private val responseAsString by lazy {
        getResourceAsText("response.json")
    }

    private val postList by lazy {
        convertFromJsonToObjectList<PostDTO>(getResourceAsText("response.json"))!!
    }

    @BeforeEach
    open fun setUp() {

        println("AbstractPostApiTest SET UP")

        // provide the scope explicitly, in this example using a constructor parameter
        Dispatchers.setMain(testCoroutineDispatcher)

        mockWebServer = MockWebServer()
//            .apply {
//                setDispatcher(PostHeaderDispatcher())
//            }
    }

    @AfterEach
    open fun tearDown() {

        println("AbstractPostApiTest TEAR DOWN")

        Dispatchers.resetMain()
        try {
            testCoroutineDispatcher.cleanupTestCoroutines()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mockWebServer.shutdown()
    }

    @Throws(IOException::class)
    suspend fun enqueueResponse(
        code: Int = 200,
        headers: Map<String, String>? = null
    ): MockResponse {

        // Define mock response
        val mockResponse = MockResponse()
        // Set response code
        mockResponse.setResponseCode(code)

        // Set headers
        headers?.let {
            for ((key, value) in it) {
                mockResponse.addHeader(key, value)
            }
        }

        // Set body
        mockWebServer.enqueue(
            mockResponse.setBody(responseAsString)
        )
        println("🍏 enqueueResponse() ${Thread.currentThread().name} ${responseAsString.length}")
        return mockResponse
    }

//    inner class PostHeaderDispatcher : QueueDispatcher() {
//
//        override fun dispatch(request: RecordedRequest): MockResponse {
//            return if (request.getHeader("Build") != null) {
//                enqueueResponse(HttpURLConnection.HTTP_OK)
//            } else {
//                MockResponse()
//                    .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
//                    .setBody("SERVER_INTERNAL_ERROR_MESSAGE")
//            }
//        }
//
//    }
}