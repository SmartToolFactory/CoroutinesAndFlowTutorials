package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter2_scopes

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.databinding.Activity2Scope4Binding
import com.smarttoolfactory.tutorial1_1coroutinesbasics.util.dataBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

/*
* The following example started from Mitch Tabian's github repo from branch Completable-Job-with-Cancellation-and-Progress-Bar
* https://github.com/mitchtabian/Kotlin-Coroutine-Examples/tree/Completable-Job-with-Cancellation-and-Progress-Bar
* */
class Activity2CoroutineScope4 : AppCompatActivity(R.layout.activity2_scope_4) {

    private val TAG: String = "AppDebug"

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000 // ms
    private lateinit var job: CompletableJob

    private val binding: Activity2Scope4Binding by dataBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.jobButton.setOnClickListener {
            if (!::job.isInitialized) {
                initjob()
            }
            binding.jobProgressBar.startJobOrCancel(job)
        }
    }


    fun initjob() {
        binding.jobButton.text = "Start Job #1"
        updateJobCompleteTextView("")
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (msg.isNullOrBlank()) {
                    msg = "Unknown cancellation error."
                }
                Log.e(TAG, "${job} was cancelled. Reason: ${msg}")
                showToast(msg)
            }
        }
        binding.jobProgressBar.max = PROGRESS_MAX
        binding.jobProgressBar.progress = PROGRESS_START
    }


    fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            Log.d(TAG, "${job} is already active. Cancelling...")
            resetjob()
        } else {
            binding.jobButton.text = "Cancel Job #1"
            CoroutineScope(IO + job).launch {
                Log.d(TAG, "coroutine ${this} is activated with job ${job}.")

                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTextView("Job is complete!")
            }
        }
    }

    fun resetjob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting job"))
        }
        initjob()
    }

    private fun updateJobCompleteTextView(text: String) {
        GlobalScope.launch(Main) {
            binding.jobCompleteText.text = text
        }
    }

    private fun showToast(text: String) {
        GlobalScope.launch(Main) {
            Toast.makeText(this@Activity2CoroutineScope4, text, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::job.isInitialized)
            job.cancel()
    }

}


