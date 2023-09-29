package part8_suspending_function

import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import part1_루틴과코루틴.printWithThread
import java.util.concurrent.CompletableFuture

fun example1() : Unit = runBlocking {
    val result1 = call1()

    val result2 = call2(result1)

    printWithThread(result2)
}

suspend fun call1() : Int {
    return CoroutineScope(Dispatchers.Default).async {
        Thread.sleep(1000)
        1
    }.await()
}

suspend fun call2(num: Int) : Int {
    return CompletableFuture.supplyAsync {
        Thread.sleep(1000)
        100
    }.await()
}

interface AsyncCaller {
    suspend fun call()
}

class AsyncCallerImpl : AsyncCaller {
    override suspend fun call() {
        // 무언가를 비동기로 호출
    }
}


fun main() = runBlocking {
    printWithThread("START")
    calculateResult()
    printWithThread("END")
}

suspend fun calculateResult() : Int = withContext(Dispatchers.Default) {
    val num1 = async {
        delay(1000)
        10
    }

    val num2 = async {
        delay(1000)
        20
    }

    num1.await() + num2.await()
}