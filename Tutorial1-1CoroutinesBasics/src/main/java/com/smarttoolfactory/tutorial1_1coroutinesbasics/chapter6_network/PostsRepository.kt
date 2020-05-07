package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.DataResult
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.PostApi
import com.smarttoolfactory.tutorial1_1coroutinesbasics.model.Post
import retrofit2.Call
import retrofit2.HttpException

class PostsRepository(private val postApi: PostApi) {

    fun getPostsWithCall(): Call<List<Post>> {
        return postApi.getPostsWithCall()
    }

    suspend fun getPosts(): DataResult<List<Post>> {

        return try {
            val response = postApi.getPosts()
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
