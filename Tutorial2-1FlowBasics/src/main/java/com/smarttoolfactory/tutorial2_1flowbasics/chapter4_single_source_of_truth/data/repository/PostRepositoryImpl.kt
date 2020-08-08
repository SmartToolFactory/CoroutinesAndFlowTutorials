package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository

import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source.LocalPostDataSource
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source.RemotePostDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class PostRepositoryImpl(
    private val localPostDataSource: LocalPostDataSource,
    private val remotePostDataSource: RemotePostDataSource
) : PostRepositoryFlow {

    /**
     * Steps to get data with offline-first approach
     *
     * * Check out local data source
     * * If it's not empty emit data
     * * If it's empty check out remote data source
     * * Delete old data from db
     * * Save new data to db
     * * Get new data from db
     */
    override suspend fun getPostsOfflineFirstSemiFlow(): Flow<List<PostEntity>> = TODO()
//        flow {
//            emit(localPostDataSource.getPostEntities())
//        }
//            .flowOn(Dispatchers.Default)
//            // FlatMap in RxJava
//            .flatMapMerge { postEntities ->
//
//                if (postEntities.isNullOrEmpty()) {
//
//                    mapper.map(remotePostDataSource.getPostDTOs()).apply {
//                        localPostDataSource.deletePosts()
//                        localPostDataSource.savePosts(this)
//                    }
//
//                    flowOf(localPostDataSource.getPostEntities())
//
//                } else {
//                    flowOf(postEntities)
//                }
//            }
//            .catch { emit(listOf()) }


    override suspend fun getPostsOfflineLastSemiFlow(): Flow<List<PostEntity>> = flow {
        emit(remotePostDataSource.getPostEntities())
    }
        .flowOn(Dispatchers.Default)
        .map { postEntities ->

            localPostDataSource.run {
                deletePosts()
                savePosts(postEntities)
                getPostEntities()
            }

        }
        .catch { localPostDataSource.getPostEntities() }

    override suspend fun getPostsOfflineFirstFlow(): Flow<List<PostEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPostsOfflineLastFlow(): Flow<List<PostEntity>> {
        TODO("Not yet implemented")
    }

}