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



