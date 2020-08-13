package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Post
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.PostApiCoroutines
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@ExperimentalCoroutinesApi
class PostApiCoroutinesTest : AbstractPostApiTest() {

    private lateinit var postApi: PostApiCoroutines

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val okHttpClient = OkHttpClient
            .Builder()
            .build()

        postApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(PostApiCoroutines::class.java)


        Dispatchers.setMain(testCoroutineDispatcher)

    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()

        Dispatchers.resetMain()
        try {
            testCoroutineScope.cleanupTestCoroutines()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @Test
    fun `given we have a valid request, should be done to correct url`() =
        testCoroutineScope.runBlockingTest {

            // GIVEN
            launch(testCoroutineScope.coroutineContext) { enqueueResponse(200) }

            // WHEN

            // 🔥 NOT WORKING 🤨
//            postApi.getPosts()
//            advanceUntilIdle()

            // This one works WHY?
            launch(testCoroutineScope.coroutineContext) {
                postApi.getPosts()
            }

            val request = mockWebServer.takeRequest()

            // THEN
            Truth.assertThat(request.path).isEqualTo("/posts")

        }


    /**
     * ❌⚠️ This test is flaky with async or launch exist or not. It fails some of the time,
     *
     */
    @Test
    fun `given api return 200, should have list of posts`() =
        testCoroutineScope.runBlockingTest {

            // GIVEN
            val job1 = launch(testCoroutineScope.coroutineContext) {
                enqueueResponse(200)
                println("⏰ First job ${Thread.currentThread().name}")
            }


            job1.cancelAndJoin()

            // WHEN
            var posts: List<Post> = emptyList()
            val job2 = launch(testCoroutineScope.coroutineContext) {
                println("⏰ Second job START in thread: ${Thread.currentThread().name}")
                posts = postApi.getPosts()
                println("⏰ Second job END in thread: ${Thread.currentThread().name}")
            }

            job2.cancelAndJoin()


            // THEN
            println("🎃 THEN in thread: ${Thread.currentThread().name}")
            Truth.assertThat(posts).isNotNull()
            Truth.assertThat(posts.size).isEqualTo(100)

            /*
                PASSED TEST Prints:
                AbstractPostApiTest setUp() MockWebServer[-1]
                Aug 12, 2020 8:10:43 PM okhttp3.mockwebserver.MockWebServer$2 execute
                INFO: MockWebServer[63985] starting to accept connections
                ⏰ First job main @coroutine#2
                ⏰ Second job START in thread: main @coroutine#3
                🎃 THEN in thread: main @coroutine#1
                Aug 12, 2020 8:10:43 PM okhttp3.mockwebserver.MockWebServer$3 processOneRequest
                INFO: MockWebServer[63985] received request: GET /posts HTTP/1.1 and responded: HTTP/1.1 200 OK
                ⏰ Second job END in thread: OkHttp http://localhost:63985/... @coroutine#3
                Aug 12, 2020 8:10:44 PM okhttp3.mockwebserver.MockWebServer$2 acceptConnections
                INFO: MockWebServer[63985] done accepting connections: Socket closed
             */

        }


    /**
     * ❌ This test fails
     */
    @Test
    fun `given Server down, should return 500 error`() = testCoroutineScope.runBlockingTest {

        // GIVEN
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        // WHEN
        // ❌ Fails
//        val exception = assertThrows<RuntimeException> {
//            launch {
//                postApi.getPosts()
//            }
//        }

        // ❌ Also Fails
//        val exception = testCoroutineScope.async {
//            assertThrows<RuntimeException> {
//                launch {
//                    postApi.getPosts()
//                }
//            }
//        }.await()

        val exception = try {
            testCoroutineScope.async {
                postApi.getPosts()
            }.await()
            null
        } catch (e: Exception) {
            e
        }

        // THEN
        Truth.assertThat(exception?.message)
            .isEqualTo("com.jakewharton.retrofit2.adapter.rxjava2.HttpException: HTTP 500 Server Error")
    }


}