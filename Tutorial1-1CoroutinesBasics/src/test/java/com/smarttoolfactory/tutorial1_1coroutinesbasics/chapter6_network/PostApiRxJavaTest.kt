package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import com.google.common.truth.Truth
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Post
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.PostApiRxJava
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostApiRxJavaTest : AbstractPostApiTest() {

    private lateinit var api: PostApiRxJava

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
            .create(PostApiRxJava::class.java)
    }


    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun `Given we have a valid request, should be done to correct url`() {

        @Test
        fun `Request has correct url`() {

            // GIVEN
            enqueueResponse(200)

            // WHEN
            api.getPostsAsObservable()
                .blockingFirst()
            val request = mockWebServer.takeRequest()

            // THEN
            Truth.assertThat(request.path).isEqualTo("/posts")

        }

    }

    @Test
    fun `Given api return 200, should have list of posts with blockingFirst`() {

        // GIVEN
        enqueueResponse(200)

        // WHEN
        val posts = api.getPostsAsObservable()
            .blockingFirst()

        // THEN
        Truth.assertThat(posts).isNotNull()
        Truth.assertThat(posts.size).isEqualTo(100)

    }

    /**
     * Alternative method with [TestObserver] to test 200 code response same
     * as the one above
     */
    @Test
    fun `Given api return 200, should have list of posts with testObserver`() {


        // GIVEN
        enqueueResponse(200)
        val testObserver = TestObserver<List<Post>>()

        // WHEN
        val observable = api.getPostsAsObservable()

        //Assert no subscription has occurred yet
        testObserver.assertNotSubscribed()

        //Subscribe TestObserver to source
        observable.subscribe(testObserver)

        // THEN
        // Subscribes here
        //Assert TestObserver is subscribed
        testObserver.assertSubscribed()

        //🔥🔥 Block and wait for Observable to terminate
        testObserver.awaitTerminalEvent()

        //Assert TestObserver called onComplete()
        testObserver.assertComplete()

        //Assert there were no errors
        testObserver.assertNoErrors()

        //Assert 1 list is received
        testObserver.assertValueCount(1)

        val posts = testObserver.values()[0]
        Truth.assertThat(posts.size).isEqualTo(100)

        // ⚠️ Do not forget to dispose TestObserver same as other observables
        testObserver.dispose()

    }

    @Test
    fun `Given Server down, should return 500 error`() {

        // GIVEN
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        // WHEN
        val exception = assertThrows<RuntimeException> {
            api.getPostsAsObservable().blockingFirst()
        }

        // THEN
        Truth.assertThat(exception.message)
            .isEqualTo("com.jakewharton.retrofit2.adapter.rxjava2.HttpException: HTTP 500 Server Error")
    }

}