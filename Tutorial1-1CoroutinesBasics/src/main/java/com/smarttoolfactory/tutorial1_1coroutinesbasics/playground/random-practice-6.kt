import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/** To get the result from coroutine, you need to start the coroutine using async{ }
 *  await() function awaits until the job is finished and gives you the result back
 *  The last statement in the async block becomes your return statement.
 * */
fun main() {
    runBlocking {
        val deferredResult: Deferred<String> = async {
            println("context of async : ${Thread.currentThread().name}")
            delay(3000L)
            "Veggie treat"
        }
        println("Your coke is ready, waiting for burger..")
        println("Here is your burger, ${deferredResult.await()}")
        println("--THE END--")
    }
}

