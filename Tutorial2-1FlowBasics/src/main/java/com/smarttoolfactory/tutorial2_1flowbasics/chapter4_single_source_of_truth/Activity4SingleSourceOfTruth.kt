package com.smarttoolfactory.tutorial2_1flowbasics.chapter4_single_source_of_truth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smarttoolfactory.tutorial2_1flowbasics.R
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
    }

}