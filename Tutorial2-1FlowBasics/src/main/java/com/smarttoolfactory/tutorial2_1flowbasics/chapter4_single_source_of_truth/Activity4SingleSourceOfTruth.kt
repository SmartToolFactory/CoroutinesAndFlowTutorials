package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smarttoolfactory.tutorial2_1flowbasics.R
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import com.smarttoolfactory.tutorial2_1flowbasics.di.ServiceLocator
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class Activity4SingleSourceOfTruth : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main + CoroutineName("🙄 Activity Scope") + CoroutineExceptionHandler { coroutineContext, throwable ->
            println("🤬 Exception $throwable in context:$coroutineContext")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity4_single_source_of_truth)

        job = Job()

        val serviceLocator = ServiceLocator(application)
        val postDao = serviceLocator.providePostDao()
        val postApi = serviceLocator.providePostApi()

//        examineDaoWithRxJava()

    }

    /**
     * Function to test what happens when there is no post in DB with Single, Maybe and Observable
     * classes of RxJava2
     */
    private fun examineDaoWithRxJava() {

        val serviceLocator = ServiceLocator(application)

        val postDaoRxJava = serviceLocator.providePostDaoRxJava()

        // SINGLE
        val disposableSingle = postDaoRxJava.getPostByIdSingle(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .onErrorResumeNext {
//                    _ -> Single.error(AssertionError("ERROR"))
//            }
            .onErrorResumeNext {
                Single.just(PostEntity(1, 1, "Title", "Body"))
            }
            .doOnError { throwable ->
                println("🔥 MainActivity onCreate() doOnError() throwable: $throwable")
            }
            .subscribe(
                { postEntity ->
                    println("🍎 MainActivity onCreate() getPostByIdSingle() onNext(): $postEntity")
                },
                {
                    println("⏰ MainActivity onCreate() getPostByIdSingle() onError: $it")
                }
            )

        // MAYBE
//        val disposableMaybe = postDaoRxJava.getPostListByIdMaybe(1)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnError { throwable ->
//                println("🔥 MainActivity onCreate() doOnError() throwable: $throwable")
//            }
//            .doOnComplete {
//                println("🔥 MainActivity onCreate() doOnComplete()")
//
//            }
//            .subscribe(
//                { postEntity ->
//                    println("🍎 MainActivity onCreate() getPostListByIdMaybe() onNext(): $postEntity")
//                },
//                {
//                    println("🍏 MainActivity onCreate() getPostListByIdMaybe() onError: $it")
//                },
//                {
//                    println("⏰ MainActivity onCreate() getPostListByIdMaybe() onComplete")
//                }
//            )

        // OBSERVABLE
        //        val disposableObservable = postDaoRxJava.getPostListById(1)
        //            .subscribeOn(Schedulers.io())
        //            .observeOn(AndroidSchedulers.mainThread())
        //            .doOnError { throwable ->
        //                println("🔥 MainActivity onCreate() doOnError() throwable: $throwable")
        //            }
        //            .doOnComplete {
        //                println("🔥 MainActivity onCreate() doOnComplete()")
        //
        //            }
        //            .subscribe(
        //                { postEntity ->
        //                    println("🍎 MainActivity onCreate() getPostListById() onNext(): $postEntity")
        //                },
        //                {
        //                    println("🍏 MainActivity onCreate() getPostListById() onError: $it")
        //                },
        //                {
        //                    println("⏰ MainActivity onCreate() getPostListById() onComplete")
        //                }
        //            )
    }
}