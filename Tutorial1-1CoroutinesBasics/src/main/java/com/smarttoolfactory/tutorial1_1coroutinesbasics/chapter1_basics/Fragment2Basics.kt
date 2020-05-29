package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter1_basics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.databinding.Fragment2BasicsBinding
import kotlinx.android.synthetic.main.fragment2_basics.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/** 🔥 CoroutineScope(IO).launch { }
 *  A coroutine is typically launched using launch coroutine builder. It is defined as extension function on CoroutineScope class.
 *
 * 🔥 delay(5000) : Delays coroutine for 5 second without blocking a thread and resumes it after 5 second.
 *
 *  🔥 withContext(Main)  -> switches the context of coroutine to Main thread. In other words, it changes the thread which Coroutine works on to Main UI thread.
 * */
class Fragment2Basics : Fragment() {

    companion object {
        fun newInstance() = Fragment2Basics()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<Fragment2BasicsBinding>(inflater, R.layout.fragment2_basics, container, false)

        binding.buttonShowFirstFragment.setOnClickListener {
            activity?.run {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, Fragment1Basics.newInstance(), null)
                    .commit()
            }

        }

        binding.button4.setOnClickListener{
            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }

        return binding.root
    }

    private suspend fun fakeApiRequest(){
        val result1 = getResult1FromApi()
        println("test123 - result : $result1")
        setTextOnMainThread(result1)

        val result2 = getResult2FromApi()
        setTextOnMainThread(result2)
        println("test123 - result: $result2")
    }

    private suspend fun getResult1FromApi() : String{
        println("test123 - getResult1FromApi : ${Thread.currentThread().name}")
        delay(2000)
        return "Result 1 \n"
    }

    private suspend fun getResult2FromApi() : String{
        println("test123 - getResult2FromApi : ${Thread.currentThread().name}")
        delay(2000)
        return "Result 2 \n"
    }

    private fun setNewText( newString : String){
        val newText = textView.text.toString() + newString
        textView.text = newText
    }

    private suspend fun setTextOnMainThread(newString: String){
        withContext(Main){
            println("test123 - setTextOnMainThread : ${Thread.currentThread().name}")
            delay(2000)
            setNewText(newString)
        }
    }

}