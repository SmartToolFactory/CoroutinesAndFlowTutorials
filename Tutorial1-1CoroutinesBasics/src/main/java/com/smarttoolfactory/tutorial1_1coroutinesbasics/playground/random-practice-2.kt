import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/** examples from resocoder */
fun main() {
    exampleLaunchCoroutineScope1()
    exampleLaunchCoroutineScope2()
}

fun exampleLaunchCoroutineScope1() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    launch {
        delay(4000)
        println("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")
}

fun exampleLaunchCoroutineScope2() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

    launch(customDispatcher) {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")

    (customDispatcher.executor as ExecutorService).shutdown()
}


suspend fun printlnDelayed(message: String) {
    // Complex calculation
    delay(1000)
    println(message)
}