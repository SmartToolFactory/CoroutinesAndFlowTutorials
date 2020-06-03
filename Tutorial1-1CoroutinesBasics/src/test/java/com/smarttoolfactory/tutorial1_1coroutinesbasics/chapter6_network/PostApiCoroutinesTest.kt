package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import com.google.common.truth.Truth
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Post
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.PostApiCoroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
    fun `Given we have a valid request, should be done to correct url`() =
        testCoroutineScope.runBlockingTest {

            // GIVEN
            enqueueResponse(200)

            // WHEN

            // 🔥 NOT WORKING 🤨
//            postApi.getPosts()
//            advanceUntilIdle()

            // This one works WHY?
            launch {
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
    fun `Given api return 200, should have list of posts`() =
        testCoroutineScope.runBlockingTest {

            // GIVEN
            async { enqueueResponse(200) }.await()

            // WHEN
            var posts: List<Post> = emptyList()
            launch {
                posts = postApi.getPosts()
            }

            advanceUntilIdle()

            // THEN
            Truth.assertThat(posts).isNotNull()
            Truth.assertThat(posts.size).isEqualTo(100)

        }


    /**
     * ❌ This test fails
     */
    @Test
    fun `Given Server down, should return 500 error`() = testCoroutineScope.runBlockingTest {

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
        val exception = testCoroutineScope.async {
            assertThrows<RuntimeException> {
                launch {
                    postApi.getPosts()
                }
            }
        }.await()


        // THEN
        Truth.assertThat(exception.message)
            .isEqualTo("com.jakewharton.retrofit2.adapter.rxjava2.HttpException: HTTP 500 Server Error")
    }


}