package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface PostApi {

    /**
     * This request is invoked in it's own thread when [suspend] is used
     */
    @GET("/posts")
    suspend fun getPosts(): Response<List<Post>>

    @GET("/posts")
     fun getPostsWithCall(): Call<List<Post>>
}


object RetrofitFactory {

    private const val BASE_URL = "https://jsonplaceholder.typicode.com"

    fun makeRetrofitService(): PostApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(PostApi::class.java)
    }
}