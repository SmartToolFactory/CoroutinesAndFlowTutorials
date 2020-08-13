package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.getResourceAsText
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.io.IOException


/**
 * Abstract class fo testing api with [MockWebServer] and [JUnit5]
 */
abstract class AbstractPostApiTest {

    internal lateinit var mockWebServer: MockWebServer

    private val responseAsString by lazy {
        getResourceAsText(RESPONSE_JSON_PATH)
    }

    @BeforeEach
    open fun setUp() {
        mockWebServer = MockWebServer()
        println("AbstractPostApiTest SETUP() $mockWebServer")
    }


    @AfterEach
    open fun tearDown() {
        println("AbstractPostApiTest TEARDOWN() $mockWebServer")
        mockWebServer.shutdown()
    }

    companion object {
        const val RESPONSE_JSON_PATH = "posts.json"
    }


    @Throws(IOException::class)
    fun enqueueResponse(
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

        return mockResponse
    }


}