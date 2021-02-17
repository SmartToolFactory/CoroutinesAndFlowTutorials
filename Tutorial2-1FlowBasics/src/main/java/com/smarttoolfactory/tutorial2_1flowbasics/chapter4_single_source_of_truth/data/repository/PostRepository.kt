package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository

import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

/**
 * This repository contains no data save, delete or fetch logic with Coroutines.
 *
 * All business logic for creating offline-first or offline-last approach is moved to UseCase
 */
interface PostRepository {

    suspend fun fetchEntitiesFromRemote(): List<PostEntity>

    suspend fun getPostEntitiesFromLocal(): List<PostEntity>

    suspend fun savePostEntity(postEntities: List<PostEntity>)

    suspend fun deletePostEntities()

    suspend fun isCacheExpired(): Boolean
}

/**
 * This repository contains no data save, delete or fetch logic with RxJava3.
 *
 * All business logic for creating offline-first or offline-last approach is moved to UseCase
 */
interface PostRepositoryRxJava3 {

    fun fetchEntitiesFromRemote(): Single<List<PostEntity>>

    fun getPostEntitiesFromLocal(): Single<List<PostEntity>>

    fun savePostEntities(postEntities: List<PostEntity>): Completable

    fun deletePostEntities(): Completable

    fun isCacheExpired(): Boolean
}
