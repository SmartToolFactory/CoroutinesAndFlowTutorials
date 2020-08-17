package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.post_list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.domain.GetPostsUseCaseFlow
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Status
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.ViewState
import com.smarttoolfactory.tutorial2_1flowbasics.di.ServiceLocator
import com.smarttoolfactory.tutorial2_1flowbasics.util.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class PostViewModel(
    private val coroutineScope: CoroutineScope,
    private val getPostsUseCase: GetPostsUseCaseFlow
) : AbstractPostViewModel() {


   override fun getPosts() {
        getPostsUseCase.getPostFlowOfflineFirst()
            .onStart {
                println("🛳 PostViewModel LOADING in thread ${Thread.currentThread().name}")
                _postViewState.value = ViewState(status = Status.LOADING)
            }
            .onEach {
                println("🛳 PostViewModel SUCCESS in thread ${Thread.currentThread().name}")
                _postViewState.value = it
            }
            .launchIn(coroutineScope)
    }

    override  fun refreshPosts() {
        getPostsUseCase.getPostFlowOfflineLast()
            .onStart {
                println("🛳 PostViewModel LOADING in thread ${Thread.currentThread().name}")
                _postViewState.value = ViewState(status = Status.LOADING)
            }
            .onEach {
                println("🛳 PostViewModel SUCCESS in thread ${Thread.currentThread().name}")
                _postViewState.value = it
            }
            .launchIn(coroutineScope)
    }

    override fun onClick(post: Post) {
        _goToDetailScreen.value = Event(post)
    }
}

class PostViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

        val serviceLocator = ServiceLocator(application)

        return PostViewModel(coroutineScope, serviceLocator.provideGetPostsUseCase()) as T
    }

}