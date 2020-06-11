import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/** examples from resocoder */
fun main() {
    exampleLaunchGlobal()
    exampleLaunchGlobalWaiting()
}


fun exampleLaunchGlobal() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    GlobalScope.launch {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")
    delay(3000)
}

fun exampleLaunchGlobalWaiting() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    val job = GlobalScope.launch {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")
    job.join()
}