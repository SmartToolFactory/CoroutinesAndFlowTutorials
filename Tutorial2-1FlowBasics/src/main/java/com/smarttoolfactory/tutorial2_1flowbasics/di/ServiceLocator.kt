package com.smarttoolfactory.tutorial2_1flowbasics.di

import android.app.Application
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.DispatcherProvider
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository.PostRepository
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository.PostRepositoryImpl
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source.*
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.domain.GetPostsUseCase

import com.smarttoolfactory.tutorial2_1flowbasics.data.api.PostApi
import com.smarttoolfactory.tutorial2_1flowbasics.data.api.RetrofitFactory
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDao
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDaoRxJava
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.provideDatabase
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.EntityToPostMapper
import kotlinx.coroutines.Dispatchers

class ServiceLocator(private val application: Application) {

    /**
     * Rest Api
     */
    private fun providePostApi(): PostApi = RetrofitFactory.getPostApiCoroutines()

    /**
     * Post Dao with suspend and Flow functions
     */
    fun providePostDao(): PostDao = provideDatabase(application).postDao()

    /**
     * Post Dao with RxJava functions
     */
    fun providePostDaoRxJava(): PostDaoRxJava = provideDatabase(application).postDaoRxJava()


    private fun provideDTOtoEntityMapper(): DTOtoEntityMapper = DTOtoEntityMapper()

    private fun provideEntityToPostMapper(): EntityToPostMapper = EntityToPostMapper()

    private fun provideLocalPostDataSource(): LocalPostDataSource =
        LocalDataSourceImpl(providePostDao())

    private fun provideRemotePostDataSource(): RemotePostDataSource =
        RemoteDataSourceImpl(providePostApi())

    private fun provideCache(): Cache = Cache(providePostDao())

    private fun providePostRepository(): PostRepository = PostRepositoryImpl(
        provideLocalPostDataSource(),
        provideRemotePostDataSource(),
        provideDTOtoEntityMapper(),
        provideCache()
    )

    private fun provideDispatcherWrapper(): DispatcherProvider =
        DispatcherProvider(Dispatchers.IO, Dispatchers.Default)

    fun provideGetPostsUseCase(): GetPostsUseCase =
        GetPostsUseCase(
            providePostRepository(),
            provideEntityToPostMapper(),
            provideDispatcherWrapper()
        )
}