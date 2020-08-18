package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository

import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source.*
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


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

class PostRepoRxJava3Impl(
    private val localPostDataSource: LocalPostDataSourceRxJava3,
    private val remotePostDataSource: RemotePostDataSourceRxJava3,
    private val mapper: DTOtoEntityMapper,
    private val cache: Cache
) : PostRepositoryRxJava3 {

    override fun getPostEntitiesFromLocal(): Single<List<PostEntity>> {
        return localPostDataSource.getPostEntities()
    }

    override fun fetchEntitiesFromRemote(): Single<List<PostEntity>> {
        return remotePostDataSource.getPostDTOs()
            .map {
                mapper.map(it)
            }
    }

    override fun isCacheExpired(): Boolean {
        return cache.isDirty
    }

    override fun savePostEntities(postEntities: List<PostEntity>): Completable {
        return localPostDataSource.saveEntities(postEntities).andThen(Completable.fromCallable {
            cache.saveCacheTime()
        })
    }

    override fun deletePostEntities(): Completable {
        return localPostDataSource.deletePostEntities()
    }
}
