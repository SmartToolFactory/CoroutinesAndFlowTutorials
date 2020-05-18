package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.DataResult
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Post
import retrofit2.Call

class PostsUseCase(private val postsRepository: PostsRepository) {

    fun getPostsWithCall(): Call<List<Post>> {
        return postsRepository.getPostsWithCall()
    }

    suspend fun getPosts(): DataResult<List<Post>> {
        return postsRepository.getPostResult()
    }
}