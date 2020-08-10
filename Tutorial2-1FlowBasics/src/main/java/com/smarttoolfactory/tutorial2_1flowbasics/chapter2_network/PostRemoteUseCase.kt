package com.smarttoolfactory.tutorial2_1flowbasics.chapter2_network

import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.DTOtoPostMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


class PostRemoteUseCase(
    private val postRemoteRepository: PostRemoteRepository,
    private val mapper: DTOtoPostMapper
) {

    fun getPostFlow(): Flow<List<Post>> {
        return postRemoteRepository.getPostFlow()

                // 🔥 This method is just to show flowOn below changes current thread
            .map {
                // Runs in IO Thread DefaultDispatcher-worker-2
                println("⏰ PostsUseCase map() FIRST thread: ${Thread.currentThread().name}")
               it
            }
            .flowOn(Dispatchers.IO)
            .map {
                // Runs in Default Thread DefaultDispatcher-worker-1
                println("⏰ PostsUseCase map() thread: ${Thread.currentThread().name}")
                mapper.map(it)
            }
            // This is a upstream operator, does not leak downstream
            .flowOn(Dispatchers.Default)

    }

    fun getPostFlowAlt(): Flow<List<Post>> {
        return flow { emit(postRemoteRepository.getPosts()) }
            .map {
                println("⏰ PostsUseCase map() thread: ${Thread.currentThread().name}")
                mapper.map(it)
            }
            // This is a upstream operator, does not leak downstream
            .flowOn(Dispatchers.Default)
    }


}