# 3. 람다 표현식

깔끔한 코드로 동작을 구현하고 전달하는 자바 8의 새로운 기능인 람다 표현식을 설명한다. 

람다 표현식은 익명 클래스처럼 이름이 없는 함수면서 메서드를 인수로 전달할 수 있다.

<br/>

# 3.1. 람다란 무엇인가?

**람다 표현식은** 메서드로 전달할 수 있는 익명 함수를 단순화한 것이라고 할 수 있다.

* **람다의 특징**
  * **익명** : 보통의 메서드와 달리 이름이 없으므로 익명으로 표현한다.
  * **함수** : 람다는 메서드처럼 특정 클래스에 종속되지 않으므로 함수라고 부른다.
  * **전달** : 람다 표현식을 메서드 인수로 전달하거나 변수로 저장할 수 있다.
  * **간결성** : 익명 클래스처럼 많은 자질구레한 코드를 구현할 필요가 없다.

<br/>

* **자바 8 이전의 코드**

  ```java
  Comparator<Apple> byWeight = new Comparator<Apple>() {
    public int compare(Apple a1, Apple a2) {
      return a1.getWeight().compareTo(a2.getWeight());
    }
  };
  ```

* **람다를 이용한 코드**

  ```java
  Comparator<Apple> byWeight = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight);
  ```

  > 코드가 훨씬 간단해졌다.

<br/>

* **람다 표현식은 파라미터, 화살표, 바디로 이루어진다.**

  ```java
  (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
  ```

  * **파라미터 리스트**

    <u>(Apple a1, Apple a2)</u> : Comparator의 compare 메서드 파라미터

  * **화살표**

    <u>-></u> : 화살표는 람다의 파라미터 리스트와 바디를 구분한다.

  * **람다 바디**

    <u>a1.getWeight().compareTo(a2.getWeight())</u> : 람다의 반환값에 해당하는 표현식이다.

<br/>

**람다 예제**

* **불리언 표현식**

  ```java
  (List<String> list) -> list.isEmpty()
  ```

* **객체 생성**

  ```java
  () -> new Apple(10)
  ```

* **객체에서 소비**

  ```java
  (Apple a) -> {
    System.out.println(a.getWeight());
  }
  ```

* **객체에서 선택/추출**

  ```java
  (String s) -> s.length()
  ```

* **두 값을 조합**

  ```java
  (int a, int b) -> a * b
  ```

* **두 객체 비교**

  ```java
  (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())
  ```

<br/>

# 3.2. 어디에, 어떻게 람다를 사용할까?

2장에서 구현했던 필터 메서드에도 람다를 활용할 수 있었다.

```java
public class Main {

  public static void main(String[] args) {
    List<Apple> inventory = List.of(new Apple(GREEN, 100), new Apple(RED, 150));
    List<Apple> greenApples = filter(inventory, (Apple a) -> GREEN.equals(a.getColor()));
    System.out.println(greenApples);
  }

  public static <T> List<T> filter(List<T> list, Predicate<T> p) {
    List<T> result = new ArrayList<>();
    for(T e : list) {
      if (p.test(e)) {
        result.add(e);
      }
    }
    return result;
  }

}
```

>  **함수형 인터페이스** 라는 문맥에서 람다 표현식을 사용할 수 있다. 위 예제에서는 **함수형 인터페이스 Predicate\<T>를** 기대하는 filter 메서드의 두 번째 인수로 람다 표현식을 전달했다.

<br/>

## 3.2.1. 함수형 인터페이스

* **Predicate\<T>**

  ```java
  public interface Predicate<T> {
    boolean test (T t);
  }
  ```

  >  위와 같이 **오직 하나의 추상 메서드만 지정하는 것을 함수형 인터페이스라 한다.**

* **자바 API의 함수형 인터페이스들**

  ```java
  public interface Comparator<T> {
    int compare(T o1, T o2);
  }
  
  public interface Runnable {
    void run();
  }
  
  public interface ActionListner extends EventListener {
    void actionPerformed(ActionEvent e);
  }
  
  public interface Callable<V> {
    V call() throws Exception;
  }
  
  public interface PrivilegedAction<T> {
    T run();
  }
  ```

<br/>

> 인터페이스에 많은 디폴트 메서드가 있더라도 **추상 메서드가 오직 하나면** 함수형 인터페이스다.

<br/>

**함수형 인터페이스로 뭘 할 수 있을까?**

람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있으므로 <u>전체 표현식을 함수형 인터페이스의 인스턴스로 취급</u> 할 수 있다.

* **예제) Runnable 함수형 인터페이스 사용**

  ```java
  // 람다 사용
  Runnable r1 = () -> System.out.println("Hello World 1");
  
  // 익명 클래스 사용
  Runnable r2 = new Runnable() {
    public void run() {
      System.out.println("Hello World 2");
    }
  };
  
  public static void process(Runnable r) {
    r.run();
  }
  
  process(r1);
  process(r2);
  process(() -> System.out.println("Hello World 3"));
  ```

* **실행 결과**

  ```
  Hello World 1
  Hello World 2
  Hello World 3
  ```

<br/>

## 3.2.2. 함수 디스크립터

함수형 인터페이스의 추상 메서드 **시그니처(선언만 되어있고 구현은 안되어있는 상태)** 는 람다 표현식의 시그니처를 가리킨다.

람다 표현식의 시그니처를 서술하는 메서드를 **함수 디스크립터(function descriptor)** 라고 부른다.

* **예시) Runnable 을 통한 문자열 출력**

  ```java
  public void process(Runnable r) {
    r.run();
  }
  process(() -> System.out.println("This is awesome!!"));
  ```

  > 위의 예시는 인수가 없으며 void를 반환하는 람다 표현식이다. 이는 Runnable 인터페이스의 run 메서드 시그니처와 같다.

<br/>

* **@FunctionalInterface는 무엇인가?**

  이 어노테이션은 함수형 인터페이스임을 가리키는 어노테이션이다. 이것을 써서 인터페이스를 선언했지만 실제로 함수형 인터페이스가 아니면 컴파일러가 에러를 발생시킨다.

<br/>

# 3.3. 람다 활용 : 실행 어라운드 패턴

자원 처리(ex. 데이터베이스의 파일 처리)에 사용하는 **순환 패턴은** 자원을 열고, 처리한 다음에, 자원을 닫는 순서로 이루어진다. 설정과 정리 과정은 대부분 비슷하다.

즉, 실제 자원을 처리하는 코드를 설정과 정리 두 과정이 둘러싸는 형태를 **실행 어라운드 패턴(execute around pattern)이라고** 부른다.

* **실행 어라운드 패턴 (중복되는 준비 코드와 정리 코드가 작업 A와 작업 B를 감싸고 있다.)**

  <img src="../capture/스크린샷 2019-08-20 오후 7.27.04.png" width=500>

* **예제) 파일에서 한 행을 읽는 코드**

  ```java
  public String processFile() throws IOException {
    // try-with-resources 구문을 통해 자원을 명시적으로 닫을 필요가 없다.
    try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
      return br.readLine();	// 실제 필요한 작업을 하는 행
    }
  }
  ```

  

<br/>

## 3.3.1. 1단계 : 동작 파라미터화를 기억하라

위의 예제 코드의 기존의 설정, 정리 과정은 재사용하고 processFile 메서드만 다른 동작을 수행하도록 할려면 **processFile의 동작을 파라미터화 해야 한다.**

즉, processFile 메서드가 BufferedReader를 이용해서 다른 동작을 수행할 수 있도록 processFile 메서드로 동작을 전달해야 한다.

* **두 행을 출력하는 코드**

  ```java
  String result = processFile(
    (BufferedReader br) -> br.readLine() + br.readLine());
  ```

<br/>

## 3.3.2. 2단계 : 함수형 인터페이스를 이용해서 동작 전달

위와 같이 람다를 사용하기 위해서는 BufferedReader -> String 과 IOException을 던질(throw) 수 있는 시그니처와 일치하는 함수형 인터페이스를 만들어야 한다.

* **함수형 인터페이스 구현**

  ```java
  @FunctionalInterface
  public interface BufferedReaderProcessor {
    String process(BufferedReader b) throws IOException;
  }
  
  // 위에서 정의한 인터페이스를 processFile 메서드의 인수로 전달할 수 있다.
  public String processFile(BufferedReaderProcessor p) throws IOException {
    ...
  }
  ```

<br/>

## 3.3.3. 3단계 : 동작 실행

람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있으며 **전달된 코드는 함수형 인터페이스의 인스턴스로 전달된 코드와 같은 방식으로 처리한다.** 따라서 processFile 바디 내에서 BufferedReaderProcessor 객체의 process를 호출할 수 있다.

* **processFile 코드 수정**

  ```java
  public String processFile(BufferedReaderProcessor p) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
      return p.process(br);		// BufferedReader 객체 처리
    }
  }
  ```

<br/>

## 3.3.4. 4단계 : 람다 전달

이제 람다를 이용해서 다양한 동작을 processFile 메서드로 전달할 수 있다.

* **한 행을 처리하는 코드**

  ```java
  String oneLine = processFile((BufferedReader br) -> br.readLine());
  ```

* **두 행을 처리하는 코드**

  ```java
  String twoLines = processFile(
    (BufferedReader br) -> br.readLine() + br.readLine());
  ```

<br/>

# 3.4. 함수형 인터페이스 사용

함수형 인터페이스의 추상 메서드는 람다 표현식의 시그니처를 묘사한다. 함수형 인터페이스의 추상 메서드 시그니처를 **함수 디스크립터(function descriptor)라고** 한다.

자바 8 라이브러리 설계자들은 java.util.function 패키지로 여러 가지 새로운 함수형 인터페이스를 제공한다.

* **ex)** Predicate, Consumer, Function

<br/>

## 3.4.1. Predicate

**java.util.function.Predicate\<T> 인터페이스는** test라는 추상 메서드를 정의하며 test는 제네릭 형식 T의 객체를 인수로 받아 불리언을 반환한다.

* **Predicate 예제**

  ```java
  @FunctionalInterface
  public interface Predicate<T> {
    boolean test(T t);
  }
  
  public <T> List<T> filter(List<T> list, Predicate<T> p) {
    List<T> results = new ArrayList<>();
    for (T t : list) {
      if (p.test(t)) {
        results.add(t);
      }
    }
    return results;
  }
  
  Predicate<String> nonEmptyStringPredicate = (Stirng s) -> !s.isEmpty();
  List<String> nonEmpty = filter(listOfStrings, nonEmptyStringPredicate);
  ```

<br/>

## 3.4.2. Consumer

**java.util.function.Consumer\<T> 인터페이스는** 제네릭 형식 T 객체를 받아서 void를 반환하는 accept라는 추상 메서드를 정의한다.

* **Consumer 예제**

  ```java
  @FunctionalInterface
  public interface Consumer<T> {
    void accept(T t);
  }
  
  public <T> void forEach(List<T> list, Consumer<T> c) {
    for (T t : list) {
      c.accept(t);
    }
  }
  
  forEach(
    Arrays.asList(1, 2, 3, 4, 5),
    (Integer i) -> System.out.println(i)	// Consumer의 accept 메소드를 구현하는 람다
  );
  ```

<br/>

## 3.4.3. Function

**java.util.function.Function\<T, R> 인터페이스는** 제네릭 형식 T를 인수로 받아서 제네릭 형식 R 객체를 반환하는 추상 메서드 apply를 정의한다.

* **Function 예제**

  ```java
  @FunctionalInterface
  public interface Function<T, R> {
    R apply(T t);
  }
  
  public <T, R> List<R> map(List<T> list, Function<T, R> f) {
    List<R> result = new ArrayList<>();
    for(T t : list) {
      result.add(f.apply(t));
    }
    return result;
  }
  
  List<Integer> l = map(
    Arrays.asList("lambdas", "in", "action"),
    (String s) -> s.length()	// Function의 apply 메서드를 구현하는 람다
  );
  ```

  <br/>

### 기본형 특화

자바에서는 기본형을 참조형으로 변환하는 기능을 제공한다. 이 기능을 **박싱(boxing)** 이라고 한다. 

참조형을 기본형으로 변환하는 반대 동작을 **언박싱(unboxing)** 이라고 한다.

또한 프로그래머가 편리하게 코드를 구현할 수 있도록 박싱과 언박싱이 자동으로 이루어지는 **오토박싱(autoboxing)** 이라는 기등도 제공한다.

* **예시) 오토박싱**

  ```java
  List<Integer> list = new ArrayList<>();
  for (int i = 300; i < 400; i++) {
    list.add(i);
  }
  ```

  > 하지만 이런 변환 과정은 박싱한 값이 메모리를 더 소비하며 기본형을 가져올 때도 메모리를 탐색하는 과정이 필요하다.

<br/>

자바 8 에서는 기본형을 입출력으로 사용하는 상황에서 오토박싱 동작을 피할 수 있도록 특별한 버전의 함수형 인터페이스를 제공한다.

* **예시) IntPredicate**

  ```java
  public interface IntPredicate {
    boolean test(int t);
  }
  
  IntPredicate evenNumber = (int i) -> i % 2 == 0;
  evenNumbers.test(1000);		// 박싱 없음
  
  predicate<Integer> oddNumbers = (Integer i) -> i % 2 != 0;
  oddNumbers.test(1000);		// 박싱
  ```

<br/>

아래의 표는 자바에서 제공하는 함수형 인터페이스 중 일부에 불과하다는 사실을 기억하자. 필요하다면 우리가 직접 함수형 인터페이스를 만들 수 있다. **(T, U) -> R 같은 표기법으로 함수 디스크립터를 설명할 수 있음을 기억하자.**

| 함수형 인터페이스    | 함수 디스크립터   | 기본형 특화                                                  |
| -------------------- | ----------------- | ------------------------------------------------------------ |
| Predicate\<T>        | T -> boolean      | IntPredicate, LongPredicate, DoublePredicate                 |
| Consumer\<T>         | T -> void         | IntConsumer, LongConsumer, DoubleConsumer                    |
| Function\<T, R>      | T -> R            | IntFunction\<R>, IntToDoubleFunction, IntToLongFunction, LongFunction\<R>, LongToDoubleFunction, LongToIntFunction, DoubleFunction\<R>, DoubleToIntFunction, DoubleToLongFunction, ToIntFunction\<T>, ToDoubleFunction\<T>, ToLongFunction\<T> |
| Supplier\<T>         | () -> T           | BooleanSupplier, IntSupplier, LongSupplier, DoubleSupplier   |
| UnaryOperator\<T>    | T -> T            | IntUnaryOperator, LongUnaryOperator, DoubleUnaryOperator     |
| BinaryOperator\<T>   | (T, T) -> T       | IntBinaryOperator, LongBinaryOperator, DoubleBinaryOperator  |
| BiPredicate\<L, R>   | (T, U) -> boolean |                                                              |
| BiConsumer\<T, U>    | (T, U) -> void    | ObjIntConsumer\<T>, ObjLongConsumer\<T>, ObjDoubleConsumer\<T> |
| BiFunction\<T, U, R> | (T, U) -> R       | ToIntBiFunction\<T, U>, ToLongBiFunction\<T, U>, ToDoubleBiFunction\<T, U> |

<br/>

* **람다와 함수형 인터페이스 예제**

| 사용 사례          | 람다 예제                                                    | 대응하는 함수형 인터페이스                                   |
| ------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 불리언 표현        | (List\<String> list) -> list.isEmpty()                       | Predicate\<List\<String>>                                    |
| 객체 생성          | () -> new Apple(10)                                          | Supplier\<Apple>                                             |
| 객체에서 소비      | (Apple a) -> System.out.println(a.getWeight())               | Consumer\<Apple>                                             |
| 객체에서 선택/추출 | (String s) -> s.length()                                     | Function\<String, Integer> 또는 ToIntFunction\<String>       |
| 두 값 조합         | (int a, int b) -> a * b                                      | IntBinaryOperator                                            |
| 두 객체 비교       | (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()) | Comparator\<Apple> 또는 BiFunction\<Apple, Apple, Integer> 또는 ToIntBiFunction\<Apple, Apple> |

<br/>

### 예외, 람다, 함수형 인터페이스의 관계

예외를 던지는 람다 표현식을 만들려면 확인된 예외를 선언하는 함수형 인터페이스를 직접 정의하거나 람다를 try/catch 블록으로 감싸야 한다.

- **예시) IOException throw**

  ```java
  @FunctionalInterface
  public interface BufferedReaderProcessor {
    String process(BufferedReader b) throws IOException;
  }
  BufferedReaderProcessor p = (BufferedReader br) br.readLine();
  ```

  > 그러나 우리는 Function\<T, R> 형식의 함수형 인터페이스를 기대하는 API 사용하고 있으며 직접 함수형 인터페이스를 만들기 어려운 상황이라고 가정하면, 아래와 같이 예외를 처리할 수 있다.

  ```java
  Function<BufferedReader, String> f = (BufferedReader b) -> {
    try {
      return b.readLine();
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
  ```

<br/>

# 3.5. 형식 검사, 형식 추론, 제약

## 3.5.1. 형식 검사

람다가 사용되는 **콘텍스트(context, 람다가 전달될 메서드 파라미터나 람다가 할당되는 변수 등)를** 이용해서 람다의 형식(type)을 추론할 수 있다.

어떤 콘텍스트에서 기대되는 람다 표현식의 형식을 **대상 형식(target type)** 이라고 부른다.

- **예제) 람다 표현시**

  ```java
  List<Apple> heavierThan150g =
    filter(inventory, (Apple apple) -> apple.getWeight() > 150);
  ```

  1. filter 메서드의 선언을 확인한다.
  2. filter 메서드는 두 번째 파라미터로 Predicate\<Apple> 형식(대상 형식)을 기대한다.
  3. Predicate\<Apple>은 test라는 한 개의 추상 메서드를 정의하는 함수형 인터페이스다.
  4. test 메서드는 Apple을 받아 boolean을 반환하는 함수 디스크립터를 묘사한다.
  5. filter 메서드로 전달된 인수는 이와 같은 요구사항을 만족해야 한다.

<br/>

## 3.5.2. 같은 람다, 다른 함수형 인터페이스

대상 형식(target typing)이라는 특징 때문에 같은 람다 표현식이더라도 호환되는 추상 메서드를 가진 다른 함수형 인터페이스로 사용될 수 있다.

- **예시) Callable 과 PrivilegedAction 인터페이스**

  ```java
  Callable<Integer> c = () -> 42;
  PrivilegedAction<Integer> p = () -> 42;
  ```

<br/>

### 다이아몬드 연산자

다이아몬드 연산자(\<>)로 콘텍스트에 따른 제네릭 형식을 추론할 수 있다. 주어진 클래스 인스턴스 표현식을 두 개 이상의 다양한 콘텍스트에 사용할 수 있다. 이때 인스턴스 표현식의 형식 인수는 콘텍스트에 의해 추론된다.

```java
List<String> listOfStrings = new ArrayList<>();
List<Integer> listOfIntegers = new ArrayList<>();
```

<br/>

### 특별한 void 호환 규칙

람다의 바디에 일반 표현식이 있으면 void를 반환하는 함수 디스크립터와 호환된다.

```java
// Predicate는 불리언 반환값을 갖는다.
Predicate<String> p = s -> list.add(s);
// Consumer는 void 반환값을 갖는다.
Consumer<String> b = s -> list.add(s);
```

<br/>

## 3.5.3. 형식 추론

컴파일러는 람다 표현식의 파라미터 형식에 접근할 수 있으므로 람다 문법에서 이를 생략할 수 있다.

즉, **자바 컴파일러는 다음처럼 람다 파라미터 형식을 추론할 수 있다.**

```java
// 파라미터 apple에는 형식을 명시적으로 지정하지 않았다.
List<Apple> greenApples =
  filter(inventory, apple -> GREEN.equals(apple.getColor()));
```

<br/>

여러 파라미터를 포함하는 람다 표현식에서는 코드 가독성 향상이 더 두드러진다.

```java
// 형식을 추론하지 않음
Comparator<Apple> c =
  (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
// 형식을 추론
Comparator<Apple> c =
  (a1, a2) -> a1.getWeight().compareTo(a2.getWeight());
```

상황에 따라 명시적으로 형식을 포함하는 것이 좋을 때도 있고 형식을 배제하는 것이 가독성을 향상시킬 때도 있다.

<br/>

## 3.5.4. 지역 변수 사용

람다 표현식에서는 익명 함수가 하는 것처럼 **자유 변수(free variable, 파라미터로 넘겨진 변수가 아닌 외부에서 정의된 변수)** 를 활용할 수 있다. 이와 같은 동작을 **람다 캡처링(capturing lambda)** 이라고 부른다.

- **예제) portNumber 변수를 캡처하는 람다**

  ```java
  int portNumber = 1337;
  Runnable r = () -> System.out.println(portNumber);
  ```

<br/>

하지만 람다가 지역 변수를 사용하려면 **final로 선언된 변수와 똑같이 사용되어야 한다.** 즉, 람다 표현식은 한 번만 할당할 수 있는 지역 변수를 캡처할 수 있다.

- **컴파일할 수 없는 코드**

  ```java
  int portNumber = 1337;
  Runnable r = () -> System.out.println(portNumber);
  portNumber = 31337;
  ```

  > 람다에서 참고하는 지역 변수는 final로 선언되거나 실질적으로 final 처럼 취급되어야 한다.

<br/>

### 지역 변수의 제약

자유 지역 변수의 복사본의 값이 바뀌지 않아야 하므로 지역 변수에는 **한 번만 값을 할당해야 한다는 제약이 있다.** 또한 지역 변수의 제약 때문에 외부 변수를 변화시키는 일반적인 명령형 프로그래밍 패턴(병렬화를 방해하는 요소)에 제동을 걸 수 있다.

<br/>

### 클로저

**클로저(Closure)란** 함수의 비지역 변수를 자유롭게 참조할 수 있는 함수의 인스턴스를 가리킨다.

<br/>

# 3.6. 메서드 참조

메서드 참조를 이용하면 기존의 메서드 정의를 재활용해서 람다처럼 전달할 수 있다.

- **기존 코드**

  ```java
  inventory.sort((Apple a1, Apple a2) ->
                a1.getWeight().compareTo(a2.getWeight()));
  ```

- **메서드 참조와 java.util.Comparator.comparing 활용**

  ```java
  inventory.sort(comparing(Apple::getWeight));
  ```

<br/>

## 3.6.1. 요약

**메서드 참조는** 특정 메서드만을 호출하는 람다의 축약형이라고 생각할 수 있다. 

명적으로 메서드명을 참조함으로써 **가독성을 높일 수 있다.**

- **메서드 참조 예제**

| 람다                                     | 메서드 참조 단축 표현            |
| ---------------------------------------- | -------------------------------- |
| (Apple apple) -> apple.getWeight()       | Apple::getWeight                 |
| () -> Thread.currentThread().dumpStack() | Tread.currentThread()::dumpStack |
| (str, i) -> str.substring(i)             | String::substring                |
| (String s) -> System.out.println(s)      | System.out::println              |
| (String s) -> this.isValidName(s)        | this::isValidName                |

<br/>

### 메서드 참조를 만드는 방법

메서드 참조 세 가지 유형

1. **정적 메서드 참조** : 예를 들어 Integer의 parseInt 메서드는 Integer::parseInt로 표현할 수 있다.
2. **다양한 형식의 인스턴스 메서드 참조** : 예를 들어 String의 length 메서드는 String::length로 표현할 수 있다.
3. **기존 객체의 인스턴스 메서드 참조** : 예를 들어 Transaction 객체를 할당받은 expensiveTransaction 지역 변수가 있고 Transaction 객체에는 getValue 메서드가 있다면, 이를 expensiveTransaction::getValue라고 표현할 수 있다.

<br/>

# 3.6.2. 생성자 참조

className::new 처럼 기존 생성자의 참조를 만들 수 있다. 이것은 정적 메서드의 참조를 만드는 방법과 비슷하다.

- **예시) Supplier 함수형 인터페이스 사용**

  ```java
  Supplier<Apple> c1 = Apple::new;
  //Supplier의 get 메서드를 호출해서 새로운 Apple 객체를 만들 수 있다.
  Apple a1 = c1.get();
  ```

  > 위 예제는 다음 코드와 같다.

  ```java
  // 람다 표현식은 디폴트 생성자를 가진 Apple을 만든다.
  Supplier<Apple> c1 = () -> new Apple();
  // 새로운 Apple 객체 생성
  Apple a1 = c1.get();
  ```

- **예시) Function 함수형 인터페이스 사용**

  Apple(Integer weight)라는 시그니처를 갖는 생성자는 Function 인터페이스의 시그니처와 같다.

  ```java
  // Apple(Integer weight)의 생성자 참조
  Function<Integr, Apple> c2 = Apple::new;
  // Function의 
  Apple a2 = c2.apply(110);
  ```

  > 위의 코드는 아래 코드와 같다.

  ```java
  Function<Integer, Apple> a2 = (weight) -> new Apple(weight);
  Apple a2 = c2.apply(110);
  ```

* **예시) 다양한 무게를 포함하는 사과 리스트**

  ```java
  public List<Apple> map(List<Integer> list, Function<Integer, Apple> f) {
    List<Apple> result = new ArrayList<>();
    for (Integer i : list){
      result.add(f.apply(i));
    }
    return result;
  }
  
  List<Integer> weights = Arrays.asList(7, 3, 4, 10);
  List<Apple> apples = map(weights, Apple::new);	// map 메서드로 생성자 참조 전달
  ```

- **예시) BiFunction 함수형 인터페이스**

  Apple (Color color, Integer weight) 처럼 두 인수를 갖는 생성자는 BiFunction 인터페이스와 같은 시그니처를 가지므로 다음처럼 사용할 수 있다.

  ```java
  // Apple(Color color, Integer, weight) 생성자 참조
  BiFunctio<Color, Integer, Apple> c3 = Apple::new;
  // BiFunction의 apply 메서드에 색과 무게를 인수로 제공
  Apple a3 = c3.apply(GREEN, 110);
  ```

  > 위의 코드는 다음과 같다.

  ```java
  // 특정 색과 무게를 가진 사과를 만드는 람다 표현식
  BiFunction<Color, Integer, Apple> c3 =
    (color, weight) -> new Apple(color, weight);
  // BiFunction의 apply 메서드에 색과 무게를 인수로 제공
  Apple a3 = c3.apply(GREEN, 110);
  ```

<br/>

인스턴스화하지 않고도 생성자에 접근할 수 있는 기능을 다양한 상황에 응용할 수 있다.

- **예시) Map으로 생성자와 문자열값을 관련시키다.**

  ```java
  static Map<String, Function<Integer, Fruit>> map = new HashMap<>();
  static {
    map.put("apple", Apple::new);
    map.put("orange", Orange::new);
    // 등등
  }
  
  public static Fruit giveMeFruit(String fruit, Integer weight) {
    return map.get(fruit.toLowerCase())		// map에서 Function<Integer, Fruit> 을 얻는다.
      .apply(weight);		// Function의 apply 메서드에 정수 무게 파라미터를 제공해서 Fruit를 만들 수 있다.
  }
  ```

<br/>

# 3.7. 람다, 메서드 참조 활용하기

## 3.7.1. 1단계 : 코드 전달

어떻게 sort 메서드에 정렬 전략을 전달할 수 있을까?

- **sort 메소드 시그니처**

  ```java
  void sort(Comparator<? super E> c)
  ```

  > 이 코드는 Comparator 객체를 인수로 받아 두 사과를 비교한다.

- **sort에 전달된 정렬 전략에 따라 sort의 동작이 달라진다.**

  ```java
  public class AppleComparator implements Comparator<Apple> {
    public int compare(Apple a1, Apple a2) {
      return a1.getWeight().compareTo(a2.getWeight());
    }
  }
  inventory.sort(new AppleComparator());
  ```

<br/>

## 3.7.2. 2단계 : 익명 클래스 사용

한 번만 사용할 Comparator를 위 코드처럼 구현하는 것보다는 **익명 클래스**를 이용하는 것이 좋다.

```java
inventory.sort(new Comparator<Apple>() {
  public int compare(Apple a1, Apple a2) {
    return a1.getWeight().compareTo(a2.getWeight());
  }
})
```

<br/>

## 3.7.3. 3단계 : 람다 표현식 사용

자바 8에서는 람다 표현식이라는 경량화된 문법을 이용해서 **코드를 전달**할 수 있다. **함수형 인터페이스**를 기대하는 곳 어디에서나 람다 표현식을 사용할 수 있다.

위의 코드를 다음과 같이 개선할 수 있다.

```java
inventory.sort(
  (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

<br/>

람다의 파라미터 형식 추론을 통해 코드를 더 줄일 수 있다.

```java
inventory.sort((a1, a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

<br/>

Comparator는 Comparable 키를 추출해서 Comparator 객체를 만들면 더욱 가독성을 향상시킬 수 있다.

```java
Comparator<Apple> c = Comparator.comparing((Apple a) -> a.getWeight());
```

<br/>

이제 코드를 다음처럼 간소화할 수 있다.

```java
import static java.util.Comparator.comparing;
inventory.sort(comparing(apple -> apple.getWeight()));
```

<br/>

## 3.7.4. 4단계 : 메서드 참조 사용

메서드 참조를 이용해서 코드를 조금 더 간소화할 수 있다**(java.util.Comparator.comparing은 정적으로 임포트했다고 가정).** 

```java
inventory.sort(comparing(Apple::getWeight));
```

드디어 최적의 코드를 만들었다. 즉, 코드 자체로 **'Apple을 weight 별로 비교해서 inventory를 sort하라'는 의미를 전달할 수 있다.**

<br/>

# 3.8. 람다 표현식을 조합할 수 있는 유용한 메서드

Comparator, Function, Predicate 같은 함수형 인터페이스는 람다 표현식을 조합할 수 있도록 유틸리티 메서드를 제공한다. 즉, 간단한 여러 개의 람다 표현식을 조합해서 복잡한 람다 표현식을 만들 수 있다.

여기서 등장하는 것이 바로 **디폴트 메서드(default method)**다.

<br/>

## 3.8.1. Comparator 조합

정적 메서드 Comparator.comparing을 이용해서 비교에 사용할 키를 추출하는 Function 기반의 Comparator를 반환할 수 있다.

```java
Comparator<Apple> c = Comparator.comparing(Apple::getWeight);
```

<br/>

### 역정렬

인터페이스 자체에서 주어진 비교자의 순서를 바꾸는 **reverse라는 디폴트 메서드를** 제공하기 때문에, 역정렬을 할 수 있다.

```java
inventory.sort(comparing(Apple::getWeight).reversed());		// 무게를 내림차순으로 정렬
```

<br/>

### Comparator 연결

thenComparing 메서드로 두 번째 비교자를 만들 수 있다. thenComparing은 함수를 메서드로 받아 첫 번째 비교자를 이용해서 두 객체가 같다고 판단되면 두 번째 비교자에 객체를 전달한다.

```java
inventory.sort(comparing(Apple::getWeight)
               .reversed()												// 무게를 내림차순으로 정렬
               .thenComparing(Apple::getCountry));// 두 사과의 무게가 같으면 국가별로 정렬
```

<br/>

## 3.8.2. Predicate **조합**

Predicate 인터페이스는 복잡한 프레디케이트를 만들 수 있도록 **negate, and, or** 세 가지 메서드를 제공한다.

- **negate 메서드**

  ```java
  // 기존 프레디케이트 객체 redApple의 결과를 반전시킨 객체를 만든다.
  Predicate<Apple> notRedApple = redApple.negate();
  ```

- **and 메서드**

  ```java
  // 두 프레디케이트를 연결해서 새로운 프레디케이트 객체를 만든다.
  Predicate<Apple> redAndHeavyApple = 
    redApple.and(apple -> apple.getWeight() > 150);
  ```

- **or 메서드**

  ```java
  // 프레디케이트 메서드를 연결해서 더 복잡한 프레디케이트 객체를 만든다.
  Predicate<Apple> redAndHeavyAppleOrGreen =
    redApple.and(apple -> apple.getWeight() > 150)
    				.or(apple -> GREEN.equals(apple.getColor()));
  ```

<br/>

## 3.8.3. Function 조합

Function 인터페이스는 Function 인스턴스를 반환하는 **andThen, compose** 두 가지 디폴트 메서드를 제공한다.

- **andThen 메서드**

  ```java
  Function<Integer, Integer> f = x -> x + 1;
  Function<Integer, Integer> g = x -> x * 2;
  Function<Integer, Integer> h = f.andThen(g);
  int result = h.apply(1);	// 4를 반환, g(f(x)) 와 같다.
  ```

- **compose 메서드**

  ```java
  Function<Integer, Integer> f = x -> x + 1;
  Function<Integer, Integer> g = x -> x * 2;
  Function<Integer, Integer> h = f.compose(g);
  int result = h.apply(1);	// 3을 반환, f(g(x)) 와 같다.
  ```

<br/>

# 3.9. 마치며

- **람다 표현식**은 익명 함수의 일종이다. 이름은 없지만, 파라미터 리스트, 바디, 반환 형식을 가지며 예외를 던질 수 있다.
- **함수형 인터페이스**는 하나의 추상 메서드만을 정의하는 인터페이스다.
- 람다 표현식을 이용해서 함수형 인터페이스의 추상 메서드를 즉석으로 제공할 수 있으며 **람다 표현식 전체가 함수형 인터페이스의 인스턴스로 취급된다.**
- Java.util.function 패키지는 **Predicate\<T>, Function\<T, R>, Supplier\<T>, Consumer\<T>, BinaryOperator\<T>** 등을 포함해서 자주 사용하는 다양한 함수형 인터페이스를 제공한다.
- 자바 8은 **기본형 특화 인터페이스도 제공한다.**
- **실행 어라운드 패턴**을 람다와 활용하면 유연성과 재사용성을 추가로 얻을 수 있다.
- Comparator, Predicate, Function 같은 함수형 인터페이스는 람다 표현식을 조합할 수 있는 **다양한 디폴트 메서드를 제공한다.**