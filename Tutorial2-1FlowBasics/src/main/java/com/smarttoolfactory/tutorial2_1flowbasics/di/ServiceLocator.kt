package com.smarttoolfactory.tutorial2_1flowbasics.di

import android.app.Application
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.DispatcherProvider
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository.PostRepoRxJava3Impl
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository.PostRepository
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository.PostRepositoryImpl
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository.PostRepositoryRxJava3
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source.*
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.domain.GetPostsUseCaseFlow
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.domain.GetPostsUseCaseRxJava3
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth_rxjava3.data.source.Cache
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth_rxjava3.data.source.LocalDataSourceRxJava3Impl
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth_rxjava3.data.source.LocalPostDataSourceRxJava3
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth_rxjava3.data.source.RemoteDataSourceRxJava3Impl
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth_rxjava3.data.source.RemotePostDataSourceRxJava3

import com.smarttoolfactory.tutorial2_1flowbasics.data.api.PostApi
import com.smarttoolfactory.tutorial2_1flowbasics.data.api.PostApiRxJava
import com.smarttoolfactory.tutorial2_1flowbasics.data.api.RetrofitFactory
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDao
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDaoRxJava
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.provideDatabase
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.EntityToPostMapper
import kotlinx.coroutines.Dispatchers

class ServiceLocator(private val application: Application) {

    /**
     * Rest Api Coroutines
     */
    private fun providePostApi(): PostApi = RetrofitFactory.getPostApiCoroutines()

    /**
     * Rest Api RxJava3
     */
    private fun providePostAPiRxJava3(): PostApiRxJava = RetrofitFactory.getPostApiRxJava()


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

    /**
     * Data sources with coroutines
     */
    private fun provideLocalPostDataSource(): LocalPostDataSource =
        LocalDataSourceImpl(providePostDao())

    private fun provideRemotePostDataSource(): RemotePostDataSource =
        RemoteDataSourceImpl(providePostApi())

    /**
     * Data sources with RxJava3
     */
    private fun provideLocalPostDataSourceRxJava3(): LocalPostDataSourceRxJava3 =
        LocalDataSourceRxJava3Impl(providePostDaoRxJava())

    private fun provideRemotePostDataSourceRxJava3(): RemotePostDataSourceRxJava3 =
        RemoteDataSourceRxJava3Impl(providePostAPiRxJava3())

    private fun provideCache(): Cache = Cache(providePostDao())

    /**
     * Repository with Coroutines
     */
    private fun providePostRepository(): PostRepository = PostRepositoryImpl(
        provideLocalPostDataSource(),
        provideRemotePostDataSource(),
        provideDTOtoEntityMapper(),
        provideCache()
    )

    /**
     * Repository with RxJava3
     */
    private fun providePostRepositoryRxJava3(): PostRepositoryRxJava3 = PostRepoRxJava3Impl(
        provideLocalPostDataSourceRxJava3(),
        provideRemotePostDataSourceRxJava3(),
        provideDTOtoEntityMapper(),
        provideCache()
    )

    private fun provideDispatcherWrapper(): DispatcherProvider =
        DispatcherProvider(Dispatchers.IO, Dispatchers.Default)

    /**
     * GetPostsUseCase with Coroutines
     */
    fun provideGetPostsUseCase(): GetPostsUseCaseFlow =
        GetPostsUseCaseFlow(
            providePostRepository(),
            provideEntityToPostMapper(),
            provideDispatcherWrapper()
        )

    /**
     * GetPostsUseCase with RxJava3
     */
    fun provideGetPostsUseCaseRxJava3(): GetPostsUseCaseRxJava3 =
        GetPostsUseCaseRxJava3(
            providePostRepositoryRxJava3(),
            provideEntityToPostMapper()
        )
}