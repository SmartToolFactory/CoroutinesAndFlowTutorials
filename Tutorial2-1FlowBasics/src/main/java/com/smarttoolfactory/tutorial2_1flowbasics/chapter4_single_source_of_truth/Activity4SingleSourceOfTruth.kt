package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smarttoolfactory.tutorial2_1flowbasics.R
import com.smarttoolfactory.tutorial2_1flowbasics.di.ServiceLocator
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

//        val disposable = postDao.getPostByIdSingle(1)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnComplete {
//                println("⏰ MainActivity onCreate() doOnComplete()")
//            }
//            .doOnError {throwable ->
//                println("🔥 MainActivity onCreate() doOnError() throwable: $throwable")
//            }
//            .subscribe { postEntity ->
//                println("🍎 MainActivity onCreate() getPostsMaybe(): postEntity")
//            }
//
//            .subscribe(
//                {postEntity->
//                    println("🍎 MainActivity onCreate() onNext(): $postEntity")
//                },
//                {throwable->
//                    println("🍏 MainActivity onCreate() onError() throwable: $throwable")
//                }
//                {
//                    println("🎃 MainActivity onCreate() onComplete()")
//
//                }
//            )


//        launch {
//            val mapper = serviceLocator.provideMapper()
//            val posts = postApi.getPosts()
//            postDao.insert(mapper.map(posts))
//
//
//            val postList = postDao.getPostList()
//            val postZero = postDao.getPost(1)
//            println("🍏 MainActivity onCreate() PostList: ${postList.size}, zero post: $postZero")
//
//            postDao.getPostListFlow().collect {
//                println("🍎 MainActivity onCreate() postListFlow: ${it.size}")
//
//            }
//
//            postDao.getPostFlow(1)
//                .collect {
//                    println("🍋 MainActivity onCreate() postFlow: $it")
//                }
//        }


    }
}