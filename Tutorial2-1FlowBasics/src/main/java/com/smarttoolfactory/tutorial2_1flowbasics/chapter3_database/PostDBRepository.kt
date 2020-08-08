package com.smarttoolfactory.tutorial2_1flowbasics.chapter3_database

import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDao
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import kotlinx.coroutines.flow.Flow

class PostDBRepository(private val postDao: PostDao) {

    fun getPostFlow(): Flow<List<PostEntity>> = postDao.getPostListFlow()

    suspend fun getPosts() = postDao.getPostList()

    suspend fun getPostCount() = postDao.getPostCount()

    suspend fun savePosts(posts: List<PostEntity>) = postDao.insert(posts)
}
