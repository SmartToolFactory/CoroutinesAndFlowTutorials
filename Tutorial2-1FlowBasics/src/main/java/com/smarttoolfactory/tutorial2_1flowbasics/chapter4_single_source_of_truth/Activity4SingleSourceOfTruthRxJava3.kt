package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.smarttoolfactory.tutorial2_1flowbasics.R
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.PostEntity
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.Status
import com.smarttoolfactory.tutorial2_1flowbasics.data.model.ViewState
import com.smarttoolfactory.tutorial2_1flowbasics.di.ServiceLocator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class Activity4SingleSourceOfTruthRxJava3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity4_single_source_of_truth)

        title = "Single Source of Truth RxJava3"

        val navHostFragment: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navHostFragment.findNavController().setGraph(R.navigation.nav_graph_post_list_rxjava3)

//        examineDaoWithRxJava()

    }

    /**
     * Function to test what happens when there is no post in DB with Single, Maybe and Observable
     * classes of RxJava2
     */
    private fun examineDaoWithRxJava() {

        val serviceLocator = ServiceLocator(application)

        val postDaoRxJava = serviceLocator.providePostDaoRxJava()

//        // SINGLE
//        val disposableSingle = postDaoRxJava.getPostByIdSingle(1000)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
////            .onErrorResumeNext {
////                    _ -> Single.error(AssertionError("ERROR"))
////            }
//            .map {
//                ViewState(status = Status.SUCCESS, data = it)
//            }
//            .onErrorResumeNext {
//                Single.just(ViewState(status = Status.ERROR, error = it))
//            }
//            .doOnError { throwable ->
//                println("🔥 MainActivity onCreate() SINGLE doOnError() throwable: $throwable")
//            }
//            .startWith(Single.just(ViewState(status = Status.LOADING)))
//            .subscribe(
//                { postEntity ->
//                    println("🍎 MainActivity SINGLE onCreate() getPostByIdSingle() onNext(): ${postEntity.status}")
//                },
//                {
//                    println("⏰  MainActivity SINGLE onCreate() getPostByIdSingle() onError: $it")
//                }
//            )
//
//        // MAYBE
//        val disposableMaybe = postDaoRxJava.getPostByIdMaybe(1000)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnError { throwable ->
//                println("🔥 MainActivity onCreate() MAYBE doOnError() throwable: $throwable")
//            }
//            .doOnComplete {
//                println("🔥 MainActivity onCreate() MAYBE doOnComplete()")
//
//            }
//
//            .map {
//                ViewState<PostEntity>(status = Status.SUCCESS, data = it)
//            }
//            // 🔥🔥🔥 This does not have any effect, only doOnComplete and onComplete is called
//            .onErrorResumeNext {
//                Maybe.just(ViewState(status = Status.ERROR, error = it))
//            }
//            .startWith(Maybe.just(ViewState(status = Status.LOADING)))
//
//            .subscribe(
//                { postEntity ->
//                    println("🍎 MainActivity onCreate() MAYBE getPostListByIdMaybe() onNext(): ${postEntity.status}")
//                },
//                {
//                    println("🍏 MainActivity onCreate() MAYBE getPostListByIdMaybe() onError: $it")
//                },
//                {
//                    println("⏰ MainActivity onCreate() MAYBE getPostListByIdMaybe() onComplete")
//                }
//            )
//
//        // OBSERVABLE
//        val disposableObservable = postDaoRxJava.getPostById(1000)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//
//            .doOnError { throwable ->
//                println("🔥 MainActivity onCreate() OBSERVABLE doOnError() throwable: $throwable")
//            }
//            .doOnComplete {
//                println("🔥 MainActivity onCreate() OBSERVABLE doOnComplete()")
//            }
//            .map {
//                ViewState(status = Status.SUCCESS, data = it)
//            }
//            // 🔥🔥🔥 This does not have any effect, if there is an error NOTHING gets called
//            .onErrorResumeNext {
//                Observable.just(ViewState(status = Status.ERROR, error = it))
//            }
//            .startWith(Maybe.just(ViewState(status = Status.LOADING)))
//            .subscribe(
//                { postEntity ->
//                    println("🍎 MainActivity onCreate() OBSERVABLE getPostListById() onNext(): ${postEntity.status}")
//                },
//                {
//                    println("🍏 MainActivity onCreate() OBSERVABLE getPostListById() onError: $it")
//                },
//                {
//                    println("⏰ MainActivity onCreate() OBSERVABLE getPostListById() onComplete")
//                }
//            )


        // FROM SINGLE TO OBSERVABLE

        val disposableSingleToObservable = postDaoRxJava.getPostByIdSingle(1000)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .onErrorResumeNext {
//                    _ -> Single.error(AssertionError("ERROR"))
//            }
            .toObservable()
            .map {
                ViewState(status = Status.SUCCESS, data = it)
            }
            .onErrorResumeNext {
                Observable.just(ViewState(status = Status.ERROR, error = it))
            }
            .doOnError { throwable ->
                println("🔥 MainActivity onCreate() SINGLE->OBSERVABLE doOnError() throwable: $throwable")
            }
            .startWith(Single.just(ViewState(status = Status.LOADING)))
            .subscribe(
                { postEntity ->
                    println("🍎 MainActivity INGLE->OBSERVABLE onCreate() getPostByIdSingle() onNext(): ${postEntity.status}")
                },
                {
                    println("⏰  MainActivity INGLE->OBSERVABLE onCreate() getPostByIdSingle() onError: $it")
                }
            )
    }
}