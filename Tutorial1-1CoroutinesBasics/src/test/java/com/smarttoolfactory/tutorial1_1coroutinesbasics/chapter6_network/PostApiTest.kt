package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.PostApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.BufferedSource
import okio.Source
import okio.buffer
import okio.source
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class PostApiTest {

    private lateinit var postApi: PostApi

    private lateinit var mockWebServer: MockWebServer

    @BeforeEach
    fun setUp() {

        mockWebServer = MockWebServer()

        println("setUp() mockWebServer: $mockWebServer")

        val okHttpClient = OkHttpClient
            .Builder()
            .build()

        postApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(PostApi::class.java)

    }

    @AfterEach
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun `Request should have correct url`() = runBlockingTest {

        // GIVEN
        enqueueResponse(200, FOLDER_NAME)

        // WHEN
        postApi.getPostsResponse()

        val request = mockWebServer.takeRequest()

        // THEN
        Truth.assertThat(request.path).isEqualTo("/posts")

    }

    @Test
    fun `Given request is successful, service should return users`() = runBlockingTest {


        // GIVEN
        enqueueResponse(200, "repos.json")

        // WHEN
        val repos = postApi.getPostsResponse()

        // THEN
//        MatcherAssert.assertThat(repos.size, CoreMatchers.`is`(18))
//        MatcherAssert.assertThat(owner.login, CoreMatchers.`is`("SmartToolFactory"))
//        MatcherAssert.assertThat(
//            owner.avatarUrl,
//            CoreMatchers.`is`("https://avatars0.githubusercontent.com/u/35650605?v=4")
//        )
//        MatcherAssert.assertThat(owner.ownerId, CoreMatchers.`is`(35650605))

    }


    @Throws(IOException::class)
    private fun enqueueResponse(
        code: Int = 200,
        fileName: String,
        headers: Map<String, String>? = null
    ): MockResponse {
        // Open an InputStream to read mock json body
        val source: Source = getSource(fileName)

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
            mockResponse.setBody(
                (source as BufferedSource).readString(
                    StandardCharsets.UTF_8
                )
            )
        )

        return mockResponse
    }

    private fun getSource(fileName: String): Source {
        val inputStream: InputStream = this.javaClass.classLoader!!.getResourceAsStream(fileName)
        return inputStream.source().buffer()
    }


    inner class PostDispatcher : Dispatcher() {

        override fun dispatch(request: RecordedRequest): MockResponse {
            TODO()
        }

    }

    companion object {

        private const val FOLDER_NAME = "posts.json"
    }

}