package com.smarttoolfactory.tutorial2_1flowbasics.chapter3_database

import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.EntityToPostMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.PostToEntityMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


class PostDBUseCase(
    private val postDBRepository: PostDBRepository,
    private val entityToPostMapper: EntityToPostMapper,
    private val postToEntityMapper: PostToEntityMapper
) {

    suspend fun savePosts(post: List<Post>) {
        postDBRepository.savePosts(postToEntityMapper.map(post))
    }

    suspend fun getPostCount() = postDBRepository.getPostCount()

    fun getPostFlow(): Flow<List<Post>> {
        return postDBRepository.getPostFlow()
            .map {
                println("⏰ PostsUseCase map() thread: ${Thread.currentThread().name}")
                entityToPostMapper.map(it)
            }
            // This is a upstream operator, does not leak downstream
            .flowOn(Dispatchers.Default)
    }

    fun getPostFlowAlt(): Flow<List<Post>> {
        return flow { emit(postDBRepository.getPosts()) }
            .map {
                println("⏰ PostsUseCase map() thread: ${Thread.currentThread().name}")
                entityToPostMapper.map(it)
            }
            // This is a upstream operator, does not leak downstream
            .flowOn(Dispatchers.Default)
    }
}