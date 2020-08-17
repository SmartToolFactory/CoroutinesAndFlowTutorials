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

class PostViewModelRxJava3(
    private val getPostsUseCase: GetPostsUseCaseRxJava3
) : AbstractPostViewModel() {


    override fun getPosts() {
        getPostsUseCase.getPostsFlowOfflineLast()
//            .startWith {
//                println("🛳 PostViewModel LOADING in thread ${Thread.currentThread().name}")
//                Single.just{
//                    _postViewState.value = ViewState(status = Status.LOADING)
//                }
//            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    println("🛳 PostViewModel SUCCESS in thread ${Thread.currentThread().name}")
                    _postViewState.value = it
                }, {
                    _postViewState.value = ViewState(status = Status.ERROR, error = it)
                })

    }

    override fun refreshPosts() {
//        getPostsUseCase.getPostsFlowOfflineLast()
//            .onStart {
//                println("🛳 PostViewModel LOADING in thread ${Thread.currentThread().name}")
//                _postViewState.value = ViewState(status = Status.LOADING)
//            }
//            .onEach {
//                println("🛳 PostViewModel SUCCESS in thread ${Thread.currentThread().name}")
//                _postViewState.value = it
//            }
//            .launchIn(coroutineScope)
    }

    override fun onClick(post: Post) {
        _goToDetailScreen.value = Event(post)
    }
}

class PostViewModelRxJava3Factory(private val application: Application) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {


        val serviceLocator = ServiceLocator(application)

        return PostViewModelRxJava3(serviceLocator.provideGetPostsUseCaseRxJava3()) as T
    }

}