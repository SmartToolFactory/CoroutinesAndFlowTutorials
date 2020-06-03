package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.DataResult
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Post
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.PostApiCoroutines
import retrofit2.Call

class PostsRepository(private val postApi: PostApiCoroutines) {

    fun getPostsWithCall(): Call<List<Post>> {
        return postApi.getPostsWithCall()
    }

    suspend fun getPostResult(): DataResult<List<Post>> {

        // Using List<Post>
        return try {
            DataResult.Success(postApi.getPosts())
        } catch (error: Exception) {
            DataResult.Error(error)
        }

    }

    suspend fun getPostResultFromResponse(): DataResult<List<Post>> {

        // Using retrofit2.Response<List<Post>>
        return try {
            val response = postApi.getPostsResponse()
            if (response.isSuccessful) {
                DataResult.Success(response.body()!!)
            } else {
                DataResult.Error(HttpException(response))
            }
        } catch (error: Exception) {
            DataResult.Error(error)
        }

    }

}
