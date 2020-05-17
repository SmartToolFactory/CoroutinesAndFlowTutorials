package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter3_supervisorjob

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.databinding.Activity3SupervisorJobBinding
import kotlinx.coroutines.*


/**
 * [SupervisorJob] let's a child job to handle it's exception instead of propagating to
 * parent, and parent cancelling other children coroutines.
 *
 */
class Activity3SupervisorJob : AppCompatActivity() {

    // Jobs

    /**
     * Job of the coroutineContext, [CoroutineScope.cancel] calls this [Job] cancel method.
     * And when this job is on completed state which does not let currentJob to be active
     *
     * [SupervisorJob] does not mean child coroutines will have a [SupervisorJob]
     * with a scope builder method like [CoroutineScope.launch]
     */
    private var parentJob: Job = SupervisorJob()

    /**
     * This is the jub that runs when button is clicked
     */
    private var currentJob: Job? = null
    private var childJob1: Job? = null
    private var childJob2: Job? = null

    // Scope
    /**
     * [Job] of [CoroutineScope] is canceled if [CoroutineScope.cancel] is called
     * and is NOT launching a coroutine using this scope
     */
    private val coroutineScope = CoroutineScope(parentJob)

    private lateinit var binding: Activity3SupervisorJobBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("🤨 onCreate() parentJob: $parentJob")

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity3_supervisor_job)

        bindViews()

    }

    private fun bindViews() {

        var isJobStarted = false

        binding.btnStartJob.setOnClickListener {

            if (!isJobStarted) {

                startJob()

                isJobStarted = true
                binding.btnStartJob.text = "Cancel"

            } else {
                cancelJob()
                isJobStarted = false
                binding.btnStartJob.text = "Start Job"
            }
        }

        binding.btnCancelChildJob1.setOnClickListener {

            childJob1?.let {
                if (childJob1!!.isActive) {
                    childJob1!!.cancel()
                    binding.btnCancelChildJob1.isEnabled = false
                    binding.tvChildJobResult1.text = "Progress canceled"
                }
            }
        }

        binding.btnCancelChildJob2.setOnClickListener {
            childJob2?.let {

                if (childJob2!!.isActive) {
                    childJob2!!.cancel()
                    binding.btnCancelChildJob2.isEnabled = false
                    binding.tvChildJobResult2.text = "Progress canceled"
                }

            }
        }

    }

    private fun startJob() {

        // Handle works in thread that exception is caught that are
        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("🤬 Parent Caught $throwable in thread ${Thread.currentThread().name}, and coroutineContext: $coroutineContext")
        }

        currentJob = CoroutineScope(parentJob).launch(Dispatchers.Default + handler) {

            println("🎃 startJob() parentJob: $parentJob, currentJob: $currentJob")
            println("😛 Inside coroutineScope.launch() scope: $this, coroutineContext job: ${this.coroutineContext[Job]}}")

            // This scope is not scope with coroutineScope used to call launch function
            startChildrenJobs(this@launch)
            displayAlphabet(binding.tvJobResult)

        }

        // Invoked when a job completes from 🔥 Thread exception caught
        currentJob?.invokeOnCompletion {

            println("invokeOnCompletion() 💀 Exception $it, in thread: ${Thread.currentThread().name}")

            it?.let {

                runOnUiThread {
                    binding.tvJobResult.text = it.message
                    binding.tvChildJobResult1.text = it.message
                    binding.tvChildJobResult2.text = it.message
                }

            }
        }

    }

    private fun cancelJob() {

        binding.btnCancelChildJob1.isEnabled = false
        binding.btnCancelChildJob2.isEnabled = false

        currentJob?.cancel()
    }


    private suspend fun startChildrenJobs(myCoroutineScope: CoroutineScope) {

        childJob1 = myCoroutineScope.launch {
            displayAlphabet(binding.tvChildJobResult1)
        }

        /*
            🔥🔥 Let's this launch to have SupervisorJob to only cancel this courutine instead
            of parent and other children coroutines
         */

        val childSupervisorJob = SupervisorJob()
        childJob2 = myCoroutineScope.launch(childSupervisorJob ) {

            launch {
                displayNumbers(binding.tvChildJobResult2)
            }

            delay(2000)
            throw RuntimeException("Child 2 threw RuntimeException")

        }

        withContext(Dispatchers.Main) {
            binding.btnCancelChildJob1.isEnabled = true
            binding.btnCancelChildJob2.isEnabled = true
        }

        println(
            "🤪 startChildrenJobs() parentJob: $parentJob, currentJob: $currentJob" +
                    ", childJob1: $childJob1, childJob2: $childJob2"
        )
    }


    private suspend fun displayAlphabet(textView: TextView) {

        for (i in 'A'..'Z') {

            withContext(Dispatchers.Main) {
                textView.text = "Progress: $i"
            }

            delay(400)

        }

        withContext(Dispatchers.Main) {
            textView.text = "Job Completed"
        }

    }

    private suspend fun displayNumbers(textView: TextView) {

        for (i in 0..100) {

            withContext(Dispatchers.Main) {
                textView.text = "Progress: $i"
            }

            delay(300)
        }

        withContext(Dispatchers.Main) {
            textView.text = "Job Completed"
        }

    }

}

