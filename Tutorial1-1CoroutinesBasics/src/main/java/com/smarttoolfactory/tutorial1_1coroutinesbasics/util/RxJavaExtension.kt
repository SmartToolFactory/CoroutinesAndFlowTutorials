package com.smarttoolfactory.tutorial1_1coroutinesbasics.util

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 *
 * Extension Functions for RxJava2
 * @author Smart Tool Factory
 *
 */
fun <T> Observable<T>.listen(
    scheduleSubscribe: Scheduler,
    schedulerObserve: Scheduler
): Observable<T> {
    return subscribeOn(scheduleSubscribe)
        .observeOn(schedulerObserve)
}

fun <T> Observable<T>.listenOnMain(): Observable<T> {
    return listen(Schedulers.io(), AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.listenOnIO(): Observable<T> {
    return listen(Schedulers.io(), Schedulers.io())
}


/**
 * Extension method to subscribe an observable on schedulers thread and observe on main.
 *
 */
fun <T> Observable<T>.observeResultOnMain(
    onNext: (T) -> Unit,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
): Disposable {
    return listenOnMain()
        .subscribe(
            {
                onNext(it)
            },
            { throwable ->
                onError?.apply {
                    onError(throwable)
                }
            },
            {
                onComplete?.apply {
                    onComplete()
                }
            })
}


fun <T> Observable<T>.observeResultOnIO(
    onNext: (T) -> Unit,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
): Disposable {
    return listenOnIO()
        .subscribe(
            {
                onNext(it)
            },
            { throwable ->
                onError?.apply {
                    onError(throwable)
                }
            },
            {
                onComplete?.apply {
                    onComplete()
                }
            })
}




fun <T> Single<T>.logLifeCycleEvents(): Single<T> {
    return this
        .doOnSubscribe {
            println("⏱ doOnSubscribe() thread: ${Thread.currentThread().name}")
        }
        .doOnSuccess {
            println("🥶 doOnSuccess() thread: ${Thread.currentThread().name}, val: $it")
        }
        .doAfterSuccess {
            println("😍 doAfterSuccess() thread: ${Thread.currentThread().name}, val: $it")
        }
        .doOnTerminate {
            println("doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("🤬 doOnError() ${it.message}")
        }
}


fun <T> Maybe<T>.logLifeCycleEvents(): Maybe<T> {

    return this
        .doOnSubscribe {
            println("⏱ doOnSubscribe() thread: ${Thread.currentThread().name}")
        }

        .doOnSuccess {
            println("🥶 doOnSuccess() thread: ${Thread.currentThread().name}, val: $it")
        }
        .doAfterSuccess {
            println("😍 doAfterSuccess() thread: ${Thread.currentThread().name}, val: $it")
        }

        .doOnComplete {
            println("doOnComplete() thread: ${Thread.currentThread().name}")
        }
        .doOnTerminate {
            println("doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("🤬 doOnError() ${it.message}")
        }
}

fun <T> Observable<T>.logLifeCycleEvents(): Observable<T> {

    return this
        .doOnSubscribe {
            println("⏱ doOnSubscribe() thread: ${Thread.currentThread().name}")
        }
        .doOnEach {
            println("🎃 doOnEach() thread: ${Thread.currentThread().name}, event: ${it}, val: ${it.value}")
        }
        .doOnNext {
            println("🥶 doOnNext() thread: ${Thread.currentThread().name}, val: $it")
        }
        .doAfterNext {
            println("😍 doAfterNext() thread: ${Thread.currentThread().name}, val: $it")
        }

        .doOnComplete {
            println("doOnComplete() thread: ${Thread.currentThread().name}")
        }
        .doOnTerminate {
            println("doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("🤬 doOnError() ${it.message}")
        }
}

fun Disposable.addTo(disposables: CompositeDisposable) {
    disposables.add(this)
}