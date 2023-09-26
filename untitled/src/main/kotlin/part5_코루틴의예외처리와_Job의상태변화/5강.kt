package part5_코루틴의예외처리와_Job의상태변화

import kotlinx.coroutines.*
import part1_루틴과코루틴.printWithThread

fun example1(): Unit = runBlocking {
    val job1 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        printWithThread("Job1")
    }

    val job2 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        printWithThread("Job2")
    }
}

fun example2(): Unit = runBlocking {
    val job = CoroutineScope(Dispatchers.Default).async {
        throw IllegalArgumentException()
    }

    delay(1000)
    job.await()
}


fun example3() = runBlocking {
    val job = async {
        throw IllegalArgumentException()
    }

    delay(1000)
}

fun example4() = runBlocking {
    val job = async(SupervisorJob()) {
        throw IllegalArgumentException()
    }

    delay(1000)
}

fun example5() = runBlocking {
    val job = launch {
        try{
            throw IllegalArgumentException()
        } catch (e: IllegalArgumentException) {
            printWithThread("정상 종료")
        }
    }
}

fun main() = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        printWithThread("예외")
    }

    val job  = CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
        throw IllegalArgumentException()
    }

    delay(1000)
}