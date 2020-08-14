import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/** examples from resocoder */
fun main() {
    exampleWithContext()
}

fun exampleWithContext() = runBlocking {
    val startTime = System.currentTimeMillis()

    val result1 = withContext(Dispatchers.Default) { calculateHardThings(10) }
    println("1 done")
    val result2 = withContext(Dispatchers.IO) { calculateHardThings(20) }
    println("2 done")
    val result3 = withContext(Dispatchers.IO) { calculateHardThings(30) }
    println("3 done")

    val sum = result1 + result2 + result3
    println("async/await result = $sum")

    val endTime = System.currentTimeMillis()
    println("Time taken: ${endTime - startTime}")
}

suspend fun calculateHardThings(startNum: Int): Int {
    delay(1000)
    return startNum * 10
}