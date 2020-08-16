package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source

import com.smarttoolfactory.tutorial2_1flowbasics.data.api.PostApi
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDao
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostDTO
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity

interface PostDataSource

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

