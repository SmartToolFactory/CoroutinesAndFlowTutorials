package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source

import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDao
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import kotlinx.coroutines.flow.Flow

class LocalPostDataSourceImpl(private val postDao: PostDao) : LocalPostDataSource {

    override suspend fun getPostEntities(): List<PostEntity> {
        return postDao.getPostList()
    }

    override suspend fun savePosts(posts: List<PostEntity>): List<Long> {
        return postDao.insert(posts)
    }

    override suspend fun deletePosts(){
        postDao.deleteAll()
    }

    override fun getPostEntitiesFlow(): Flow<List<PostEntity>> {
        return postDao.getPostListFlow()
    }

}