package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.domain

import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.data.repository.PostRepositoryRxJava3
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.EntityToPostMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.ViewState
import com.smarttoolfactory.tutorial2_1flowbasics.util.EmptyDataException
import com.smarttoolfactory.tutorial2_1flowbasics.util.convertFromSingleToObservableViewState
import com.smarttoolfactory.tutorial2_1flowbasics.util.convertToObservableViewState
import io.reactivex.rxjava3.core.Observable
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
     * This function always looks for new data from REMOTE data source first
     *
     * * if data is fetched from remote source: deletes old data, saves new data and returns new data
     * * if error occurred while fetching data from remote: it tries to fetch data from database
     * * if both network and db didn't have any data throws empty set exception error
     */
    fun getPostsOfflineLast(): Observable<ViewState<List<Post>>> {

        // 🔥 Section 1 fetch data from local or remote data
        return repository.fetchEntitiesFromRemote()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                println("🍏 getPostFlowOfflineLast() flatMap() in thread: ${Thread.currentThread().name}")

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
            // 🔥 Section 2 map Entity to UI item or throw exception if data list is empty
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
                println("🎃 getPostFlowOfflineLast() Second map in thread: ${Thread.currentThread().name} list null or empty: ${it.isNullOrEmpty()}")
                it.isNotEmpty()
            }
            .map {
                entityToPostMapper.map(it)
            }
            .switchIfEmpty(
                Single.error(EmptyDataException("Data is available in neither in remote nor local source!"))
            )
            // Alternative 2 END
            // 🔥 Section 3 Convert result to state of UI
            .convertFromSingleToObservableViewState()
//            .map { postList ->
//                println("🍎 getPostFlowOfflineLast() Third map in thread: ${Thread.currentThread().name}")
//                ViewState(status = Status.SUCCESS, data = postList)
//            }
//            .onErrorResumeNext { cause ->
//                println("❌ getPostFlowOfflineLast() SECOND catch with error: $cause, in thread: ${Thread.currentThread().name}")
////                Single.error(cause)
//                Observable.just(ViewState(status = Status.ERROR, error = cause))
//            }
    }


    /**
     * Function to fetch data by searching LOCAL data source first. If local data source does not have
     * any data fetch data from REMOTE source and save data to local data source to use it
     * as Single Source of Truth.
     */
    fun getPostsOfflineFirst(): Observable<ViewState<List<Post>>> {

        // 🔥 Section 1 fetch data from local or remote data

        return repository.getPostEntitiesFromLocal()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap { list ->
                println("🍏 getPostsOfflineFirst() flatMap() in thread: ${Thread.currentThread().name}")

                if (list.isNullOrEmpty()) {
                    repository.fetchEntitiesFromRemote().flatMap {
                        repository.deletePostEntities()
                            .andThen(repository.savePostEntities(it))
                            .andThen(repository.getPostEntitiesFromLocal())
                    }
                } else {
                    Single.just(list)
                }
            }
            // TODO This is not actually necessary but this onResumeNext can be used to transform error to desired one
            .onErrorResumeNext { cause ->
                println("❌ getPostsOfflineFirst() FIRST onErrorResumeNext() with error: $cause, in thread: ${Thread.currentThread().name}")
                Single.just(listOf())
            }
            // 🔥 Section 2 map Entity to UI item or throw exception if data list is empty
            .filter {
                println("🎃 getPostsOfflineFirst() Second map in thread: ${Thread.currentThread().name} list null or empty: ${it.isNullOrEmpty()}")
                it.isNotEmpty()
            }
            .map {
                entityToPostMapper.map(it)
            }
            .switchIfEmpty(
                Single.error(EmptyDataException("Data is available in neither in remote nor local source!"))
            )

            // 🔥 Section 3 Convert result to state of UI
            .convertFromSingleToObservableViewState()
//            .map { postList ->
//                println("🍎 getPostsOfflineFirst() Third map in thread: ${Thread.currentThread().name}")
//                ViewState(status = Status.SUCCESS, data = postList)
//            }
//            .onErrorResumeNext { cause ->
//                println("❌ getPostsOfflineFirst() SECOND catch with error: $cause, in thread: ${Thread.currentThread().name}")
////                Single.error(cause)
//                Observable.just(ViewState(status = Status.ERROR, error = cause))
//            }

    }

}
