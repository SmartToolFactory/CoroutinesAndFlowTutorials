package com.smarttoolfactory.tutorial2_1flowbasics.chapter3_database

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Status
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.ViewState
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.EntityToPostMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.mapper.PostToEntityMapper
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Post
import com.smarttoolfactory.tutorial2_1flowbasics.di.ServiceLocator
import com.smarttoolfactory.tutorial2_1flowbasics.util.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PostDBViewModel(
    private val myCoroutineScope: CoroutineScope,
    private val postsUseCase: PostDBUseCase
) : ViewModel() {


    /**
     * LiveData to test network operation with [Post]
     */
    private val _postViewState = MutableLiveData<ViewState<List<Post>>>()
    val postViewState: LiveData<ViewState<List<Post>>>
        get() = _postViewState

    private val _postCount = MutableLiveData<Event<Int>>()
    val postCount: LiveData<Event<Int>>
        get() = _postCount


    fun getPostCount() {
        flow { emit(postsUseCase.getPostCount()) }
            .flowOn(Dispatchers.IO)
            .catch { cause ->
                println("🤬 PostDBViewModel getPostCount() error: ${cause.message}, thread: ${Thread.currentThread().name}")
            }
            .onEach { postCount ->
                _postCount.value = Event(postCount)
            }
            .launchIn(myCoroutineScope)

    }

    fun deletePost(post: Post) {

    }

    fun savePosts(posts: List<Post>) {
        myCoroutineScope.launch(Dispatchers.IO) {
            println("😀 PostDBViewModel savePosts() thread: ${Thread.currentThread().name}")
            postsUseCase.savePosts(posts)
        }
    }

    /**
     * This function works in thread of [myCoroutineScope] is [MainCoroutineDispatcher.Main.immediate]
     * but [Flow.flowOn] changes the working thread from  [Dispatchers.Default]
     */
    fun getPosts() {

        // Set current state to LOADING

        // 🔥🔥 Get result from network, invoked in Retrofit's enqueue function thread
        postsUseCase.getPostFlow()
            .flowOn(Dispatchers.Main)
            .onStart {
                _postViewState.value =
                    ViewState(
                        Status.LOADING
                    )
            }
            .catch { throwable: Throwable ->
                _postViewState.value =
                    ViewState(
                        Status.ERROR,
                        error = throwable
                    )
            }
            .onEach { postList ->
                println("😇 PostDBViewModel getPosts() thread: ${Thread.currentThread().name}")
                _postViewState.value =
                    ViewState(
                        Status.SUCCESS,
                        data = postList
                    )
            }
            .launchIn(myCoroutineScope)

    }


    fun onClick(post: Post) {

    }


    override fun onCleared() {
        super.onCleared()
        myCoroutineScope.cancel()
    }

}

class PostViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val coroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

        val serviceLocator = ServiceLocator(application)

        val postsUseCase =
            PostDBUseCase(
                PostDBRepository(serviceLocator.providePostDao()),
                EntityToPostMapper(),
                PostToEntityMapper()
            )

        return PostDBViewModel(coroutineScope, postsUseCase) as T
    }

}