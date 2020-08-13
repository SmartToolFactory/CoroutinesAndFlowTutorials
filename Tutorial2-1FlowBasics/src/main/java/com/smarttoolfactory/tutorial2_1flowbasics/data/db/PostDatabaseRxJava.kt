package com.smarttoolfactory.tutorial2_1flowbasics.data.db

import androidx.room.Dao
import androidx.room.Query
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single


@Dao
interface PostDaoRxJava {

    /*
        ***** RxJava ******
     */

    @Query("SELECT * FROM post")
    fun getPostsSingleNonNull(): Single<List<PostEntity>>

    /**
     * Get list of [PostEntity]s to from database.
     */
    @Query("SELECT * FROM post")
    fun getPostsMaybe(): Maybe<List<PostEntity>>


    /**
     * Get list of [PostEntity]s to from database.
     */
    @Query("SELECT * FROM post")
    fun getPosts(): Observable<List<PostEntity>>

    /*
        Get only one post using id
     */

    /**
     * When there is no entities in the database and the query returns no rows, Single will trigger onError(EmptyResultSetException.class)
     *
     * * If `onError` is NOT used with `subscribe` throws EmptyResultSetException and app **crashes**
     *
     * * If `onError` is used with `subscribe` returns error
     * `androidx.room.EmptyResultSetException: Query returned empty result set: SELECT * FROM post WHERE id=?`
     */
    @Query("SELECT * FROM post WHERE id=:postId")
    fun getPostByIdSingle(postId: Int): Single<PostEntity>

    /**
     * When there is NO entity in the database and the query returns no rows, Maybe will complete.
     *
     * * Neither `onError`, nor `doOnError` will be called. It will complete only with `onComplete` call
     */
    @Query("SELECT * FROM post WHERE id=:postId")
    fun getPostByIdMaybe(postId: Int): Maybe<PostEntity>

    /**
     * WHen there is NO entity in the database **NOTHING** will be called. Simply nothing!
     */
    @Query("SELECT * FROM post WHERE id=:postId")
    fun getPostById(postId: Int): Observable<PostEntity>

    /*
        Get one post inside a list using id
     */
    @Query("SELECT * FROM post WHERE id=:postId")
    fun getPostListByIdSingle(postId: Int): Single<List<PostEntity>>

    @Query("SELECT * FROM post WHERE id=:postId")
    fun getPostListByIdMaybe(postId: Int): Maybe<List<PostEntity>>

    @Query("SELECT * FROM post WHERE id=:postId")
    fun getPostListById(postId: Int): Observable<List<PostEntity>>

}

