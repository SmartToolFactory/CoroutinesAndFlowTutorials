package com.smarttoolfactory.tutorial2_1flowbasics.di

import android.app.Application
import com.smarttoolfactory.tutorial2_1flowbasics.data.api.PostApi
import com.smarttoolfactory.tutorial2_1flowbasics.data.api.RetrofitFactory
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.PostDao
import com.smarttoolfactory.tutorial2_1flowbasics.data.db.provideDatabase
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository.PostRepositoryFlow
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository.PostRepositoryImpl
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source.LocalPostDataSource
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source.LocalPostDataSourceImpl
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source.RemotePostDataSource
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.source.RemotePostDataSourceImpl

class ServiceLocator(private val application: Application) {

    fun providePostDao(): PostDao {
        return provideDatabase(application).postDao()
    }

    fun providePostApi(): PostApi {
        return RetrofitFactory.getPostApiCoroutines()
    }

    fun provideDTOtoEntityMapper(): DTOtoEntityMapper {
        return DTOtoEntityMapper()
    }

    fun provideLocalPostDataSource(): LocalPostDataSource {
        return LocalPostDataSourceImpl(providePostDao())
    }

    fun provideRemotePostDataSource(): RemotePostDataSource {
        return RemotePostDataSourceImpl(providePostApi(), provideDTOtoEntityMapper())
    }

    fun providePostRepository(): PostRepositoryFlow {
        return PostRepositoryImpl(
            provideLocalPostDataSource(),
            provideRemotePostDataSource()

        )
    }
}