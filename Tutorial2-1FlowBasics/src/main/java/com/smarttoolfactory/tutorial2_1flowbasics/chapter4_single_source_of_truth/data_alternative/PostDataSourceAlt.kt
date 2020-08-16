//package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source
//
//import com.smarttoolfactory.tutorial2_1flowbasics.data.api.PostApi
//import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDao
//import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.DTOtoEntityMapper
//import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
//
//interface PostDataSource {
//    suspend fun getPostEntities(): List<PostEntity>
//
//}
//
//interface RemotePostDataSource : PostDataSource
//
//interface LocalPostDataSource : PostDataSource {
//    suspend fun savePosts(posts: List<PostEntity>): List<Long>
//    suspend fun deletePosts()
//}
//
//class RemotePostDataSourceImpl(
//    private val postApi: PostApi,
//    private val mapper: DTOtoEntityMapper
//) : RemotePostDataSource {
//
//    override suspend fun getPostEntities(): List<PostEntity> {
//        return mapper.map(postApi.getPosts())
//    }
//}
//
//class LocalPostDataSourceImpl(private val postDao: PostDao) : LocalPostDataSource {
//
//    override suspend fun getPostEntities(): List<PostEntity> {
//        return postDao.getPostList()
//    }
//
//    override suspend fun savePosts(posts: List<PostEntity>): List<Long> {
//        return postDao.insert(posts)
//    }
//
//    override suspend fun deletePosts() {
//        postDao.deleteAll()
//    }
//}