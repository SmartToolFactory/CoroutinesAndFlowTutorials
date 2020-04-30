package com.smarttoolfactory.tutorial1_1coroutinesbasics.chapter1_basics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smarttoolfactory.tutorial1_1basics.R
import com.smarttoolfactory.tutorial1_1basics.databinding.Activity1BasicsBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis


/**
 * * **Coroutine context**
 * The coroutine context is a set of rules and configurations that define
 * how the coroutine will be executed.
 * Under the hood, it’s a kind of map, with a set of possible keys and values.
 *
 * Coroutine context is immutable, but you can add elements to a context using plus operator,
 * just like you add elements to a set, producing a new context instance
 *
 * The CoroutineContext is a set of elements that define the behavior of a coroutine. It’s made of:
 * * Job — controls the lifecycle of the coroutine.
 * * CoroutineDispatcher — dispatches work to the appropriate thread.
 * * CoroutineName — name of the coroutine, useful for debugging.
 * * CoroutineExceptionHandler — handles uncaught exceptions, will be covered in Part 3 of the series.
 *
 * What’s the CoroutineContext of a new coroutine?
 * We already know that a **new instance of Job** will be created,
 * allowing us to control its lifecycle.
 *
 * The rest of the elements will be inherited from the CoroutineContext of
 * its parent (either another coroutine or the CoroutineScope where it was created).
 *
 * * **Dispatchers**
 * Dispatchers determine which thread pool should be used. Dispatchers class is also
 * [CoroutineContext] which can be added to [CoroutineContext]
 *
 * **Dispatchers.Default**: CPU-intensive work, such as sorting large lists,
 * doing complex calculations and similar. A shared pool of threads on the JVM backs it.
 *
 * **Dispatchers.IO**: networking or reading and writing from files.
 * In short – any input and output, as the name states
 *
 * **Dispatchers.Main**: recommended dispatcher for performing UI-related events.
 * For example, showing lists in a RecyclerView, updating Views and so on.
 *
 * * **Job** A coroutine itself is represented by a Job.
 * A Job is a handle to a coroutine. For every coroutine that you create (by launch or async),
 * it returns a Job instance that uniquely identifies the coroutine and manages its lifecycle.
 * You can also pass a Job to a CoroutineScope to keep a handle on its lifecycle.
 *
 * It is responsible for coroutine’s lifecycle, cancellation, and parent-child relations.
 * A current job can be retrieved from a current coroutine’s context:
 * A Job can go through a set of states: New, Active, Completing, Completed, Cancelling and Cancelled.
 * While we don’t have access to the states themselves,
 * we can access properties of a Job: isActive, isCancelled and isCompleted.
 *
 * * **CoroutineScope** It is defined as extension function on CoroutineScope
 * and takes a CoroutineContext as parameter, so it actually takes two
 * coroutine contexts (since a scope is just a reference to a context).
 * What does it do with them? It merges them using plus operator,
 * producing a set-union of their elements, so that the elements
 * in context parameter are taking precedence over the elements from the scope.
 *
 * The resulting context is used to start a new coroutine,
 * but it is **not the context of the new coroutine** — is the **parent context** of the new coroutine.
 * The new coroutine creates **its own child Job instance**
 * (using a job from this context as its parent) and defines its child
 * context as a parent context plus its job:
 */


/*
    Dispatchers
    Dispatchers are coroutine contexts that specify the thread or threads that can be used by
    the coroutine to run its code. There are dispatchers that just use one thread (like Main)
    and others that define a pool of threads that will be optimized to run all the coroutines they receive.


    We have four main dispatchers:

    🔥 Default: It will be used when no dispatcher is defined, but we can set it explicitly too.
    This dispatcher is used to run tasks that make intensive
    use of the CPU, mainly App computations, algorithms, etc.
    It can use as many threads as CPU cores. As these are intensive tasks,
     it doesn’t make sense to have more running at the same time, because the CPU will be busy

    🔥 IO: You will use this one to run input/output operations. In general, all tasks that
    will block the thread while waiting for an answer from another system:
    server requests, access to database, files, sensors…
    As they don’t use the CPU, you can have many running at the same time,
    so the size of this thread pool is 64. Android Apps are all about
    interaction with the device and network requests,
    so you probably will use this one most of the time.

    🔥 Unconfined: if you don’t care much what thread is used, you can use this one.
    It’s difficult to predict what thread will be used,
    so don’t use it unless you’re very sure of what you’re doing

    🔥Main: this is a special dispatcher that is included in UI related coroutine libraries.
    In particular, in the Android one, it will use the UI thread.

 */

/*
    Builder Functions
    🔥 launch: The exception propagates to the parent and will fail your coroutine
    parent-child hierarchy. This will throw an exception in the coroutine thread immediately.
    You can avoid these exceptions with try/catch blocks, or a custom exception handler.

    🔥 async: You defer exceptions until you consume the result for the async block.
    That means if you forgot or did not consume the result of the async block, through await(),
     you may not get an exception at all! The coroutine will bury it, and your app will be fine.
     If you want to avoid exceptions from await(),
     use a try/catch block either on the await() call, or within async().

 */
class Activity1Basics : AppCompatActivity() {

    private lateinit var binding: Activity1BasicsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity1_basics)


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