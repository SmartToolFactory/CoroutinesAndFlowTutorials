package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter1_basics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.databinding.Fragment1BasicsBinding
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class Fragment1Basics : Fragment() {

    lateinit var binding: Fragment1BasicsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<Fragment1BasicsBinding>(
            inflater,
            R.layout.fragment1_basics,
            container,
            false
        )

        binding.button1.setOnClickListener {
            binding.tvResult.text = "Waiting result..."
            displayResult()
        }

        binding.buttonASync.setOnClickListener {
            binding.tvResultAsync.text = "Waiting result..."
            displayResultAsync()
        }

        binding.buttonSync.setOnClickListener {
            binding.buttonSync.isEnabled = false
            binding.tvResultSync.text = "Waiting result..."
            displayResultSync()

        }

        return binding.root
    }

    companion object {
        fun newInstance() = Fragment1Basics()
    }

    /**
     * Gets result with launch sequentially
     */
    private fun displayResult() {


        GlobalScope.launch {

            var output1 = 0
            var output2 = 0

            val time = measureTimeMillis {
                output1 = getResult(3)
                output2 = getResult(5)
            }

            // withBindingContext changes CoroutineContext(thread currently coroutine is)
            withContext(Dispatchers.Main) {
                binding.tvResult.text = "Result with suspend only: ${output1 + output2} in $time ms"

            }

        }
    }

    /**
     * Uses [async] to get result asynchronously.
     */
    private fun displayResultAsync() {

        GlobalScope.launch {

            var result = 0

            val time = measureTimeMillis {

                val deferred1 = async { getResult(3) }
                val deferred2 = async { getResult(5) }

                result = deferred1.await() + deferred2.await()
            }

            // withBindingContext changes CoroutineContext(thread currently coroutine is)
            withContext(Dispatchers.Main) {
                binding.tvResultAsync.text =
                    "Result with async: $result in $time ms"
            }
        }
    }

    /**
     * Despite using [async] runs synchronously since [async] calls [Deferred.await] immediately
     */
    private fun displayResultSync() {

        GlobalScope.launch {

            var output1 = 0
            var output2 = 0

            val time = measureTimeMillis {

                output1 = async { getResult(3) }.await()
                output2 = async { getResult(5) }.await()
            }

            // withBindingContext changes CoroutineContext(thread currently coroutine is)
            withContext(Dispatchers.Main) {
                binding.tvResultSync.text =
                    "Result with async: ${output1 + output2} in $time ms"
                binding.buttonSync.isEnabled = true

            }
        }
    }


    private suspend fun getResult(input: Int): Int {
        delay(1000)
        return input * 10

    }

}