package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter3_lifecycle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smarttoolfactory.tutorial1_1basics.databinding.Activity3CoroutineLifecycleBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class Activity3CoroutineLifecycle : AppCompatActivity(), CoroutineScope {


    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main + CoroutineName("🙄 Activity Scope") + CoroutineExceptionHandler { coroutineContext, throwable ->
            println("🤬 Exception $throwable in context:$coroutineContext")
        }


    private val dataBinding by lazy {
        Activity3CoroutineLifecycleBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dataBinding.root)

        job = Job()

        dataBinding. button.setOnClickListener {

            // This scope lives as long as Application is alive
            GlobalScope.launch {
                for (i in 0..300) {
                    println("🤪 Global Progress: $i in thread: ${Thread.currentThread().name}, scope: $this")
                    delay(300)
                }
            }

            // This scope is canceled whenever this Activity's onDestroy method is called
            launch {
                for (i in 0..300) {
                    println("😍 Activity Scope Progress: $i in thread: ${Thread.currentThread().name}, scope: $this")
                    withContext(Dispatchers.Main) {
                        dataBinding.tvResult.text = "😍 Activity Scope Progress: $i in thread: ${Thread.currentThread().name}, scope: $this"
                    }
                    delay(300)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    private suspend fun printNumbers(coroutineScope: CoroutineScope) {

        for (i in 0..100) {
            println("Progress: $i in thread: ${Thread.currentThread().name}, scope: $coroutineScope")
            delay(300)
        }
    }
}
