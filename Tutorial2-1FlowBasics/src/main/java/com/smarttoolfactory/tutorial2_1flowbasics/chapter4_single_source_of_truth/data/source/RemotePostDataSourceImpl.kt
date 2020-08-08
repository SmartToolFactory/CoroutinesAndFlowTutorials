package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source

import com.smarttoolfactory.tutorial2_1flowbasics.data.api.PostApi
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostDTO
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class RemotePostDataSourceImpl(
    private val postApi: PostApi,
    private val mapper: DTOtoEntityMapper
) : RemotePostDataSource {


    override suspend fun getPostEntities(): List<PostEntity> {
        return mapper.map(postApi.getPosts())
    }

    override fun getPostEntitiesFlow(): Flow<List<PostEntity>> {
        return flow<List<PostDTO>> { postApi.getPosts() }
            .map {
                mapper.map(it)
            }
    }
}