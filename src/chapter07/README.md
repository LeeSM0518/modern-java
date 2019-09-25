# 7. 병렬 데이터 처리와 성능

자바 7은 쉽게 병렬화를 수행하면서 에러를 최소화할 수 있도록 **포크/조인 프레임워크(fork/join framework) 기능을 제공한다.**

스트림을 이용하면 순차 스트림을 **병렬 스트림으로 자연스럽게 바꿀 수 있다.**

<br>

# 7.1. 병렬 스트림

컬렉션에 **parallelStream을** 호출하면 **병렬 스트림(parallel stream)이** 생성된다. 

**병렬 스트림이란** 각각의 스레드에서 처리할 수 있도록 스트림 요소를 여러 청크로 분할한 스트림이다.

<br>

### *숫자 n을 인수로 받아서 1부터 n까지의 모든 숫자의 합계를 반환하는 예제*

- **스트림 사용 X**

  ```java
  public static long iterativeSum(long n) {
    long result = 0;
    for (long i = 1L; i <= n; i++) {
      result += i;
    }
    return result;
  }
  ```

  - n이 커진다면 결과 변수는 어떻게 동기화해야 할까?

<br>

- **스트림 사용 O**

  ```java
  public static long sequentialSum(long n) {
    return Stream.iterate(1L, i -> i + 1)     // 무한 자연수 스트림 생성
      .limit(n)                               // n개 이하로 제한
      .reduce(0L, Long::sum);                 // 모든 숫자를 더하는 스트림 리듀싱 연산
  }
  ```

<br>

## 7.1.1. 순차 스트림을 병렬 스트림으로 변환하기

순차 스트림에 **parallel 메소드를** 호출하면 기존의 함수형 리듀싱 연산이 병렬로 처리된다.

```java
public static long parallelSum(long n) {
  return Stream.iterate(1L, i -> i + 1)
    .limit(n)
    .parallel() // 스트림을 병렬 스트림으로 변환
    .reduce(0L, Long::sum);
}
```

- 리듀싱 연산을 여러 청크에 병렬로 수행한 뒤, 다시 리듀싱 연산으로 합쳐서 전체 스트림의 리듀싱 결과를 도출한다.

<br>

### *병렬 리듀싱 연산*

<img src="https://t1.daumcdn.net/cfile/tistory/2458BC37588D91401F">

<br>

### *병렬 스트림에서 사용하는 스레드 풀 설정*

병렬 스트림은 내부적으로 **ForkJoinPool을** 사용한다. 기본적으로 ForkJoinPool은 프로세서 수, 즉 **Runtime.getRuntime().availableProcessors()가 반환하는 값에 상응하는 스레드를 갖는다.** 

<br>

## 7.1.2. 스트림 성능 측정

**Java Microbenchmark Haness(JMH)** 를 이용하면 간단하고, 어노테이션 기반 방식을 지원하며, 안정적으로 자바 프로그램이나 자바 가상 머신(JVM) 을 대상으로 하는 **다른 언어용 벤치마크를 구현할 수 있다.**



<br>

## 7.1.4. 병렬 스트림 효과적으로 사용하기

- **확신이 서지 않으면 직접 측정하라.** 언제나 병렬 스트림이 순차 스트림보다 빠른 것은 아니기 때문에, 적절한 벤치마크로 직접 성늘을 측정해보자.
- **박싱을 주의하라.** 자동 방식과 언박싱은 성능을 크게 저하시킬 수 있는 요소다. 따라서 되도록이면 기본형 특화 스트림을 사용하는 것이 좋다.
- **순차 스트림보다 병렬 스트림에서 성능이 떨어지는 연산이 있다.** 
- **스트림에서 수행하는 전체 파이프라인 연산 비용을 고려하라.** 
- **소량의 데이터에서는 병렬 스트림이 도움 되지 않는다.**

- **스트림을 구성하는 자료구조가 적절한지 확인하라.** 
- **스트림의 특성과 파이프라인의 중간 연산이 스트림의 특성을 어떻게 바꾸는지에 따라 분해 과정의 성능이 달라질 수 있다.**
- **최종 연산의 병합 과정 비용을 살펴보라.**

<br>

### *스트림 소스와 분해성*

| 소스            | 분해성 |
| --------------- | ------ |
| ArrayList       | 훌륭함 |
| LinkedList      | 나쁨   |
| IntStream.range | 훌륭함 |
| Stream.iterate  | 나쁨   |
| HashSet         | 좋음   |
| TreeSet         | 좋음   |

<br>

# 7.2. 포크/조인 프레임워크

포크/조인 프레임워크는 병렬화할 수 있는 작업을 재귀적으로 **작은 작업으로 분할한 다음에 서브태스크 각각의 결과를 합쳐서 전체 결과를 만들도록** 설계되었다.

<br>

## 7.2.1. RecursiveTask 활용

스레드 풀을 이용하려면 **RecursiveTask\<R>**의 서브클래스를 만들어야 한다. RecursiveTask를 정의하려면 추상 메서드 **compute를 구현해야 한다.**

```java
protected abstract R compute();
```

* **R** : 병렬화된 태스크가 생성하는 결과 형식 또는 결과가 없을 때는 RecursiveAction 형식이다.

<br>

**compute 메서드는** 태스크를 서브태스크로 분할하는 로직과 더 이상 분할할 수 없을 때 개별 서브태스크의 결과를 생산할 알고리즘을 정의한다.

* **compute 메서드 구현 의사코드**

  ```java
  if (태스크가 충분히 적거나 더 이상 분할할 수 없으면) {
    순차적으로 태스크 계산
  } else {
    태스크를 두 서브태스크로 분할
    태스크가 다시 서브태스크로 분할되도록 이 메서드를 재귀적으로 호출함
    모든 서브태스크의 연산이 완료될 때까지 기다림
    각 서브태스크의 결과를 합침
  }
  ```

  * 이 알고리즘은 **분할 후 정복(divide-and-conquer)** 알고리즘의 병렬화 버전이다.

<br>

* **포크/조인 과정**

  <img src="https://t1.daumcdn.net/cfile/tistory/224ADD37588D914120">

<br>

### *포크/조인 프레임워크를 이용해서 병렬 합계 수행*

**ForkJoinSumCalculator.java**

```java
// RecursiveTask 를 상속받아 포크/조인 프레임워크에서 사용할
// 태스크를 생성한다.
public class ForkJoinSumCalculator extends java.util.concurrent.RecursiveTask<Long> {

  private final long[] numbers;  // 더할 숫자 배열
  private final int start;       // 이 서브태스크에서 처리할 배열의 초기 위치 
  private final int end;         // 최종 위치
  public static final long THRESHOLD = 10_000;  // 이 값 이하의 서브태스크는 더 이상 분할X

  // 메인 태스크를 생성할 때 사용할 공개 생성자
  public ForkJoinSumCalculator(long[] numbers) {
    this(numbers, 0, numbers.length);
  }

  // 메인 태스크의 서브태스크를 재귀적으로 만들 때 사용할 비공개 생성자
  private ForkJoinSumCalculator(long[] numbers, int start, int end) {
    this.numbers = numbers;
    this.start = start;
    this.end = end;
  }

  // RecursiveTask 의 추상 메서드 오버라이드
  @Override
  protected Long compute() {
    int length = end - start;       // 이 태스크에서 더할 배열의 길이

    if (length <= THRESHOLD) {
      // 기준값과 같거나 작으면 순차적으로 결과를 계산한다.
      return computeSequentially();
    }

    // 배열의 첫 번째 절반을 더하도록 서브태스크를 생성한다.  
    ForkJoinSumCalculator leftTask =
      new ForkJoinSumCalculator(numbers, start, start + length/2);
    // ForkJoinPool 의 다른 스레드로 새로 생성한 태스크를 비동기로 실행
    leftTask.fork();

    // 배열의 나머지 절반을 더하도록 서브태스크를 생성
    ForkJoinSumCalculator rightTask =
      new ForkJoinSumCalculator(numbers, start + length/2, end);
    // 두 번째 서브태스크를 동기 실행한다.
    // 이때 추가로 분할이 일어날 수 있다.
    Long rightResult = rightTask.compute();
    // 첫 번째 서브태스크의 결과를 읽거나
    // 아직 결과가 없으면 기다린다.
    Long leftResult = leftTask.join();

    // 두 서브태스크의 결과를 조합한 값이 태스크의 결과다
    return leftResult + rightResult;
  }

  // 더 분할할 수 없을 때 서브태스크의 결과를 계산
  private long computeSequentially() {
    long sum = 0;
    for (int i = start; i < end; i++) {
      sum += numbers[i];
    }
    return sum;
  }

}
```

* **n 까지의 배열을 생성해 계산하는 메소드**

  ```java
  public static long forkJoinSum(long n) {
    // n까지의 자연수를 포함하는 배열 생성
    long[] numbers = LongStream.rangeClosed(1, n).toArray();
    // 배열을 생성자로 전달해 태스크 생성
    ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
    // invoke 메서드를 통해 태스크의 결과를 반환
    return new ForkJoinPool().invoke(task);
  }
  ```

<br>

### ForkJoinSumCalculator 실행

1. ForkJoinSumCalculator를 ForkJoinPool로 전달하면 풀의 스레드가 ForkJoinSumCalculator의 compute 메서드를 실행하면서 작업 수행
2. compute 메서드는 태스크를 나눌 수 없을 때까지 반으로 나누면서 두 개의 새로운 ForkJoinSumCalculator로 할당하는 것을 재귀적으로 실행한다.
3. 매우 작아진 각 서브태스크는 순차적으로 처리되며 포킹 프로세스로 만들어진 이진트리의 태스크를 루트에서 역순으로 방문한다(계산한다).
4. 서브태스크의 부분 결과를 합쳐서 태스크의 최종 결과를 계산한다.

<br>

* **포크/조인 알고리즘**

<img src="../capture/스크린샷 2019-09-24 오후 8.45.23.png">

<br>

## 7.2.2. 포크/조인 프레임워크를 제대로 사용하는 방법

* join 메서드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때까지 호출자를 블록시킨다. 따라서 두 서브태스크가 모두 시작된 다음에 join을 호출해야 한다.
* RecursiveTask 내에서는 ForkJoinPool의 invoke 메서드를 사용하지 말아야 한다. 대신 compute나 fork 메서드를 직접 호출할 수 있다.
* 서브태스크에 fork 메서드를 호출해서 ForkJoinPool의 일정을 조절할 수 있다. 양쪽 작업 모두에서 fork를 호출하는 것보다는 한쪽에서는 fork를, 다른 한쪽에서는 compute를 호출하는 것이 효율적이다.
* 포크/조인 프레임워크를 이용하는 병렬 계산은 디버깅하기 어렵다.
* 포크/조인 프레임워크를 사용하는 것이 순차 처리보다 무조건 빠르지는 않다.

<br>

## 7.2.3. 작업 훔치기

각각의 서브태스크의 작업완료 시간이 크게 달라질 수 있다. 분할 기법이 효율적이지 않았기 때문일 수도 있고 아니면 디스크 접근 속도가 저하되었거나 외부 서비스와 협력하는 과정에서 지연이 생겼을 수도 있다.

포크/조인 프레임워크에서는 **작업 훔치기(work stealing)라는** 기법으로 이 문제를 해결한다. 

작업 훔치기 기법에서는 ForkJoinPool의 모든 스레드를 거의 공정하게 분할한다. 그리고 작업이 끝난 스레드는 큐의 헤드에서 다른 태스크를 가져와서 작업을 처리한다. 즉, 모든 큐가 빌 때까지 스레드들이 할 일이 끝나면 작업을 훔쳐온다.

풀에 있는 작업자 스레드의 태스크를 재분배하고 균형을 맞출 때 작업 훔치기 알고리즘을 사용한다.

<br>

* **포크/조인 프레임워크에서 사용하는 작업 훔치기 알고리즘**

  <img src="../capture/스크린샷 2019-09-24 오후 9.26.20.png">

<br>

# 7.3. Spliterator 인터페이스

**Spliterator는 '분할할 수 있는 반복자다(splitable iterator)' 라는 의미다.** 

Spliterator는 병렬 작업에 특화되어 있다. 

<br>

* **Spliterator 인터페이스**

  ```java
  public interface Spliterator<T> {
    boolean tryAdvance(Consumer<? super T> action);
    Spliterator<T> trySplit();
    long estimateSize();
    int characteristics();
  }
  ```

  * **T** : Spliterator에서 탐색하는 요소의 형식
  * **tryAdvance()** : Spliterator의 요소를 하나씩 순차적으로 소비하면서 탐색해야 할 요소가 남아있으면 참을 반환한다(Iterator와 비슷).
  * **trySplit()** : Spliterator의 일부 요소(자신이 반환한 요소)를 분할해서 두 번째 Spliterator를 생성하는 메서드.
  * **estimateSize()** : Spliterator에서 탐색해야 할 요소 수 정보

<br>

## 7.3.1. 분할 과정

### *재귀 분할 과정*

<img src="../capture/스크린샷 2019-09-24 오후 9.39.18.png">

1. 첫 번째 Spliterator에 trySplit을 호출하면 두 번째 Spliterator가 생성된다.
2. 두 개의 Spliterator에 trySplit를 다시 호출하면 두 배인 네 개의 Spliterator가 생성된다.

> 이처럼 trySplit의 결과가 null이 될 때까지, 즉 더 이상 분할할 수 없을 때까지 실행된다.

<br>

### Spliterator 특성

Spliterator는 characteristics라는 추상 메서드도 정의한다. **Characteristics 메서드 Spliterator 자체의 특성 집합을 포함하는 int를 반환한다.**

<br>

* **Spliterator 특성**

| 특성       | 의미                                                         |
| ---------- | ------------------------------------------------------------ |
| ORDERED    | 리스트처럼 요소에 정해진 순서가 있으므로 Spliterator는 요소를 탐색하고 분할할 때 이 순서에 유의해야 한다. |
| DISTINCT   | x, y 두 요소를 방문했을 때, x.equals(y)는 항상 false를 반환한다. |
| SORTED     | 탐색된 요소는 미리 정의된 정렬 순서를 따른다.                |
| SIZED      | 크기가 알려진 소스로 Spliterator를 생성했으므로 estimatedSize()는 정확한 값을 반환한다. |
| NON-NULL   | 탐색하는 모든 요소는 null이 아니다.                          |
| IMMUTABLE  | 이 Spliterator의 소스는 불변이다. 즉, 요소를 탐색하는 동안 요소를 추가하거나, 삭제하거나, 고칠 수 없다. |
| CONCURRENT | 동기화 없이 Spliterator의 소스를 여러 스레드에서 동시에 고칠 수 있다. |
| SUBSIZED   | 이 Spliterator 그리고 분할되는 모든 Spliterator는 SIZED 특성을 갖는다. |

<br>

## 7.3.2. 커스텀 Spliterator 구현하기

* **반복형으로 단어 수를 세는 메서드**

  ```java
  public int countWordsIteratively(String s) {
    int counter = 0;
    boolean lastSpace = true;
    for (char c : s.toCharArray()) {
      if (Character.isWhitespace(c)) {
        lastSpace = true;
      } else {
        if (lastSpace) counter++;
        lastSpace = false;
      }
    }
    return counter;
  }
  ```

* **Main**

  ```java
  public static void main(String[] args) {
    final String SENTENCE = "There is no requirement that a new or distinct result be returned each " +
      "time the supplier is invoked.";
    System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");
  }
  ```

* **실행 결과**

  ```
  Found 18 words
  ```

반복형 대신 함수형을 이용하면 직접 스레드를 동기화하지 않고도 **병렬 스트림으로 작업을 병렬화할 수 있다.**

<br>

### 함수형으로 단어 수를 세는 메서드 재구현하기

우선 String을 스트림으로 변환 한다. 스트림은 기본형만 제공하므로 Stream\<Character>를 사용해야 한다.

```java
Stream<Character> stream = IntStream.range(0, SENTENCE.length())
  .mapToObj(SENTENCE::charAt);
```

<br>

스트림에 리듀싱 연산을 실행하면서 단어 수를 계산할 수 있다. 이들 변수 상태를 캡슐화하는 새로운 클래스 WordCounter를 만들어야 한다.

* **WordCounter.java**

  ```java
  public class WordCounter {
  
    private final int counter;
    private final boolean lastSpace;
  
    public WordCounter(int counter, boolean lastSpace) {
      this.counter = counter;
      this.lastSpace = lastSpace;
    }
  
    // 반복 알고리즘처럼 accumulate 메서드는
    // 문자열의 문자를 하나씩 탐색한다.
    public WordCounter accumulate(Character c) {
      if (Character.isWhitespace(c)) {
        return lastSpace ?
            this : new WordCounter(counter, true);
      } else {
        return lastSpace ?
            // 문자를 하나씩 탐색하다 공백 문자를 만나면
            // 지금까지 탐색한 문자를 단어로 간주하여
            // 단어 수를 증가시킨다.
            new WordCounter(counter + 1, false) :
            this;
      }
    }
  
    public WordCounter combine(WordCounter wordCounter) {
      // 두 WordCounter 의 counter 값을 더한다.
      return new WordCounter(counter + wordCounter.counter,
          // counter 값만 더할 것이므로 마지막 공백은 신경 쓰지 않는다.
          wordCounter.lastSpace);
    }
  
    public int getCounter() {
      return counter;
    }
  
  }
  ```

  * accumulate 메서드는 WordCounter의 상태를 어떻게 바꿀 것인지, 또는 엄밀히 WordCounter는 불편 클래스이므로 새로운 WordCounter 클래스를 어떤 상태로 생성할 것인지 정의한다.
  * 스트림을 탐색하면서 새로운 문자를 찾을 때마다 accumulate 메서드를 호출한다.

<br>

* **새로운 문자 c를 탐색했을 때 WordCounter의 상태 변화**

  <img src="../capture/스크린샷 2019-09-25 오후 2.12.09.png">

<br>

* **리듀싱 연산을 직관적으로 구현할 수 있다.**

  ```java
  private static int countWords(Stream<Character> stream) {
    WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
                                            WordCounter::accumulate, WordCounter::combine);
    return wordCounter.getCounter();
  }
  ```

<br>

* **Main**

  ```java
  public static void main(String[] args) {
    final String SENTENCE = "There is no requirement that a new or distinct result be returned each " +
      "time the supplier is invoked.";
    Stream<Character> stream = IntStream.range(0, SENTENCE.length())
      .mapToObj(SENTENCE::charAt);
    System.out.println("Found " + countWords(stream) + " words");
  }
  ```

<br>

* **실행 결과**

  ```
  Found 18 words
  ```

<br>

### WordCounter 병렬로 수행하기

단어 수를 계산하는 연산을 병렬 스트림으로 처리하자.

* **Main**

  ```java
  System.out.println("Found " + countWords(stream.parallel()) + " words");
  ```

* **실행 결과**

  ```
  Found 57 words
  ```

  > 18이 아닌 57이 나왔음을 알 수 있다. 즉, 잘못된 값이 나옴을 알 수 있다.

<br>

원래 문자열을 임의의 위치에서 둘로 나누다보니 예상치 못하게 하나의 단어를 둘로 계산하는 상황이 발생할 수 있다.

즉, **순차 스트림을 병렬 스트림으로 바꿀 때 스트림 분할 위치에 따라 잘못된 결과가 나올 수 있다.**

문자열을 임의의 위치에서 분할하지 말고 단어가 끝나는 위치에서만 분할하는 방법으로 이 문제를 해결할 수 있다. 그러면 **단어 끝에서 문자열을 분할하는 문자 Spliterator가 필요하다.**

<br>

* **문자 Spliterator를 구현한 다음에 병렬 스트림으로 전달하는 코드**

  ```java
  public class WordCounterSpliterator implements Spliterator<Character> {
  
    private final String string;
    private int currentChar = 0;
  
    public WordCounterSpliterator(String string) {
      this.string = string;
    }
  
    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {
      action.accept(string.charAt(currentChar++));   // 현재 문자를 소비한다.
      return currentChar < string.length();          // 소비할 문자가 남아있으면 true 를 반환
    }
  
    @Override
    public Spliterator<Character> trySplit() {
      int currentSize = string.length() - currentChar;
      if (currentSize < 10) {
        // 피싱할 문자열을 순차 처리할 수 있을 만큼
        // 충분히 작아졌음을 알리는 null 을 반환한다.
        return null;
      }
      // 피싱할 문자열의 중간을 분할 위치로 설정한다.
      for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
        // 다음 공백이 나올 때까지 분할 위치를 뒤로 이동 시킨다.
        if (Character.isWhitespace(string.charAt(splitPos))) {
          // 처음부터 분할 위치까지 문자열을 파싱할
          // 새로운 WordCounterSpliterator 를 생성한다.
          Spliterator<Character> spliterator =
              new WordCounterSpliterator(string.substring(currentChar,
                  splitPos));
          // 이 WordCounterSpliterator 의 시작 위치를 분할 위치로 설정한다.
          currentChar = splitPos;
          // 공백을 찾았고 문자열을 분리했으므로 루프를 종료한다.
          return spliterator;
        }
      }
      return null;
    }
  
    @Override
    public long estimateSize() {
      return string.length() - currentChar;
    }
  
    @Override
    public int characteristics() {
      return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
    }
    
  }
  ```

  * 분석 대상 문자열로 Spliterator를 생성한 다음에 현재 탐색 중인 문자를 가리키는 인덱스를 이용해서 모든 문자를 반복 탐색한다.
  * tryAdvance 메서드는 문자열에서 현재 인덱스에 해당하는 문자를 Consumer에 제공한 다음에 인덱스를 증가시킨다.  인수로 전달된 Consumer는 스트림을 탐색하면서 적용해야 하는 함수 집합이 작업을 처리할 수 있도록 소비한 문자를 전달하는 자바 내부 클래스다.
  * trySplit은 반복될 자료구조를 분할하는 로직을 포함하므로 Spliterator에서 가장 중요한 메서드다. 우선 분할 동작을 중단할 한계를 설정해야 한다.
  * 탐색해야 할 요소의 개수(estimatedSize)는 Spliterator가 파싱할 문자열 전체 길이(string.length())와 현재 반복 중인 위치(currentChar)의 차다.
  * characteristics 메서드는 프레임워크에 Spliterator가 ORDERED (문자열의 문자 등장 순서), SIZED (estimatedSize 메서드의 반환값이 정확), SUBSIZED (trySplit으로 생성된 Spliterator도 정확한 크기를 가짐) , NONNULL (문자열에는 null 문자가 존재하지 않음), IMMUTABLE (문자열 자체가 불편 클래스이므로 문자열을 파싱하면서 속성이 추가되지 않음) 등의 특성임을 알려준다.

<br>

### WordCounterSpliterator 활용

- **WordCounterSpliterator를 병렬 스트림에 사용**

  ```java
  Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);
  Stream<Character> stream = StreamSupport.stream(spliterator, true);
  ```

  - StreamSupport.stream 팩토리 메서드로 전달한 두 번째 불리언 인수는 병렬 스트림 생성 여부를 지시한다.

- **실행**

  ```java
  System.out.println("Found " + countWords(stream) + " words");
  ```

- **결과**

  ```
  Found 18 words
  ```

<br>

Spliterator는 첫 번째 탐색 시점, 첫 번째 분할 시점, 또는 첫 번째 예상 크기(estimatedSize) 요청 시점에 요소의 소스를 바인딩할 수 있다. 이와 같은 동작을 늦은 바인딩 Spliterator라고 부른다.

<br>

# 7.4. 마치며

- 내부 반복을 이용하면 명시적으로 다른 스레드를 사용하지 않고도 스트림을 병렬로 처리할 수 있다.
- 스트림을 병렬로 처리하는 것이 항상 빠른 것은 아니다.
- 병렬 스트림으로 데이터 집합을 병렬 실행할 때 특히 처리해야 할 데이터가 아주 많거나 각 요소를 처리하는 데 오랜 시간이 걸릴 때 성능을 높일 수 있다.
- 가능하면 기본형 특화 스트림을 사용하여 병렬 처리하는 것이 좋다.
- 포크/조인 프레임워크에서는 병렬화할 수 있는 태스크를 작은 태스크로 분할한 다음에 분할된 태스크를 각각의 스레드로 실행하며 서브태스크 각각의 결과를 합쳐서 최종 결과를 생산한다.
- Spliterator는 탐색하려는 데이터를 포함하는 스트림을 어떻게 병렬화할 것인지 정의한다.