package com.smarttoolfactory.tutorial2_1flowbasics.chapter3_database

import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDao
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class PostDBRepository(private val postDao: PostDao) {

    fun getPostListFlow(): Flow<List<PostEntity>> {
        println("🤔 PostDBRepository getPostFlow() thread: ${Thread.currentThread().name}")
      return  postDao.getPostListFlow().map {
          println("🤔 PostDBRepository getPostFlow() MAP thread: ${Thread.currentThread().name}")
          it
      }
    }

     fun getPostByIdFlow(id:Int) =postDao.getPostFlow(id)

    suspend fun getPostList() = postDao.getPostList()

    suspend fun getPostById(id: Int) = postDao.getPost(id)

    suspend fun getPostCount() = postDao.getPostCount()

    suspend fun savePosts(posts: List<PostEntity>) = postDao.insert(posts)
}
