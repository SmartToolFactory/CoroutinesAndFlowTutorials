package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.domain

import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository.PostRepositoryRxJava3
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.EntityToPostMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Status
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.ViewState
import com.smarttoolfactory.tutorial2_1flowbasics.util.EmptyDataException
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class GetPostsUseCaseRxJava3(
    private val repository: PostRepositoryRxJava3,
    private val entityToPostMapper: EntityToPostMapper
) {
    /**
     * Method for getting data and saving data to db to use db as Single Source of Truth.
     * This method is useful for using in splash screen to fetch data, and data is required
     * to be used in multiple screens
     */
    fun fetchDataFromRemoteAndSaveToDB(): Single<List<PostEntity>> {
        return repository.fetchEntitiesFromRemote()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                repository.deletePostEntities()
                    .andThen(repository.savePostEntities(it))
                    .andThen(repository.getPostEntitiesFromLocal())
            }
    }


    /**
     * This function always looks for new data from REMOTE source first
     *
     * * if data is fetched from remote source: deletes old data, saves new data and returns new data
     * * if error occurred while fetching data from remote: it tries to fetch data from database
     * * if both network and db didn't have any data throws empty set exception error
     */
    fun getPostsFlowOfflineLast(): Single<ViewState<List<Post>>> {

        return repository.fetchEntitiesFromRemote()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                println("🍏 getPostFlowOfflineLast() First map in thread: ${Thread.currentThread().name}")

                if (it.isNullOrEmpty()) {
                    throw EmptyDataException("No data is available!")
                } else {
                    repository.deletePostEntities()
                        .andThen(repository.savePostEntities(it))
                        .andThen(repository.getPostEntitiesFromLocal())
                }
            }
            .onErrorResumeNext { cause ->
                println("❌ getPostFlowOfflineLast() FIRST onErrorResumeNext() with error: $cause, in thread: ${Thread.currentThread().name}")
                repository.getPostEntitiesFromLocal()
            }
            // Alternative 1
//            .map {
//                if (it.isNullOrEmpty()) {
//                    throw EmptyDataException("No data is available!")
//
//                } else {
//                    entityToPostMapper.map(it)
//                }
            // Alternative 2 START
            // This filter does not emit anything if list is empty. Empty list is still an emission
            .filter {
                println("🤓 getPostFlowOfflineLast() filter: ${it.isNotEmpty()}")
                it.isNotEmpty()
            }
            .map {
                entityToPostMapper.map(it)
            }
            .switchIfEmpty(
                Single.error(EmptyDataException("Data is available in neither in remote nor local source!"))
            )
            // Alternative 2 END

            .map { postList ->
                println("🎃 getPostFlowOfflineLast() Third map in thread: ${Thread.currentThread().name}")
                ViewState(status = Status.SUCCESS, data = postList)
            }
            .onErrorResumeNext { cause ->
                println("❌ getPostFlowOfflineLast() SECOND catch with error: $cause, in thread: ${Thread.currentThread().name}")
//                Single.error(cause)
                Single.just(ViewState(status = Status.ERROR, error = cause))
            }
    }


//    fun getPostFlowOfflineFirst(): Flow<ViewState<List<Post>>> {
//
//        return flow { emit(repository.getPostEntitiesFromLocal()) }
//            .map {
//
//                println("🍏 getPostFlowOfflineFirst() First map in thread: ${Thread.currentThread().name}")
//
//                if (it.isEmpty()) {
//                    repository.run {
//                        repository.deletePostEntities()
//                        repository.savePostEntities(fetchEntitiesFromRemote())
//                        repository.getPostEntitiesFromLocal()
//                    }
//                } else {
//                    it
//                }
//
//            }
//            .flowOn(dispatcherProvider.ioDispatcher)
//            .map {
//                println("🎃 getPostFlowOfflineFirst() Second map in thread: ${Thread.currentThread().name}")
//
//                // Map Entity to UI item
//                if (!it.isNullOrEmpty()) {
//                    entityToPostMapper.map(it)
//                } else {
//                    throw EmptyDataException("No data is available!")
//                }
//            }
//            .map { postList ->
//
//                println("🍎 getPostFlowOfflineFirst() Third map in thread: ${Thread.currentThread().name}")
//
//                ViewState<List<Post>>(
//                    status = Status.SUCCESS,
//                    data = postList
//                )
//            }
//            .catch { cause: Throwable ->
//                println("❌ getPostFlowOfflineFirst() SECOND catch with error: $cause, in thread: ${Thread.currentThread().name}")
//                emitAll(flow<ViewState<List<Post>>> {
//                    emit(
//                        ViewState(
//                            Status.ERROR,
//                            error = cause
//                        )
//                    )
//                })
//            }
//            .flowOn(dispatcherProvider.defaultDispatcher)
//    }

}