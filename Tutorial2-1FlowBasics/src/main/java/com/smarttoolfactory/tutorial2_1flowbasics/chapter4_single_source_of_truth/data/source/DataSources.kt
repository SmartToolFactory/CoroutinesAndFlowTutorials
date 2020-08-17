package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source

import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth_rxjava3.data.source.LocalPostDataSourceRxJava3
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth_rxjava3.data.source.PostDataSource
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth_rxjava3.data.source.RemotePostDataSourceRxJava3
import com.smarttoolfactory.tutorial2_1flowbasics.data.api.PostApi
import com.smarttoolfactory.tutorial2_1flowbasics.data.api.PostApiRxJava
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDao
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDaoRxJava
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostDTO
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface PostDataSource

/*
    Coroutines
 */

interface RemotePostDataSource : PostDataSource {
    suspend fun getPostDTOs(): List<PostDTO>
}

interface LocalPostDataSource : PostDataSource {

    suspend fun getPostEntities(): List<PostEntity>
    suspend fun saveEntities(posts: List<PostEntity>): List<Long>
    suspend fun deletePostEntities()
}

class RemoteDataSourceImpl(private val postApi: PostApi) : RemotePostDataSource {

    override suspend fun getPostDTOs(): List<PostDTO> {
        return postApi.getPosts()
    }
}

class LocalDataSourceImpl(private val postDao: PostDao) : LocalPostDataSource {

    override suspend fun getPostEntities(): List<PostEntity> {
        return postDao.getPostList()
    }

    override suspend fun saveEntities(posts: List<PostEntity>): List<Long> {
        return postDao.insert(posts)
    }

    override suspend fun deletePostEntities() {
        postDao.deleteAll()
    }
}

/*
    RxJava3
 */

interface RemotePostDataSourceRxJava3 : PostDataSource {
    fun getPostDTOs(): Single<List<PostDTO>>
}

interface LocalPostDataSourceRxJava3 : PostDataSource {

    fun getPostEntities(): Single<List<PostEntity>>
    fun saveEntities(posts: List<PostEntity>): Completable
    fun deletePostEntities(): Completable
}

class RemoteDataSourceRxJava3Impl(private val postApi: PostApiRxJava) :
    RemotePostDataSourceRxJava3 {

    override fun getPostDTOs(): Single<List<PostDTO>> {
        return postApi.getPostsSingle()
    }
}

class LocalDataSourceRxJava3Impl(private val postDao: PostDaoRxJava) : LocalPostDataSourceRxJava3 {

    override fun getPostEntities(): Single<List<PostEntity>> {
        return postDao.getPostsSingleNonNull()
    }

    override fun saveEntities(posts: List<PostEntity>): Completable {
        return postDao.savePosts(posts)
    }

    override fun deletePostEntities(): Completable {
        return postDao.deleteAll()
    }
}

/*
    Cache
 */
class Cache(private val postDao: PostDao) {

    private val expireDuration = 30 * 60 * 1000
    private var dataSaveTime: Long = 0

    var isDirty: Boolean = System.currentTimeMillis() - getCacheSaveTime() < expireDuration

    fun saveCacheTime() {
        dataSaveTime = System.currentTimeMillis()
    }

    private fun getCacheSaveTime(): Long {
        return System.currentTimeMillis()
    }

}

