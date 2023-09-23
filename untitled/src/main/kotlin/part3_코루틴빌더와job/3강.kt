package part3_코루틴빌더와job

import kotlinx.coroutines.*
import part1_루틴과코루틴.printWithThread
import kotlin.system.measureTimeMillis

fun example1() {
    runBlocking {
        printWithThread("START")
        launch {
            delay(2000) // yield()와 같은 기능이지만 특정 시간만큼 멈추고 다른 코루틴으로 넘기는 것
            printWithThread("Launch End")
        }
    }

    printWithThread("END")
}

fun example2(): Unit = runBlocking {
    val job = launch(start = CoroutineStart.LAZY) {
        printWithThread("Hello launch")
    }
    delay(1000)
    job.start()
}


fun example3(): Unit = runBlocking {
    val job = launch {
        (1..5).forEach {
            printWithThread(it)
            delay(500)
        }
    }

    delay(1_000)
    job.cancel()
}

fun example4(): Unit = runBlocking {
    val job1 = launch {
        delay(1000)
        printWithThread("Job 1")
    }

    job1.join()

    val job2 = launch {
        delay(1000)
        printWithThread("Job 2")
    }

}

fun example5(): Unit = runBlocking {
   val job = async {
       3 + 5
   }
    val eight = job.await() // await: async의 결과값을 반환
    println(eight)
}


fun example6(): Unit = runBlocking {

    val time = measureTimeMillis {

        val job1 = async { apiCall1() }
        val job2 = async { apiCall2(job1.await()) }
        printWithThread(job2.await())
    }
    printWithThread("Completed in $time ms")
}

fun main(): Unit = runBlocking {

    val time = measureTimeMillis {

        val job1 = async(start = CoroutineStart.LAZY) { apiCall1() }
        val job2 = async(start = CoroutineStart.LAZY) { apiCall2(job1.await()) }
        job1.start()
        job2.start()
        printWithThread(job2.await())
    }
    printWithThread("Completed in $time ms")
}

suspend fun apiCall1() : Int {
    delay(1000)
    return 1
}

suspend fun apiCall2(num: Int) : Int {
    delay(1000)
    return 2 + num
}