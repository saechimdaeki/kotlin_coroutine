# part1 루틴과 코루틴

### 코루틴이란?

`co-routine`, 협력하는 루틴

### 루틴과 코루틴의 차이

```kotlin
fun main() {
    println("START")
    newRoutine()
    println("END")
}

fun newRoutine() {
    val num1 = 1
    val num2 = 2
    println("${num1 + num2}")
}
```

메모리 관점으로 보면 새로운 루틴이 호출되면, newRoutine이 사용하는 스택에

지역변수가 초기화되고 `루틴이 끝나면 해당 메모리에 접근이 불가능하다`

즉 루틴에 진입하는 곳이 한 군데이며, 종료되면 해당 루틴의 정보가 초기화된다

### 하지만 코루틴은!

협력하는 루틴(=코드 모음)

```kotlin
fun main() : Unit = runBlocking {
    println("START")
    launch { newRoutine() }
    yield()
    println("END")
}

suspend fun newRoutine(){
    val num1 = 1
    val num2 = 2
    yield()
    println("${num1 + num2}")
}
```

`runBlocking` - 일반루틴 세계와 코루틴 세계를 연결한다. 이 함수 자체로 `새로운 코루틴`을 만든다

`launch` - `반환값이 없는` 코루틴을 만든다

`suspend fun` - 다른 suspend fun을 호출할 수 있다

`yield` - 지금 코루틴을 중단하고 다른 코루틴이 실행되도록 한다 (스레드를 양보한다)

메모리 관점 : 새로운 루틴이 호출된 후, 완전히 종료되기 전, `해당 루틴에서 사용했던 정보들을 보관`하고 있어야 한다

`루틴이 중단되었다가 해당 메모리에 접근이 가능하다`

### 루틴과 코루틴의 차이 정리

`루틴` - 시작되면 끝날 때 까지 멈추지 않는다. 한 번 끝나면 루틴 내의 정보가 사라진다

`코루틴` - 중단되었다가 재개될 수 있다. 중단되더라도 루틴 내의 정보가 사라지지 않는다



# part2 스레드와 코루틴

### 프로세스

컴퓨터에서 실행되고 있는 프로그램

### 스레드

프로세스보다 작은 개념. 프로세스에 소속되어 여러 코드를 동시에 실행할 수 있게 해준다

### 스레드와 코루틴의 차이

프로세스가 스레드보다 큰 개념이듯이 스레드가 코루틴보다 큰 개념이다

`다만 스레드-코루틴을 프로세스-스레드에 빗대기에는 다른 점이 있다`

프로세스가 있어야만 스레드가 있을 수 있고 스레드가 프로세스를 바꿀 순 없다

코루틴의 코드가 실행되려면, 스레드가 있어야만한다

하지만, 중단되었다가 재개될 때 다른 스레드에 배정될 수 있다

스레드는 특정 프로세스에 종속되어 있지만 코루틴은 Thread에 종속되어 있는 개념이 아니라 코루틴의 코드를 어떤 스레드건 가져갈 수 있다

`context switching에서도 차이가 존재한다`

### 프로세스 context switching이 일어날 때 특징

프로세스는 독립된 메모리를 가지고 있다. 모든 메모리가 교체되므로 비용이 많이 발생

### 스레드 context switching이 일어날 때 특징

같은 프로세스에 소속되었기에 heap area를 공유하고 독립적인 stack area를 가진다. stack area만 교체되므로 프로세스보다 비용이 적게 발생

### 코루틴 context switching이 일어날 때 특징

동일 스레드에서 코루틴이 실행되면, 메모리 전부를 공유하므로 스레드보다 context switching cost가 낮다

하나의 스레드에서도 `동시성`을 확보할 수 있다

### 동시성

한번에 한가지 일만 할 수 있지만 아주 빠르게 작업이 전환되어 동시에 하는 것처럼 보이는 것을 의미

### 병렬성

실제로 2가지 일을 동시에 하는 것(CPU multi-core)

### 코루틴은 스스로 자리를 양보할 수 있다 (yield())

### 스레드와 코루틴의 차이 정리

1. 스레드는 프로세스보다 작은개념. 코루틴은 스레드보다 작은 개념
2. 한스레드는 오직 한 프로세스에만 포함되어 있다. 한 코루틴의 코드는 여러 스레드에서 실행될 수 있다
3. 스레드는 context switching발생시 stack영역이 교체. 코루틴은 (한스레드에서 실행되는경우 )context switchign시 메모리 교체가 없다
4. 스레드는 os가 스레드를 강제로 멈추고 다른 스레드를 실행한다. 코루틴은 스스로가 다른 코루틴에게 양보한다

# part 3 코루틴 빌더와 Job

## 1. runBlocking

```kotlin
fun main() = runBlocking {
    
}
```

새로운 코루틴을 만들고, 루틴 세계와 코루틴 세계를 이어준다. (이름에 blocking이 들어가 있다!!)

RunBlocking으로 인해 만들어진 Coroutine과 안에서 추가적으로 만든 Coroutine이 모두 다 완료될 때 까지 스레드를 Blocking한다

스레드는 RunBlocking에 의해 Blocking된 게 풀릴때까지 다른 코드를 실행할 수 없다

```kotlin
fun main() {
    runBlocking {
        printWithThread("START")
        launch {
            delay(2000) // yield()와 같은 기능이지만 특정 시간만큼 멈추고 다른 코루틴으로 넘기는 것
            printWithThread("Launch End")
        }
    }
    printWithThread("END")
}
```
End가 출력되려면 아무 의미없이 2초를 기다려야함. 그러기에 runBlocking의 경우 프로그램에 진입할 때 최초로 작성해주거나 특정 테스트 코드에서 사용하는 것이 좋다

## 2. launch

```kotlin
       val job : CoroutineScope =  launch {
            printWithThread("Launch End")
        }
```

반환값이 없는 코드를 실행한다. 위 코드에서 job은 코루틴을 제어할 수 있는 객체 Job!

```kotlin
fun main(): Unit = runBlocking {
    val job = launch(start = CoroutineStart.LAZY) {
        printWithThread("Hello launch")
    }
    delay(1000)
    job.start()
}
```

위 코드는 1초 기다렸다가 Job을 실행시키는 제어를한다

```kotlin
fun main(): Unit = runBlocking {
    val job = launch {
        (1..5).forEach {
            printWithThread(it)
            delay(500)
        }
    }

    delay(1_000)
    job.cancel()
}
```

다음처럼 job을 cancel시킬 수도 있다 위 코드에서 출력은 2까지 노출

```kotlin
fun main(): Unit = runBlocking {
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
```

위 코드는 job1이 완전히 끝나고 job2를 실행시킨다

### Job 객체 활용 정리

- start() : 시작 신호
- cancel() : 취소 신호
- join() : 코루틴이 완료될 때까지 대기

## 3. async

```kotlin
fun main(): Unit = runBlocking {
    val job = async {
        3 + 5
    }
    val eight = job.await() // await: async의 결과값을 반환
    println(eight)
}
```

주어진 함수의 실행 결과를 반환할 수 있다

### 활용 예시 

```kotlin
fun main(): Unit = runBlocking {

    val time = measureTimeMillis {


        val job1 = async { apiCall1() }
        val job2 = async { apiCall2() }

        printWithThread("Result: ${job1.await() + job2.await()}")
    }
    printWithThread("Completed in $time ms")
}

suspend fun apiCall1() : Int {
    delay(1000)
    return 1
}

suspend fun apiCall2() : Int {
    delay(1000)
    return 2
}
```

### async 활용

여러 API를 동시에 호출하여 소요시간을 최소화할 수 있다

callback을 이용하지 않고 `동기 방식`으로 코드를 작성할 수 있다

### async 주의 사항

CoroutineStart.LAZY 옵션을 사용하면, await()함수를 호출했을 때 계산 결과를 계속 기다린다

CoroutineStart.LAZY 옵션을 사용후, start() 함수를 한 번 호출하면 괜찮다

# part4 코루틴의 취소

### 코루틴을 적절히 취소하는 것은 중요하다

필요하지 않은 코루틴을 적절히 취소해 컴퓨터 자원을 아껴야 한다

(만약 필요하지 않은 코루틴을 취소하지 않는다면 그 코루틴이 계속 사용됨으로써 cpu나 메모리 자원을 잡아먹기도함)

### cancel() 함수를 활용하면 되지만...

코루틴도 취소에 협조를 해 주어야한다

### 취소에 협조하는 방법 1

delay()/ yield() 같은 kotlinx.coroutines 패키지의 suspend 함수를 사용!

```kotlin
fun main() : Unit = runBlocking {
    val job = launch {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (nextPrintTime <= System.currentTimeMillis()) {
                printWithThread("${i++} 번째 출력")
                nextPrintTime += 1000L
            }
        }
    }

    delay(100L)
    job.cancel()
}
```
위와 같은 코드는 취소가 되지 않음을 알 수 있는데 여기서 바로 협력하는 Coroutine이어야만 취소가 가능하다

### 취소에 협조하는 방법 2

코루틴 스스로 본인의 상태를 확인해 취소 요청을 받았다면, `CancellationException`을 던지자

```kotlin
fun main() : Unit = runBlocking {
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

```

launch옵션에 Dispathers.Default를 주면 이 코루틴은 다른 스레드에서 동작하고 메인 스레드와는 별개의 스레드가 된다

- isActive: 현재 코루틴이 활성화되어 있는지, 취소 신호를 받았는지
- Dispatchers.Default : 우리의 코루틴을 다른 스레드에 배정

## 코루틴이 취소에 협조하는 방법 정리
1. kotlinx.coroutines 패키지의 suspend 함수를 호출
2. isActive로 CancellationException을 던지기

### 사실 delay() 같은 함수도

`CancellationException` 예외를 던져 취소하고 있었다

```kotlin
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
```

그래서 Coroutine 취소의 첫번째 방법인 Suspend fun을 호출하는 방법은 try-catch-finally의 영향을 받는다


# part 5 코루틴의 예외 처리와 Job의 상태 변화

```kotlin
fun main() : Unit = runBlocking { 
    val job1 = launch{
        delay(1000)
        printWithThread("Job1")
    }
    
    val job2 = launch {
        delay(1000)
        printWithThread("Job2")
    }
}
```

### 현재 중첩된 코루틴 간의 관계는 

```markdown
runBlocking (부모 코루틴) -> launch (자식 코루틴)
                       -> launch (자식 코루틴)
```

### 새로운 root 코루틴을 만들고 싶다면? 

새로운 영역(CoroutineScope)을 만들어야한다

```kotlin
fun main() : Unit = runBlocking {
    val job1 = CoroutineScope(Dispatchers.Default).launch{
        delay(1000)
        printWithThread("Job1")
    }

    val job2 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        printWithThread("Job2")
    }
}
```

다음과 같이 작성하면 새로운 영역의 코루틴은 root 코루틴이 된다 

### launch와 async의 예외 발생 차이

- `launch` : 예외가 발생하면, 예외를 출력하고 코루틴이 종료
- `async` : 예외가 발생해도, 예외를 출력하지 않음. 예외를 확인하려면 await()이 필요함

### 참고로 자식 코루틴의 예외는 부모에게 전파된다

### 만약 자식 코루틴의 예외를 부모에게 전파하고 싶지 않다면?

`SupervisorJob()`

```kotlin
fun main() = runBlocking {
    val job = async(SupervisorJob()) {
        throw IllegalArgumentException()
    }

    delay(1000)
}
```

### 예외를 다루는 방법1

직관적인 try - catch - finally

```kotlin
fun main() = runBlocking {
    val job = launch {
        try{
            throw IllegalArgumentException()
        } catch (e: IllegalArgumentException) {
            printWithThread("정상 종료")
        }
    }
}
```

### 예외를 다루는 방법2

CoroutineExceptionHandler를 사용 (try catch와 달리 예외 발생 이후 에러 로깅/ 에러메시지 전송 등에 활용)

### CoroutineExceptionHandler의 두 가지 파라미터

```kotlin
val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
    printWithThread("예외 발생 : ${throwable.message}")
}
```

```kotlin
fun main() = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        printWithThread("예외")
    }

    val job  = CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
        throw IllegalArgumentException()
    }

    delay(1000)
}
```

### CoroutineExceptionHandler 주의할 점

launch에만 적용 가능하고, 부모 코루틴이 있으면 동작하지 않는다!!!!

### 그런데 궁금한점이 생긴다

취소도 `CancellationException`이라는 예외였는데 일반적인 예외랑 어떻게 다른걸까?

# 코루틴 취소 예외 한방정리

- CASE 1. 발생한 예외가 CancellationException인 경우 `취소`로 간주하고 부모 코루틴에게 전파 X
- CASE 2. 그 외 다른 예외가 발생한 경우 `실패`로 간주하고 부모 코루틴에게 전파
- 다만 내부적으로 취소나 실패 모두 `취소됨`상태로 관리함

### Job(코루틴)의 Life cycle

<img width="567" alt="image" src="https://github.com/saechimdaeki/Dev-Diary/assets/40031858/12d0e354-db2a-420d-a14e-59eb1bb35ec8">

# part6 Structured Concurrency

<img width="567" alt="image" src="https://github.com/saechimdaeki/Dev-Diary/assets/40031858/12d0e354-db2a-420d-a14e-59eb1bb35ec8">

잡의 상태가 바로 Completed가 아닌 ComPleting이 있는 이유는 다음과 같다

바로 `자식 코루틴을 기다려야 하기 때문`이다!!

```kotlin
fun main(): Unit = runBlocking {
    launch {
        delay(600L)
        printWithThread("A")
    }

    launch {
        delay(500L)
        throw IllegalArgumentException("코루틴 실패!")
    }
}
```

위 코드에서 첫번째 코루틴은 정상 동작할 것 같지만 그렇지 않다!

이유는 다음과 같다

### 자식 코루틴을 기다리다가 예외가 발생하면?!

예외가 부모로 전파되고 다른 자식 코루틴에게 취소 요청을 보낸다!

### 부모 - 자식 관계의 코루틴이 한 몸처럼 움직이는 것

이를 `Structured Concurrency`라고 한다

### Structured Concurrency

Structured Concurrency는 수많은 코루틴이 유실되거나 누수되지 않도록 보장한다

Structured Concurrency는 코드 내의 에러가 유실되지 않고 적절히 보고될 수 있도록 보장한다

## `취소와 예외. Structured Concurrency 총 정리`

자식 코루틴에서 예외가 발생할 경우, Structured Concurrency에 의해 부모 코루틴이 취소되고, 부모 코루틴의 다른 자식 코루틴들도 취소된다

자식 코루틴에서 예외가 발생하지 않더라도, 부모 코루틴이 취소되면, 자식 코루틴들이 취소된다

다만, CancellationException은 정상적인 취소로 간주하기 때문에 부모 코루틴에게 전파되지 않고, 부모 코루틴의 다른자식 코루틴을 취소시키지도 않는다

# part7 CoroutineScope과 CoroutineContext

```kotlin
fun main() : Unit = runBlocking {
    val job1 = CoroutineScope(Dispatchers.Default).launch{
        delay(1000)
        printWithThread("Job1")
    }
}
```

### 사실 launch / async는

CoroutineScope의 확장함수이다

지금까지 runBlocking이 코루틴과 루틴의 세계를 이어주며 CoroutineScope을 제공해주었다

### 우리가 직접 CoroutineScope을 만들면

runBlocking이 필요하지 않다

```kotlin
fun main() {
    CoroutineScope(Dispatchers.Default).launch { 
        delay(1000)
        printWithThread("Job1")
    }
    Thread.sleep(1500L)
}
```

### CoroutineScope의 주요 역할은 무엇일까?

CoroutineContrxt라는 데이터를 보관하는 것!

```kotlin
public interface CoroutineScope {
    public val coroutineContext: CoroutineContext
}
```

### CoroutineContext란?!

코루틴과 관련된 여러가지 데이터를 갖고 있다!

코루틴의 이름, CoroutineExceptionHandler, 코루틴 그 자체, CoroutineDispatcher

### Dispatcher

코루틴이 어떤 스레드에 배정될지를 관리하는 역할

## 중간 정리

- CoroutineScope : 코루틴이 탄생할 수 있는 영역
- CoroutineContext : 코루틴과 관련된 데이터를 보관

### 코루틴의 Structured Concurrency 기반 

<img width="608" alt="image" src="https://github.com/saechimdaeki/Dev-Diary/assets/40031858/f1f7da1c-a682-404a-9cfc-36ccffe53501">

이때 부모-자식 관계도 설정해준다

### 클래스 내부에서 독립적인 CoroutineScope을 관리

해당 클래스에서 사용하던 코루틴을 한 번에 종료시킬 수 있다

```kotlin
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
```

### CoroutineContext

Map + Set을 합쳐놓은 형태

key- value로 데이터 저장. 같은 key의 데이터는 유일

```kotlin
fun main() {
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
```

### CoroutineDispatcher

<img width="953" alt="image" src="https://github.com/saechimdaeki/Dev-Diary/assets/40031858/fabd9ca2-91b0-4d58-8128-31e67214f2a4">

코루틴을 스레드에 배정하는 역할

### Dispathcers.Default

가장 기본적인 디스패처, CPU자원을 많이 쓸 때 권장. 별다른 설정이 없다면 이 디스패처가 사용됨

### Dispatchers.IO

I/O작업에 최적화된 디스패처

### Dispatchers.Main

보통 UI 컴포넌트를 조작하기 위한 디스패처. 특정 의존성을 갖고 있어야 정상적으로 활용할 수 있다

### ExecutorService를 디스패처로

asCoroutineDispatcher() 확장함수 활용

```kotlin
fun main() {
    CoroutineName("나만의 코루틴") + Dispatchers.Default
    val threadPool = Executors.newSingleThreadExecutor()
    CoroutineScope(threadPool.asCoroutineDispatcher()).launch {
        printWithThread("코루틴 시작")
    }
}

```

# part 8 suspending function

### suspending function

suspend가 붙은 함수. 다른 suspend를 붙은 함수를 호출할 수 있다

### 어떻게 fun main 에서 delay()를 호출했을까?

```kotlin
fun main() : Unit = runBlocking {
    launch {
        delay(100L)
    }
}
```

### launch의 시그니처를 살펴보면

```kotlin
public fun CoroutineScope.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {}
```

여기서 ` block: suspend CoroutineScope.()` 이 부분을 suspending lambda라고 함

### suspend 함수는

코루틴이 중지되었다가 재개 될 수 있는 지점. (suspending function)

### suspending function 함수의 활용

여러 비동기 라이브러리를 사용할 수 있도록 도와준다

```kotlin
fun main() : Unit = runBlocking {
    val result1 = async {
        call1()
    }

    val result2 = async {
        call2(result1.await())
    }

    printWithThread(result2.await())
}

fun call1() : Int {
    Thread.sleep(1000)
    return 1
}

fun call2(num: Int) : Int {
    Thread.sleep(1000)
    return num * 2
}
```

suspend를 통해 다음 코드를 작성할 수 있다 (main에서는 의존성이 없는 코드를 작성할 수 있다)

```kotlin
fun main() : Unit = runBlocking {
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
```

## 추가적인 suspend 함수들

- CoroutineScope : 추가적인 코루틴을 만들고, 주어진 함수 블록이 바로 실행된다. 만들어진 코루틴이 모두 완료되면 다음 코드로 넘어간다
- withContext : coroutineScope와 기본적으로 유사하다. context에 변화를 주는 기능이  추가적으로 있다
```kotlin
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
```

withContext는 추가적인 원소를 덮처쓰고 싶을때 사용

- withTimeout/ withTimeoutOrNull : 주어진 시간 안에 코루틴이 완료되지 못하면 예외를 던지게/ null을 반환하게 된다

# part 9 코루틴과 Continuation

```kotlin
class UserServiceV2 {

    private val userProfileRepository = UserProfileRepositoryV2()
    private val userImageRepository = UserImageRepositoryV2()

    suspend fun findUser(userId: Long): UserDtoV2 {
        // 0단계 - 초기 시작 
        println("유저를 가져오겠습니다")
        val profile = userProfileRepository.findProfile(userId)
        // 1단계 - 1차 중단 후 재시작
        println("이미지를 가져오겠습니다")
        val image = userImageRepository.findImage(profile)
        // 2단계 - 2차 중단 후 재시작
        return UserDtoV2(profile, image)
    }

}

data class UserDtoV2(
    val profile: ProfileV2,
    val image: ImageV2,
)


class UserProfileRepositoryV2 {
    suspend fun findProfile(userId: Long): ProfileV2 {
        delay(100L)
        return ProfileV2()
    }
}

class ProfileV2

class UserImageRepositoryV2 {
    suspend fun findImage(profile: ProfileV2): ImageV2 {
        delay(100L)
        return ImageV2()
    }
}
```

### 9강은 part9_코루틴과_Continuation 참고

### 실제 Continuation 인터페이스

```kotlin

public interface Continuation<in T> {
    public val context: CoroutineContext
    
    public fun resumeWith(result: Result<T>)
}
```

# part10 코루틴의 활용과 마무리

### 코루틴의 특징
- callback hell을 해결
- kotlin언어 키워드가 아닌 라이브러리

### 비동기 non-blocking 혹은 동시성이 필요한 곳에 사용

- client: Asynchronous UI (Android, iOS)
- Server : 여러 API를 동시에 호출
- webflux(비동기 non-blocking 프레임워크)
- 동시성 테스트
