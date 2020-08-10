package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network

import androidx.lifecycle.*
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Post
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.RetrofitFactory
import com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter6_network.api.Status.*
import com.smarttoolfactory.tutorial1_1coroutinesbasics.model.ViewState
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.measureTimeMillis

class PostsCoroutineViewModel(
    private val myCoroutineScope: CoroutineScope,
    private val postsUseCase: PostsUseCase
) : ViewModel() {


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
        setLiveDataBuilder()
    }


    /**
     * These calls are sequential since [Deferred.await] is called right
     * after [CoroutineScope.async]
     */
    fun doSomeSequentialNetworkCalls() {

        myCoroutineScope.launch {

            val measuredTime = measureTimeMillis {

                val response1 = async { postsUseCase.getPosts() }.await()
                val response2 = async { postsUseCase.getPosts() }.await()
                val response3 = async { postsUseCase.getPosts() }.await()
                val response4 = async { postsUseCase.getPosts() }.await()
                val response5 = async { postsUseCase.getPosts() }.await()
            }

            println("🙄 Total time with sequential call $measuredTime")
        }


    }

    fun doSomeParallelNetworkCalls() {

        myCoroutineScope.launch {

            val measuredTime = measureTimeMillis {

                val response1 = async { postsUseCase.getPosts() }
                val response2 = async { postsUseCase.getPosts() }
                val response3 = async { postsUseCase.getPosts() }
                val response4 = async { postsUseCase.getPosts() }
                val response5 = async { postsUseCase.getPosts() }

                val responseList = listOf(response1, response2, response3, response4, response5)

                responseList.awaitAll()

            }


            println("🎃 Total time with parallel call $measuredTime")
        }

    }

    fun doSomeParallelNetworkCallsWithLaunch() {

        myCoroutineScope.launch {

            val measuredTime = measureTimeMillis {
                launch { postsUseCase.getPosts() }
                launch { postsUseCase.getPosts() }
                launch { postsUseCase.getPosts() }
                launch { postsUseCase.getPosts() }
                launch { postsUseCase.getPosts() }
            }


            println("😎 Total time with parallel call with LAUNCH: $measuredTime")
        }

    }

    fun getPostWithCall() {

        myCoroutineScope.launch {

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
     * [viewModelScope] uses [MainCoroutineDispatcher.Main.immediate]
     */
    fun getPostWithSuspend() {

        myCoroutineScope.launch {


            // Set current state to LOADING
            _postStateWithSuspend.value = ViewState(LOADING)

            println("🥶 getPostWithSuspend() scope: $this, LOADING in thread: ${Thread.currentThread().name}")


            // 🔥🔥 Get result from network, invoked in Retrofit's enqueue function thread
            val result = postsUseCase.getPosts()

            println("😍 getPostWithSuspend() result: $result, thread: ${Thread.currentThread().name}")

            // Check and assign result to UI
            val resultViewState = if (result.status == SUCCESS) {
                ViewState(SUCCESS, data = result.data?.get(0)?.title)
            } else {
                ViewState(ERROR, error = result.error)
            }

            _postStateWithSuspend.value = resultViewState

            println("🔥 getPostWithSuspend() resultViewState: ${resultViewState.status}, thread: ${Thread.currentThread().name}")

        }
    }

    /**
     * This function uses a custom scope that has [CoroutineScope.coroutineContext]
     * that contains [Dispatchers.IO] and [SupervisorJob]
     */
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

    private fun setLiveDataBuilder(): LiveData<ViewState<String>> {

        return liveData {

            println("😎 liveDataBuilder() scope: $this, thread: ${Thread.currentThread().name}")

            // Set current state to LOADING
            emit(ViewState(LOADING))

            // Get result from network, invoked in Retrofit's enqeue function thread
            val result = postsUseCase.getPosts()

            // Check and assign result to UI
            if (result.status == SUCCESS) {
                emit(ViewState(SUCCESS, data = result.data?.get(0)?.title))
            } else if (result.status == ERROR) {
                emit(ViewState(ERROR, error = result.error))
            }
        }
    }

    fun getPostWithLiveDataBuilder() {
        callPostFunction.value = true
    }


    override fun onCleared() {
        super.onCleared()
        myCoroutineScope.cancel()
    }

}

class PostCoroutineViewModelFactory : ViewModelProvider.Factory {

    private val postsUseCase by lazy {
        PostsUseCase(PostsRepository(RetrofitFactory.getPostApiCoroutines()))
    }


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

        return PostsCoroutineViewModel(coroutineScope, postsUseCase) as T
    }

}