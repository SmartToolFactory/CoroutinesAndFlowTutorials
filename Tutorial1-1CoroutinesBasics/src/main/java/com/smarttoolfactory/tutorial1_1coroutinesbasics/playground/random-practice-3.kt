import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

/** examples from resocoder */
fun main() {
    exampleAsyncAwait1()
    exampleAsyncAwait2()
}

fun exampleAsyncAwait1() = runBlocking {
    val startTime = System.currentTimeMillis()

    val result1 = async { calculateHardThings(10) }.await()
    val result2 = async { calculateHardThings(20) }.await()
    val result3 = async { calculateHardThings(30) }.await()

    val sum = result1 + result2 + result3
    println("async/await result = $sum")

    val endTime = System.currentTimeMillis()
    println("Time taken: ${endTime - startTime}")
}

fun exampleAsyncAwait2() = runBlocking {
    val startTime = System.currentTimeMillis()

    val deferred1 = async { calculateHardThings(10) }
    val deferred2 = async { calculateHardThings(20) }
    val deferred3 = async { calculateHardThings(30) }

    val sum = deferred1.await() + deferred2.await() + deferred3.await()
    println("async/await result = $sum")

    val endTime = System.currentTimeMillis()
    println("Time taken: ${endTime - startTime}")
}