package com.smarttoolfactory.tutorial2_1flowbasics.chapter2_network

import androidx.lifecycle.*
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.Status
import com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth.ViewState
import com.smarttoolfactory.tutorial2_1flowbasics.data.api.RetrofitFactory
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.DTOtoPostMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PostNetworkViewModel(
    private val coroutineScope: CoroutineScope,
    private val postsUseCase: PostRemoteUseCase
) : ViewModel() {

    /**
     * Scope to test with context with IO thread
     */
    private val myCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * LiveData to test network operation with [Post]
     */
    private val _postViewState = MutableLiveData<ViewState<List<Post>>>()
    val postViewState: LiveData<ViewState<List<Post>>>
        get() = _postViewState

    private val callPostFunction = MutableLiveData<Boolean>()

    /**
     * Every thing in this function works in thread of [viewModelScope] other than network action
     * [viewModelScope] uses [MainCoroutineDispatcher.Main.immediate]
     */
    fun getPosts() {

        viewModelScope.launch {

            println("🥶 getPostWithSuspend() scope: $this, thread: ${Thread.currentThread().name}")

            // Set current state to LOADING

            // 🔥🔥 Get result from network, invoked in Retrofit's enqueue function thread
            postsUseCase.getPostFlow()
                .onStart {
                    _postViewState.value = ViewState(Status.LOADING)
                }
                .catch { throwable: Throwable ->
                    _postViewState.value = ViewState(Status.ERROR, error = throwable)
                }
                .onEach { postList ->
                    _postViewState.value = ViewState(Status.SUCCESS, data = postList)
                }
                .launchIn(coroutineScope)

        }
    }


    fun onClick(post: Post) {

    }


    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

}

class PostViewModelFactory :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val coroutineScope = CoroutineScope(Job())

        val postsUseCase =
            PostRemoteUseCase(PostRemoteRepository(RetrofitFactory.getPostApiCoroutines()), DTOtoPostMapper())

        return PostNetworkViewModel(coroutineScope, postsUseCase) as T
    }

}