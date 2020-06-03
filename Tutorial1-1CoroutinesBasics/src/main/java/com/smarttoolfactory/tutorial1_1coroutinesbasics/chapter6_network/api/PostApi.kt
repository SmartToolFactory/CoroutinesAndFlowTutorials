package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface PostApi

interface PostApiCoroutines: PostApi {

    /**
     * This request is invoked in it's own thread when [suspend] is used
     */
    @GET("/posts")
    suspend fun getPostsResponse(): Response<List<Post>>

    @GET("/posts")
    suspend fun getPosts(): List<Post>

    @GET("/posts")
    fun getPostsWithCall(): Call<List<Post>>

}

/**
 * This is for writing REST api test with RxJava to test [MockWebServer]
 */
interface PostApiRxJava: PostApi {
    @GET("/posts")
    fun getPostsAsObservable(): Observable<List<Post>>
}

object RetrofitFactory {

    private const val BASE_URL = "https://jsonplaceholder.typicode.com"

    fun getPostApiCoroutines(): PostApiCoroutines {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostApiCoroutines::class.java)
    }

    /**
     * Returns RxJava counterpart of coroutines REST api, not required for this example,
     * but you can try it if you wish
     */
    fun getPostApiRxJava(): PostApiRxJava {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(PostApiRxJava::class.java)
    }
}