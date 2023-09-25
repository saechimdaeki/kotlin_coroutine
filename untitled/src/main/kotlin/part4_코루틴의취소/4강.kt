package part4_코루틴의취소

import kotlinx.coroutines.*
import part1_루틴과코루틴.printWithThread

fun example1() : Unit = runBlocking {
    val job1 = launch {
        delay(10)
        printWithThread("Job1")
    }

    delay(100)
    job1.cancel()
}

fun example2() : Unit = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (nextPrintTime <= System.currentTimeMillis()) {
                printWithThread("${i++} 번째 출력")
                nextPrintTime += 1000L
            }

            if (!isActive) {
                throw CancellationException()
            }
        }
    }


    delay(100)
    job.cancel()
}

fun main() : Unit = runBlocking {
    val job = launch {
        try {
            delay(100L)
        }catch (e: CancellationException) {
            // 아무것도 안한다
        } finally {
            // 필요한 자원을 닫을 수도 있다

        }

        printWithThread("delay에 의해 취소되지 않았다")
    }

    delay(100)
    printWithThread("취소 시작")
    job.cancel()
}