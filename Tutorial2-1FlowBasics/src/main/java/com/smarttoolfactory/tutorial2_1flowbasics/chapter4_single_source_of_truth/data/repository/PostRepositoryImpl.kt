package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository

import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source.Cache
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source.RemotePostDataSource
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source.LocalPostDataSource
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity


/**
 * Repository for persistence layer. Local Data source acts as Single Source of Truth
 * even if data is retrieved from REST api it's stored to database and retrieved from database
 * when required.
 *
 * * Get [getPostEntitiesFromLocal] function returns data that is not expired, if data is older than
 * required it uses [LocalPostDataSource] to retrieve data.
 *
 * * Other than caching this class does not contain any business
 * logic to set order of retrieving, saving or deleting data operations.
 *
 * All business logic is delegated to a UseCase/Interactor class that injects
 * this [PostRepository] as dependency.
 *
 */
class PostRepositoryImpl(
    private val localPostDataSource: LocalPostDataSource,
    private val remotePostDataSource: RemotePostDataSource,
    private val mapper: DTOtoEntityMapper,
    private val cache: Cache
) : PostRepository {

    override suspend fun getPostEntitiesFromLocal(): List<PostEntity> {
        return localPostDataSource.getPostEntities()
    }

    override suspend fun fetchEntitiesFromRemote(): List<PostEntity> {
        return mapper.map(remotePostDataSource.getPostDTOs())
    }

    override suspend fun isCacheExpired(): Boolean {
        return cache.isDirty
    }

    override suspend fun savePostEntity(postEntities: List<PostEntity>) {
        localPostDataSource.saveEntities(postEntities)
        cache.saveCacheTime()
    }

    override suspend fun deletePostEntities() {
        localPostDataSource.deletePostEntities()
    }
}