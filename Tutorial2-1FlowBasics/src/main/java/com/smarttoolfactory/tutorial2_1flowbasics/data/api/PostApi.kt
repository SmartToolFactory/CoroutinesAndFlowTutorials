package com.smarttoolfactory.tutorial2_1flowbasics.data.api


import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostDTO
import io.reactivex.rxjava3.core.Observable

import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface PostApi {

    @GET("/posts")
    suspend fun getPosts(): List<PostDTO>

}

interface PostApiRxJava : PostApi {
    @GET("/posts")
    fun getPostsAsObservable(): Observable<List<PostDTO>>
}


object RetrofitFactory {

    private const val BASE_URL = "https://jsonplaceholder.typicode.com"

    fun getPostApiCoroutines(): PostApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostApi::class.java)
    }


    fun getPostApiRxJava(): PostApiRxJava {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(PostApiRxJava::class.java)
    }


}