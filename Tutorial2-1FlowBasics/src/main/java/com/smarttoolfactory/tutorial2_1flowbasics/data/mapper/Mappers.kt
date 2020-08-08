package com.smarttoolfactory.tutorial2_1flowbasics.data.mapper

import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostDTO
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity

/**
 * Mapper for transforming objects between REST and database or REST/db and domain
 *  which are Non-nullable to Non-nullable
 */
interface Mapper<I, O> {
    fun map(input: I): O
}

/**
 * Mapper for transforming objects between REST and database or REST/db and domain
 * as [List] which are Non-nullable to Non-nullable
 */
interface ListMapper<I, O> : Mapper<List<I>, List<O>>


class DTOtoEntityMapper : ListMapper<PostDTO, PostEntity> {

    override fun map(input: List<PostDTO>): List<PostEntity> {
        return input.map {
            PostEntity(it.id, it.userId, it.title, it.body)
        }
    }

}

class EntityToPostMapper : ListMapper<PostEntity, Post> {
    override fun map(input: List<PostEntity>): List<Post> {
        return input.map {
            Post(it.id, it.userId, it.title, it.body)
        }
    }
}

class PostToEntityMapper : ListMapper<Post, PostEntity> {
    override fun map(input: List<Post>): List<PostEntity> {
        return input.map {
            PostEntity(it.id, it.userId, it.title, it.body)
        }
    }
}

class DTOtoPostMapper : ListMapper<PostDTO, Post> {

    override fun map(input: List<PostDTO>): List<Post> {
        return input.map {
            Post(it.id, it.userId, it.title, it.body)
        }
    }

}