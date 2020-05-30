package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter1_basics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.databinding.Fragment3BasicsBinding


/** 🔥 CoroutineScope(IO).launch { }
 *  A coroutine is typically launched using launch coroutine builder. It is defined as extension function on CoroutineScope class.
 *
 * 🔥 delay(5000) : Delays coroutine for 5 second without blocking a thread and resumes it after 5 second.
 *
 *  🔥 withContext(Main)  -> switches the context of coroutine to Main thread. In other words, it changes the thread which Coroutine works on to Main UI thread.
 * */
class Fragment3Basics : Fragment() {

    companion object {
        fun newInstance() = Fragment3Basics()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<Fragment3BasicsBinding>(
            inflater,
            R.layout.fragment3_basics,
            container,
            false
        )

        binding.button5.setOnClickListener {

        }

        return binding.root
    }


}