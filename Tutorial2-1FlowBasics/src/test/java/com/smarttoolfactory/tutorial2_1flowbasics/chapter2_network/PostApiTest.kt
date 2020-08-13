package com.smarttoolfactory.tutorial2_1flowbasics.chapter2_network

import com.google.common.truth.Truth
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.smarttoolfactory.tutorial2_1flowbasics.base.AbstractPostApiTest
import com.smarttoolfactory.tutorial2_1flowbasics.data.api.PostApi
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostDTO
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection


class PostApiTest : AbstractPostApiTest() {

    /**
     * Api is the SUT to test headers, url, response and DTO objects
     */
    private lateinit var api: PostApi


    @BeforeEach
    override fun setUp() {
        super.setUp()

        val okHttpClient = OkHttpClient
            .Builder()
            .build()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
            .create(PostApi::class.java)
    }


    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    /**
     * Calling [Job.join] causes code below coroutine to suspend until the job that calls join
     * is complete.
     */
    @Test
    fun testLaunchBuilderSequence() = testCoroutineScope.runBlockingTest {

        val j = Job()

        val job1 = launch(testCoroutineScope.coroutineContext) {
            delay(700)
            println("⏰ First job ${Thread.currentThread().name}")
        }

        job1.join()

        val job2 = launch(testCoroutineScope.coroutineContext) {
            delay(600)
            println("⏰ Second job ${Thread.currentThread().name}")
        }

        job2.join()


        launch(testCoroutineScope.coroutineContext) {
            println("🎃 THEN ${Thread.currentThread().name}")
        }
    }

    @Test
    fun `Given we have a valid request, should be done to correct url`() =
        testCoroutineScope.runBlockingTest {

            // GIVEN
            launch(testCoroutineScope.coroutineContext) {
                enqueueResponse(HttpURLConnection.HTTP_OK)
                println("⏰ First job ${Thread.currentThread().name}")
            }

            // WHEN
            launch(testCoroutineScope.coroutineContext) {
                println("⏰ Second job START in thread: ${Thread.currentThread().name}")
                api.getPosts()
                println("⏰ Second job END in thread: ${Thread.currentThread().name}")
            }

            advanceUntilIdle()

//            launch(testCoroutineScope.coroutineContext) {
            val request = mockWebServer.takeRequest()

            // THEN
            println("🎃 THEN in thread: ${Thread.currentThread().name}")
            Truth.assertThat(request.path).isEqualTo("/posts")
//            }

        }


    @Test
    fun `Given api return 200, should have list of posts`() = testCoroutineScope.runBlockingTest {

        // GIVEN
        val job1 = launch(this.coroutineContext) {
            enqueueResponse(HttpURLConnection.HTTP_OK)
            println("⏰ First job ${Thread.currentThread().name}")
        }

        // WHEN
        var postList: List<PostDTO> = emptyList()

        val job2 = launch(this.coroutineContext) {
            println("⏰ Second job START in thread: ${Thread.currentThread().name}")
            postList = api.getPosts()
            println("⏰ Second job END in thread: ${Thread.currentThread().name}")
        }

        advanceUntilIdle()

        // THEN
        println("🎃 THEN in thread: ${Thread.currentThread().name}")
        Truth.assertThat(postList).isNotNull()
        Truth.assertThat(postList?.size).isEqualTo(100)

    }

    /**
     * Alternative method with [TestObserver] to test 200 code response same
     * as the one above
     */
    @Test
    fun `Given api return 200, should have list of posts with testObserver`() =
        testCoroutineScope.runBlockingTest {


        }

}