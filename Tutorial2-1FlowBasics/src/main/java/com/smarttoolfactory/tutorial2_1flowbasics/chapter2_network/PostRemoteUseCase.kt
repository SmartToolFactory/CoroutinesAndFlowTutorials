package com.smarttoolfactory.tutorial2_1flowbasics.chapter2_network

import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.DTOtoPostMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*


class PostRemoteUseCase(
    private val postRemoteRepository: PostRemoteRepository,
    private val mapper: DTOtoPostMapper
) {

    /**
     * 🔥 This function FAILS EXCEPTION TEST due to
     * `.flowOn(Dispatchers.IO)` using Dispatchers.IO instead of same thread
     */
    fun getPostFlow(): Flow<List<Post>> {
        return postRemoteRepository.getPostFlow()

            // 🔥 This method is just to show flowOn below changes current thread
            .map {
                // Runs in IO Thread DefaultDispatcher-worker-2
                println("⏰ PostsUseCase map() FIRST thread: ${Thread.currentThread().name}")
                it
            }
            .flowOn(Dispatchers.Main)
            .flatMapConcat {
                println("😍 PostsUseCase flatMapConcat()  thread: ${Thread.currentThread().name}")
                flow{emit((it))}
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


    fun getPostFlowSimple(): Flow<List<Post>> {
        return postRemoteRepository.getPostFlow()
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