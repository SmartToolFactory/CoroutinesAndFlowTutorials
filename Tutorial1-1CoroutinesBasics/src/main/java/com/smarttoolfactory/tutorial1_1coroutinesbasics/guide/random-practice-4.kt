import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/** examples from resocoder */
fun main() {
    exampleBlocking()
    exampleBlockingDispatcher()
}


fun exampleBlocking() = runBlocking {
    println("one")
    printlnDelayed("two")
    println("three")
}

// Running on another thread but still blocking the main thread
fun exampleBlockingDispatcher() {
    runBlocking(Dispatchers.Default) {
        println("one - from thread ${Thread.currentThread().name}")
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }
    // Outside of runBlocking to show that it's running in the blocked main thread
    println("three - from thread ${Thread.currentThread().name}")
    // It still runs only after the runBlocking is fully executed.
}