package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter1_basics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.databinding.Fragment2BasicsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking


/**
 * 🔥runBlocking
 * Runs a new coroutine and blocks the current thread interruptibly until its completion. This function should not be used from a coroutine.
 * 😱😱😱runBlocking is almost never a tool you use in production, because It undoes the asynchronous, non-blocking nature of coroutines.
 * You can use it if you happen to already have some coroutine-based code that you want to use in a context where coroutines provide no value: in blocking calls.
 * There are 2 typical use case for runBlocking :
 * 1 -  JUnit testing, where the test method must just sit and wait for the coroutine to complete.
 * 2 - Play around with coroutines, inside your main method.
 *
 * 🔥 CoroutineDispatcher
 * CoroutineDispatcher determines what thread or threads the corresponding coroutine uses for its execution.
 *
 * 🔥 CoroutineContext includes a coroutine dispatcher
 * The coroutine context includes a coroutine dispatcher (see CoroutineDispatcher) that determines what thread or threads the corresponding coroutine uses for its execution.
 * The coroutine dispatcher can confine coroutine execution to a specific thread, dispatch it to a thread pool, or let it run unconfined.
 *
 * 🔥 launch { }
 * fun CoroutineScope.launch(
 *                           context: CoroutineContext = EmptyCoroutineContext,
 *                           start: CoroutineStart = CoroutineStart.DEFAULT,
 *                           block: suspend CoroutineScope.() -> Unit
 *                           ): Job (source)
 *
 * Launches a new coroutine without blocking the current thread and returns a reference to the coroutine as a Job. The coroutine is cancelled when the resulting job is cancelled.
 * The coroutine context is inherited from a CoroutineScope. Additional context elements can be specified with context argument.
 *
 * All coroutine builders like launch and async accept an optional CoroutineContext parameter that can be used to explicitly specify the dispatcher for the new coroutine and other context elements.
 * When launch { ... } is used without parameters, it inherits the context (and thus dispatcher) from the CoroutineScope it is being launched from.
 * In this case, it inherits the context of the main runBlocking coroutine which runs in the main thread.
 *
 * */
class Fragment2Basics : Fragment() {

    companion object {
        const val TAG = "Fragment2Basic"
        fun newInstance() = Fragment2Basics()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<Fragment2BasicsBinding>(
            inflater,
            R.layout.fragment2_basics,
            container,
            false
        )

        binding.button5.setOnClickListener {

            runBlocking<Unit> {

                launch { // context of the parent, main runBlocking coroutine
                    println("$TAG main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
                }

                launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
                    println("$TAG Unconfined            : I'm working in thread ${Thread.currentThread().name}")
                }

                launch(Dispatchers.Default) { // will get dispatched to DefaultDispatcher
                    println("$TAG Default               : I'm working in thread ${Thread.currentThread().name}")
                }

                launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
                    println("$TAG newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
                }

            }

        }

        return binding.root
    }


}