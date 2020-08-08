package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter5_viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.TimeUnit

class RxJavaViewModel : ViewModel() {

    private val disposables = CompositeDisposable()

    val result = MutableLiveData<String>()
    val enableResultButton = MutableLiveData<Boolean>().apply {
        value = true
    }

    val resultWithRetry = MutableLiveData<String>()
    val enableRetryButton = MutableLiveData<Boolean>().apply {
        value = true
    }

    val resultWithTimeout = MutableLiveData<String>()
    val enableWithTimeoutButton = MutableLiveData<Boolean>().apply {
        value = true
    }

    val resultMultiple = MutableLiveData<String>()
    val enableMultipleButton = MutableLiveData<Boolean>().apply {
        value = true
    }

    fun getMockResult() {

        val disposable = getObservable { generateMockNetworkResponse() }

            /*
               🔥 subscribeOn() work upstream, if subscribeOn is below
               doOnSubscribe, then doOnSubscribe will be invoked in
               Schedulers.io(), otherwise main thread or thread subscribeOn()
               below doOnSubscribe runs in
             */
            .subscribeOn(Schedulers.io())
            .startWith("Loading...")
            .doFinally {
                println("😎 doFinally() thread: ${Thread.currentThread().name}")
                enableResultButton.postValue(true)
            }
            /*
                🔥subscribeOn is where we subscribe another observable, not the thread map() function is called
                This does not effect anything since there is another subscribeOn(Schedulers.io()) which works upstream
                only with Observables, not mapping or observing methods
             */
            .subscribeOn(Schedulers.computation())
            // 🔥observeOn works down stream any subscribe or map method works in thread that observeOn specifies
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    println("😱 onNext(): $it, thread: ${Thread.currentThread().name}")
                    result.postValue(it)
                },
                {
                    result.value = it.message
                }
            )
        disposables.add(disposable)
    }


    fun getMockResultWithTimeout() {

        val timeout = 1500L

        val disposable = getObservable { generateMockNetworkResponse(timeout) }

            .timeout(timeout, TimeUnit.MILLISECONDS)
            .startWith("Loading...")
            .doOnSubscribe {
                enableWithTimeoutButton.postValue(false)
            }
            .doFinally {
                enableWithTimeoutButton.postValue(true)
            }

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    resultWithTimeout.value = it
                },
                {
                    resultWithTimeout.value = it.message
                }
            )

        disposables.add(disposable)

    }

    fun getMockResultWithRetry() {


        val timeout = 2100L

        var retryCount = 0
        val retryThreshold = 3

        val disposable = getObservable { generateMockNetworkResponse(timeout) }
            .retryUntil {
                println("🔧 retryUntil() retry: $retryCount")

                retryCount++
                if (retryCount < retryThreshold) {
                    resultWithRetry.postValue("Retry count: $retryCount")
                } else {
                    resultWithRetry.postValue("Failed after $retryCount tries")
                }

                // 🔥 FAIL Condition
                (retryCount > retryThreshold)

            }
            .startWith("Loading...")
            .doOnSubscribe {
                enableRetryButton.postValue(false)
            }
            .doFinally {
                enableRetryButton.postValue(true)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    resultWithRetry.value = it
                },
                {
                    resultWithRetry.value = it.message
                }
            )

        disposables.add(disposable)
    }


    /**
     * 🔥 If we wish to get parallel emission with [Observable.zip] each [Observable]
     * should have schedulers with new thread
     */
    fun getMultipleResponsesConcurrently() {

        val source1 = getObservable { generateRandomCity() }
            .subscribeOn(Schedulers.newThread())

        val source2 = getObservable { generateRandomEmoji() }
            .subscribeOn(Schedulers.newThread())

        val source3 = getObservable { generateMockNetworkResponse() }
            .subscribeOn(Schedulers.newThread())


        val startTime = System.currentTimeMillis()

        val disposable = Observable.zip(source1, source2, source3,
            Function3 { city: String, emoji: String, response: String ->
                "$city - $emoji - $response, time: ${System.currentTimeMillis() - startTime}ms"
            })
            .startWith("Loading...")
            .doOnSubscribe {
                enableMultipleButton.postValue(false)
            }
            .doFinally {
                enableMultipleButton.postValue(true)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    resultMultiple.value = it
                },
                {
                    resultMultiple.value = it.message
                }
            )

        disposables.add(disposable)

    }

    /**
     * Method to fake api response in specified scheduler
     */
    private fun <T> getObservable(delayTime: Long = 0, block: () -> T): Observable<T> {
        return Observable.just(1)
            .map {
                block()
            }
    }

    /*
        Mock Response Functions to simulate network response
     */

    private fun generateMockNetworkResponse(delay: Long = 2000): String {

        println("🥶 generateMockNetworkResponse() thread: ${Thread.currentThread().name}")
        sleep(delay)

        if (delay > 2000) throw RuntimeException()

        return "Hello World"
    }

    private fun generateRandomCity(): String {
        val cityList = listOf("Berlin", "New York", "London", "Paris", "Istanbul")

        val random = Random()

        sleep(3000)
        return cityList[random.nextInt(cityList.size)]
    }

    private fun generateRandomEmoji(): String {

        val emojiList = listOf("🤪", "🙄", "😛", "😭", "🤬", "🥶", "😱")

        val random = Random()
        return emojiList[random.nextInt(emojiList.size)]

    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}