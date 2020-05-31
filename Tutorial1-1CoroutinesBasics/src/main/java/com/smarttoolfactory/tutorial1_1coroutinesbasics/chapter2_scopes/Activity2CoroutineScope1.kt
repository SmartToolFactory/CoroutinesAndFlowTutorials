package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter2_scopes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.databinding.Activity2Scope1Binding
import kotlinx.android.synthetic.main.activity2_scope_1.*
import kotlinx.coroutines.*

class Activity2CoroutineScope1 : AppCompatActivity() {

    private lateinit var binding: Activity2Scope1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity2_scope_1)

        binding.button4.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                fakeApiRequest()
            }
        }
    }

    private suspend fun fakeApiRequest() {
        val result1 = getResult1FromApi()
        println("test123 - result : $result1")
        setTextOnMainThread(result1)

        val result2 = getResult2FromApi()
        setTextOnMainThread(result2)
        println("test123 - result: $result2")
    }

    private suspend fun getResult1FromApi(): String {
        println("test123 - getResult1FromApi : ${Thread.currentThread().name}")
        delay(2000)
        return "\nResult 1"
    }

    private suspend fun getResult2FromApi(): String {
        println("test123 - getResult2FromApi : ${Thread.currentThread().name}")
        delay(2000)
        return "\nResult 2"
    }

    private fun setNewText(newString: String) {
        val newText = textView.text.toString() + newString
        textView.text = newText
    }

    private suspend fun setTextOnMainThread(newString: String) {
        withContext(Dispatchers.Main) {
            println("test123 - setTextOnMainThread : ${Thread.currentThread().name}")
            delay(2000)
            setNewText(newString)
        }
    }

}

