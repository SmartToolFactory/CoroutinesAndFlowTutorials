package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source

import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import kotlinx.coroutines.flow.Flow

interface PostDataSource {

    // Suspend
    suspend fun getPostEntities(): List<PostEntity>

    // Flow
    fun getPostEntitiesFlow(): Flow<List<PostEntity>>

}

interface LocalPostDataSource : PostDataSource {

    // Suspend
    suspend fun savePosts(posts: List<PostEntity>): List<Long>
    suspend fun deletePosts()

}

interface RemotePostDataSource : PostDataSource