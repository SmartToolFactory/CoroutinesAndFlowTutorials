package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository

import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

interface PostRepositoryFlow {


    /**
     * Function to retrieve data with offline-first approach that uses suspend functions combined with [Flow] functions
     */
    suspend fun getPostsOfflineFirstSemiFlow(): Flow<List<PostEntity>>

    /**
     * Function to retrieve data with offline-last approach that uses suspend functions combined with [Flow] functions
     */
    suspend fun getPostsOfflineLastSemiFlow(): Flow<List<PostEntity>>

    /**
     * Function to retrieve data with offline-first approach that uses only [Flow] functions
     */
    suspend fun getPostsOfflineFirstFlow(): Flow<List<PostEntity>>

    /**
     * Function to retrieve data with offline-last approach that uses only [Flow] functions
     */
    suspend fun getPostsOfflineLastFlow(): Flow<List<PostEntity>>
}

interface PostRepositoryRxJava {

    fun getPostsOfflineFirstObservable(): Observable<List<PostEntity>>

    fun getPostsOfflineLastObservable(): Observable<List<PostEntity>>
}