package com.smarttoolfactory.tutorial2_1flowbasics.chapter2_network

import com.smarttoolfactory.tutorial2_1flowbasics.data.api.PostApi
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PostRemoteRepository(private val postApi: PostApi) {

    fun getPostFlow(): Flow<List<PostDTO>> {
        return flow {
            // Runs in DefaultDispatcher-worker-2, or the thread flowOn runs which is below this function in UseCase class
            println("😱 PostRemoteRepository getPostFlow() thread: ${Thread.currentThread().name}")
            emit(postApi.getPosts())
        }
    }

    suspend fun getPosts() = postApi.getPosts()
}
