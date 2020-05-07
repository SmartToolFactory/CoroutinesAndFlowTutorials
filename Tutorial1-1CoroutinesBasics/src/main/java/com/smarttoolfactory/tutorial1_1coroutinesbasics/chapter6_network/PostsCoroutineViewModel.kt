package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import androidx.lifecycle.*
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.RetrofitFactory
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Status.*
import com.smarttoolfactory.tutorial1_1coroutinesbasics.model.Post
import com.smarttoolfactory.tutorial1_1coroutinesbasics.model.ViewState
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostsCoroutineViewModel : ViewModel() {

    /**
     * Scope to test with context with IO thread
     */
    private val myCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * LiveData to test network operation with [Call]
     */
    private val _postStateWithCall = MutableLiveData<ViewState<String>>()
    val postStateWithCall: LiveData<ViewState<String>>
        get() = _postStateWithCall

    private val _postStateWithSuspend = MutableLiveData<ViewState<String>>()
    val postStateWithSuspend: LiveData<ViewState<String>>
        get() = _postStateWithSuspend


    private val callPostFunction = MutableLiveData<Boolean>()

    /**
     *  [MutableLiveData.switchMap] function is used to trigger liveDataBuilder whenever
     *  a button is clicked to make a network request.
     *
     *  This [MutableLiveData] can be used with a String for instance to make a network
     *  request for login action
     */
    val postStateWithLiveDataBuilder = callPostFunction.switchMap {

        liveData {

            println("😎 liveDataBuilder() scope: $this, thread: ${Thread.currentThread().name}")

            // Set current state to LOADING
            emit(ViewState(LOADING))

            // Get result from network, invoked in Retrofit's enque function thread
            val result = postsUseCase.getPosts()

            // Check and assign result to UI
            if (result.status == SUCCESS) {
                emit(ViewState(SUCCESS, data = result.data?.get(0)?.title))
            } else if (result.status == ERROR) {
                emit(ViewState(ERROR, error = result.error))
            }
        }
    }


    private val postsUseCase by lazy {
        PostsUseCase(PostsRepository(RetrofitFactory.makeRetrofitService()))
    }

    fun getPostWithCall() {

        viewModelScope.launch {

            println("🙄 getPostWithCall() scope: $this, thread: ${Thread.currentThread().name}")

            // Set current state to LOADING
            _postStateWithCall.value = ViewState(LOADING)

            postsUseCase.getPostsWithCall().enqueue(object : Callback<List<Post>> {

                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    _postStateWithCall.value = if (response.isSuccessful) {
                        ViewState(SUCCESS, data = response.body()?.get(0)?.title)
                    } else {
                        ViewState(ERROR, error = RuntimeException())
                    }
                }

                override fun onFailure(call: Call<List<Post>>, throwable: Throwable) {
                    _postStateWithCall.value =
                        ViewState(ERROR, error = throwable)
                }

            })
        }
    }

    /**
     * Every thing in this function works in thread of [viewModelScope] other than network action
     * [viewModelScope] uses [MainC.Main.immediate]
     */
    fun getPostWithSuspend() {

        viewModelScope.launch {

            println("🥶 getPostWithSuspend() scope: $this, thread: ${Thread.currentThread().name}")

            // Set current state to LOADING
            _postStateWithSuspend.value = ViewState(LOADING)

            // 🔥🔥 Get result from network, invoked in Retrofit's enque function thread
            val result = postsUseCase.getPosts()

            // Check and assign result to UI
            val resultViewState = if (result.status == SUCCESS) {
                ViewState(SUCCESS, data = result.data?.get(0)?.title)
            } else {
                ViewState(ERROR, error = result.error)
            }

            _postStateWithSuspend.value = resultViewState
        }
    }

    fun getPostWithSuspendDispatcherIO() {

        // Set current state to LOADING
        _postStateWithSuspend.value = ViewState(LOADING)

        // 🔥 Uses IO thread so, don't invoke UI changes from this thread
        myCoroutineScope.launch {

            println("🎃 getPostWithSuspendWithDispatcherIO() scope: $this, thread: ${Thread.currentThread().name}")

            // 🔥🔥 Get result from network, invoked in Retrofit's enqueue function thread
            val result = postsUseCase.getPosts()

            // Convert result to propitiate state
            val resultViewState = if (result.status == SUCCESS) {
                ViewState(SUCCESS, data = result.data?.get(0)?.title)
            } else {
                ViewState(ERROR, error = result.error)
            }

            // Alternative 1
            withContext(Dispatchers.Main) {
                _postStateWithSuspend.value = resultViewState
            }

            // Alternative 2
//            _postStateWithSuspend.postValue(resultViewState)
        }

    }

    fun getPostWithLiveDataBuilder() {
        callPostFunction.value = true
    }

    override fun onCleared() {
        super.onCleared()
        myCoroutineScope?.cancel()
    }

}