package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository

import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import kotlinx.coroutines.flow.Flow




interface PostRepositoryRxJava {


}

/**
 * This repository contains no data save, delete or retieve logic. All business logic for creating offline-first
 * or offline-last approach is moved to UseCase
 */
interface PostRepository {

    suspend fun getPostEntitiesFromLocal(): List<PostEntity>

    suspend fun fetchEntitiesFromRemote(): List<PostEntity>

    suspend fun isCacheExpired():Boolean

    suspend fun savePostEntity(postEntities: List<PostEntity>)

    suspend fun deletePostEntities()

}