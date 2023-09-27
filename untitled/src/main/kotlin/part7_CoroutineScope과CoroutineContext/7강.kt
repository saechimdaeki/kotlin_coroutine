package part7_CoroutineScope과CoroutineContext

import kotlinx.coroutines.*
import part1_루틴과코루틴.printWithThread
import java.util.concurrent.Executors

suspend fun example() {
    val job = CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        printWithThread("Job1")
    }
    job.join()
}

fun example3() {
    CoroutineName("나만의 코루틴") + Dispatchers.Default
}

suspend fun example2(){
    val job = CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        printWithThread("Job1")
        coroutineContext + CoroutineName("이름")
        coroutineContext.minusKey(CoroutineName.Key)
    }
    job.join()
}

fun main() {
    CoroutineName("나만의 코루틴") + Dispatchers.Default
    val threadPool = Executors.newSingleThreadExecutor()
    CoroutineScope(threadPool.asCoroutineDispatcher()).launch {
        printWithThread("코루틴 시작")
    }
}


class AsyncLogic {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun doSomething() {
        scope.launch {
            // 무언가 코루틴이 시작되어 작업!
        }
    }

    fun destroy() {
        scope.cancel()
    }
}