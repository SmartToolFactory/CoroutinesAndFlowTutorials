package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.post_list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.domain.GetPostsUseCaseRxJava3
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Status
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.ViewState
import com.smarttoolfactory.tutorial2_1flowbasics.di.ServiceLocator
import com.smarttoolfactory.tutorial2_1flowbasics.util.Event
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single

class PostViewModelRxJava3(
    private val getPostsUseCase: GetPostsUseCaseRxJava3
) : AbstractPostViewModel() {


    override fun getPosts() {
        getPostsUseCase.getPostsOfflineFirst()
//            .startWith {
//                println("🛳 PostViewModel LOADING in thread ${Thread.currentThread().name}")
//                SingleSource {
//                     ViewState(status = Status.LOADING)
//                }
//            }
//            .startWith(Single.just(ViewState(status = Status.LOADING)))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    println("🛳 PostViewModel getPosts() onNext() in thread ${Thread.currentThread().name} ViewState: ${it.status}")
                    _postViewState.value = it
                }, {
                    println("🛳 PostViewModel getPosts() onError() in thread ${Thread.currentThread().name}")
                    _postViewState.value = ViewState(status = Status.ERROR, error = it)
                })

    }

    override fun refreshPosts() {
        getPostsUseCase.getPostsOfflineLast()
//            .startWith(Single.just(ViewState(status = Status.LOADING)))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    println("🛳 PostViewModel refreshPosts() onNext()  in thread ${Thread.currentThread().name} ViewState: ${it.status}")
                    _postViewState.value = it
                }, {
                    println("🛳 PostViewModel refreshPosts() onError()  in thread ${Thread.currentThread().name}")
                    _postViewState.value = ViewState(status = Status.ERROR, error = it)
                })
    }

    override fun onClick(post: Post) {
        _goToDetailScreen.value = Event(post)
    }
}

class PostViewModelFactoryRxJava3(private val application: Application) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {


        val serviceLocator = ServiceLocator(application)

        return PostViewModelRxJava3(serviceLocator.provideGetPostsUseCaseRxJava3()) as T
    }

}