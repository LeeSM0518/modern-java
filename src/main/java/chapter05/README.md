# 5. 스트림 활용

스트림 API가 지원하는 연산을 이용해서 **필터링, 슬라이싱, 매핑, 검색, 매칭, 리듀싱** 등 다양한 데이터 처리 질의를 표현할 수 있다.

<br/>

# 5.1. 필터링

## 5.1.1. 프레디케이트로 필터링

filter 메서드는 **프레디케이트(불리언을 반환하는 함수)를** 인수로 받아서 프레디케이트와 일치하는 모든 요소를 포함하는 스트림을 반환한다.

* **예시) 모든 채식요리를 필터링해서 채식 메뉴를 만듬**

  ```java
  List<Dish> vegetarianMenu = menu.stream()
          .filter(Dish::isVegetarian)		// 채식 요리인지 확인하는 메서드 참조
          .collect(toList());
  ```

<br/>

## 5.1.2. 고유 요소 필터링

스트림은 고유 요소로 이루어진 스트림을 반환하는 distinct 메서드도 지원한다(고유 여부는 스트림에서 만든 객체의 hashCode, equals로 결정된다).

* **예시) 리스트의 모든 짝수를 선택하고 중복을 필터링**

  ```java
  List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
      numbers.stream()
          .filter(i -> i % 2 == 0)
          .distinct()
          .forEach(System.out::println);
  ```

<br/>

# 5.2. 스트림 슬라이싱

## 5.2.1. 프레디케이트를 이용한 슬라이싱

자바 9은 스트림의 요소를 효과적으로 선택할 수 있도록 **takeWhile, dropWhile** 두 가지 새로운 메서드를 지원한다.

<br/>

### TAKEWHILE 활용

다음과 같은 특별한 요리 목록을 갖고 있다.

```java
List<Dish> menu = Arrays.asList(
  new Dish("pork", false, 800, Type.MEAT),
  new Dish("beef", false, 700, Type.MEAT),
  new Dish("chicken", false, 400, Type.MEAT),
  new Dish("french fries", true, 530, Type.MEAT),
  new Dish("rice", true, 350, Type.MEAT),
  new Dish("season", true, 120, Type.MEAT),
  new Dish("pizza", true, 550, Type.MEAT),
  new Dish("prawns", false, 300, Type.MEAT),
  new Dish("salmon", false, 450, Type.MEAT)
);
```

* **예시) 어떻게 320 칼로리 이하의 요리를 선택할 수 있을까?**

  ```java
  List<Dish> filterMenu = 
    specialMenu.stream()
    .filter(dish -> dish.getCalories() < 320)
    .collect(toList());
  ```

  * 위와 같이 filter 연산을 이용해도 되지만, 이미 정렬되어 있다면 320 칼로리보다 **크거나 같은 요리가 나왔을 때 반복 작업을 중단시키는 것이** 훨씬 효율적이다.

<br/>

* **예시) takeWhile 사용**

  ```java
  List<Dish> slicedMenu1 =
    speicalMenu.stream()
    .takeWhile(dish -> dish.getCalories() < 320)
    .collect(toList());
  ```

  * takeWhile을 이용하면 무한 스트림을 포함한 모든 스트림에 프레디케이트를 적용해 스트림을 슬라이스할 수 있다.

<br/>

### DROPWHILE 활용

* **예시) dropWhile을 사용**

  ```java
  List<Dish> sliceMenu2 = menu.stream()
    .dropWhile(dish -> dish.getCalories() < 320)
    .collect(toList());
  ```

  * dropWhile은 takeWhile과 정반대의 작업을 수행한다. dropWhile은 **프레디케이트가 처음으로 거짓이 되는 지점까지 발견된 요소를 버린다.** 프레디케이트가 **거짓이 되면 그 지점에서 작업을 중단하고 남은 모든 요소를 반환한다.**

<br/>

## 5.2.2. 스트림 축소

스트림은 주어진 값 이하의 크기를 갖는 새로운 스트림을 반환하는 **limit(n)** 메서드를 지원한다.

* **예시) 300칼로리 이상의 세 요리 선택**

  ```java
  List<Dish> dishes = menu.stream()
    .filter(dish -> dish.getCalories() > 300)
    .limit(3)
    .collect(toList());
  ```

<br/>

## 5.2.3. 요소 건너뛰기

스트림은 처음 n개 요소를 제외한 스트림을 반환하는 **skip(n)** 메서드를 지원한다.

* **예시) 300칼로리 이상의 처음 두 요리를 건너뛴 다음에 300칼로리가 넘는 나머지 요리 선택**

  ```java
  List<Dish> dishes1 = menu.stream()
    .filter(d -> d.getCalories() > 300)
    .skip(2)
    .collect(toList());
  ```

<br/>

# 5.3. 매핑

스트림 API의 **map과 flatMap 메서드는** 특정 객체에서 특정 데이터를 선택하는 기능을 제공한다.

<br/>

## 5.3.1. 스트림의 각 요소에 함수 적용하기

스트림은 **함수를 인수로 받는 map 메서드를 지원한다.** 인수로 제공된 함수는 각 요소에 적용되며 함수를 적용한 결과가 **새로운 요소로 매핑된다.**

* **예시) 요리명을 추출하는 코드**

  ```java
  List<String> dishNames = menu.stream()
    .map(Dish::getName)
    .collect(toList());
  ```

* **예시) 단어를 인수로 받아서 길이를 반환하는 코드**

  ```java
  List<String> words = Arrays.asList("Modern", "Java", "In", "Action");
  List<Integer> wordLengths = words.stream()
    .map(String::length)
    .collect(toList());
  ```

* **예시) 요리명의 길이를 추출하는 코드**

  ```java
  List<Integer> dishNameLengths = menu.stream()
    .map(Dish::getName)
    .map(String::length)
    .collect(toList());
  ```

<br/>

## 5.3.2. 스트림 평면화

### map과 Arrays.stream 활용

객체를 받아 스트림을 만드는 **Arrays.stream()** 메서드가 있다.

* **예시) 문자열 배열을 스트림으로 만드는 예시**

  ```java
  String[] arrayOfWords = {"Goodbye", "World"};
  Stream<String> streamOfWords = Arrays.stream(arrayOfWords);
  ```

* **예시) 중복 문자 제거 예시**

  ```java
  List<String> strings = streamOfWords
  	.map(word -> word.split(""))
  	.map(Arrays::stream)
  	.distinct()
  	.collect(toList());
  ```

  * Stream\<Stream\<String>> 이 만들어지면서 문제가 해결되지 않는다. 문제를 해결하려면 먼저 각 단어를 개별 문자열로 이루어진 배열로 만든 다음에 각 배열을 별도의 스트림으로 만들어야 한다.

<br/>

### flatMap 사용

* **예시) flatMap 사용해서 중복 문자 제거**

  ```java
  List<String> uniqueCharacters = streamOfWords
          .map(word -> word.split(""))	// 각 단어를 개별 문자를 포함하는 배열로 변환
          .flatMap(Arrays::stream)			// 생성된 스트림을 하나의 스트림으로 평면화
          .distinct()
          .collect(toList());
  ```

  * flatMap 메서드는 스트림의 각 값을 다른 스트림으로 만든 다음에 모든 스트림을 하나의 **스트림으로 연결하는 기능을 수행한다.**

<br/>

# 5.4. 검색과 매칭

**특정 속성이 데이터 집합에 있는지 여부를 검색하는 데이터 처리도 자주 사용된다.** 스트림 API는 allMatch, anyMatch, noneMatch, findFirst, findAny 등 다양한 유틸리티 메서드를 제공한다.

<br/>

## 5.4.1. 프레디케이트가 적어도 한 요소와 일치하는지 확인

프레디케이트가 주어진 스트림에서 **적어도 한 요소와 일치하는지 확인할 때 anyMatch** 메서드를 이용한다.

* **예시) menu에 채식요리가 있는지 확인하는 예제**

  ```java
  if (menu.stream().anyMatch(Dish::isVegetarian)) {
    System.out.println("The menu is (somewhat) vegetarian friendly!!");
  }
  ```

  * anyMatch는 **불리언을 반환하므로 최종 연산이다.**

<br/>

## 5.4.2. 프레디케이트가 모든 요소와 일치하는지 검사

**allMatch** 메서드는 스트림의 **모든 요소가 주어진 프레디케이트와 일치하는지 검사한다.**

* **예시) 모든 요리가 1000 칼로리 이하인지 확인**

  ```java
  boolean isHealthy = menu.stream()
          .allMatch(dish -> dish.getCalories() < 1000);
  ```

<br/>

### NONEMATCH

**noneMatch는 allMatch와 반대 연산을 수행한다.** 즉, 주어진 프레디케이트와 일치하는 요소가 없는지 확인한다.

* **예시) 모든 요리가 1000 칼로리 이상이 아닌지 확인**

  ```java
  boolean isHealthy = menu.stream()
          .noneMatch(d -> d.getCalories() >= 1000);
  ```

<br/>

anyMatch, allMatch, noneMatch 세 메서드는 스트림 **쇼트서킷** 기법, 즉 자바의 &&, || 와 같은 연산을 활용한다.

<br/>

## 5.4.3. 요소 검색

**findAny 메서드는 현재 스트림에서 임의의 요소를 반환한다.** findAny 메서드를 다른 스트림연산과 연결해서 사용할 수 있다.

* **예시) 채식 요리 선택**

  ```java
  Optional<Dish> dish =
          menu.stream()
          .filter(Dish::isVegetarian)
          .findAny();
  ```

<br/>

### Optional 이란?

**Optional\<T>** 클래스 (java.util.Optional)는 값의 존재나 여부를 표현하는 컨테이너 클래스다. 

Optional은 값이 **존재하는지 확인하고 값이 없을 때 어떻게 처리할지 강제하는 기능을 제공한다.**

* **Optinal 의 메서드들**
  * **isPresent()** 는 Optional 이 값을 포함하면 참을 반환하고, 값을 포함하지 않으면 거짓을 반환한다.
  * **ifPresent(Consumer\<T> block)** 은 값이 있으면 주어진 블록을 실행한다.
  * **T get()** 은 값이 존재하면 값을 반환하고, 값이 없으면 NoSuchElementException을 일으킨다.
  * **T orElse(T other)** 는 값이 있으면 값을 반환하고, 값이 없으면 기본값을 반환한다.

* **예시) 임의의 요소를 찾고 출력**

  ```java
  menu.stream()
          .filter(Dish::isVegetarian)
          .findAny()	// Optional<Dish> 반환
          .ifPresent(dish -> System.out.println(dish.getName()));
  // 값이 있으면 출력, 없으면 아무 일도 일어나지 않음
  ```

<br/>

## 5.4.4. 첫 번째 요소 찾기

* **예시) 숫자 리스트에서 3으로 나누어떨어지는 첫 번째 제곱값을 출력하는 코드**

  ```java
  someNumbers.stream()
          .map(n -> n * n)
          .filter(n -> n % 3 == 0)
          .findFirst()
          .ifPresent(System.out::println);
  ```

* **findFirst 와 findAny는 언제 사용하나?**

  병렬 실행에서는 첫 번째 요소를 찾기 어렵기 때문에, 요소의 반환 순서가 상관없다면 병렬 스트림에서는 제약이 적은 findAny를 사용한다.

<br/>

# 5.5. 리듀싱

**리듀싱 연산이란** 모든 스트림 요소를 처리해서 값으로 도출하는 연산 방법이다.

함수형 프로그래밍 언어 용어로는 이 과정이 마치 종이를 작은 조각이 될 때까지 반복해서 접는 것과 비슷하다는 의미로 **폴드(fold)** 라고 부른다.

<br/>

## 5.5.1. 요소의 합

* **예시) 스트림의 모든 요소를 더하는 코드**

  ```java
  int sum = numbers.stream().reduce(0, (a, b) -> a + b);
  // Integer의 sum 정적 메서드를 참조할 수도 있다.
  int sum = numbers.stream().reduce(0, Integer::sum);
  ```

  **reduce의 인수들**

  * **0** : 초깃값
  * **(a, b) -> a + b** : 두 요소를 조합해서 새로운 값을 만드는 BinaryOperator\<T>.

스트림에서의 reduce 연산 과정은 스트림이 하나의 값으로 줄어들 때까지 람다는 각 요소를 반복해서 조합한다.

<br/>

### 초깃값 없음

초깃값을 받지 않도록 오버로드된 reduce도 있다. 그러나 이 reduce는 Optional 객체를 반환한다.

```java
Optional<Integer> sum = numbers.stream().reduce(Integer::sum);
```

> Optional을 반환하는 이유는 스트림에 아무 요소가 없으면 초깃값이 없으므로 reduce는 합계를 반환할 수 없기 때문이다.

<br/>

## 5.5.2. 최댓값과 최솟값

최댓값과 최솟값을 찾을 때도 reduce를 활용할 수 있다.

* **reduce 메소드의 인수들**
  * 초깃값
  * 스트림의 두 요소를 합쳐서 하나의 값으로 만드는데 사용할 람다

* **예시) 최소값과 최대값을 구하는 코드**

  ```java
  Optional<Integer> min = numbers.stream().reduce(Integer::min);
  Optional<Integer> max = numbers.stream().reduce(Integer::max);
  ```

<br/>

### reduce 메서드의 장점과 병렬화

reduce를 이용하면 **내부 반복이 추상화되면서** 내부 구현에서 병렬로 reduce를 실행할 수 있게 된다.

스트림의 모든 요소를 더하는 코드를 병렬로 만드는 방법은 stream()을 **parallelStream()으로** 바꾸면 된다.

<br/>

### 스트림 연산 : 상태 없음과 상태 있음

**map, filter** 등은 입력 스트림에서 각 요소를 받아 0 또는 결과를 출력 스트림으로 보낸다. 따라서 이들은 보통 상태가 없는, 즉 **내부 상태를 갖지 않는 연산(stateless operation)이다.**

하지만 **reduce, sum, max** 같은 연산은 결과를 누적할 내부 상태가 필요하다. 스트림에서 처리하는 요소 수와 관계없이 내부 상태의 크기는 **한정(bounded)되어** 있다.

반면 sorted나 distinct 같은 연산은 filter나 map처럼 스트림을 입력으로 받아 다른 스트림을 출력하는 것처럼 보일 수 있다. 하지만 sorted나 distinct는 filter나 map과는 다르다. 예를 들어 어떤 요소를 출력 스트림으로 추가하려면 **모든 요소가 버퍼에 추가되어 있어야 한다.** 이러한 연산을 **내부 상태를 갖는 연산(stateful operation)** 이라 한다.

<br/>

### 중간 연산과 최종 연산

| 연산      | 형식      | 반환 형식    | 함수형 인터페이스 형식   | 함수 디스크립터 |
| --------- | --------- | ------------ | ------------------------ | --------------- |
| filter    | 중간 연산 | Stream\<T>   | Predicate\<T>            | T -> boolean    |
| distinct  | 중간 연산 | Stream\<T>   | -                        | -               |
| takeWhile | 중간 연산 | Stream\<T>   | Predicate\<T>            | T -> boolean    |
| dropWhile | 중간 연산 | Stream\<T>   | Predicate\<T>            | T -> boolean    |
| skip      | 중간 연산 | Stream\<T>   | long                     | -               |
| limit     | 중간 연산 | Stream\<T>   | long                     | -               |
| map       | 중간 연산 | Stream\<R>   | Function\<T, R>          | T -> R          |
| flatMap   | 중간 연산 | Stream\<R>   | Function\<T, Stream\<R>> | T -> Stream\<R> |
| sorted    | 중간 연산 | Stream\<T>   | Comparator\<T>           | (T, T) -> int   |
| anyMatch  | 최종 연산 | boolean      | Predicate\<T>            | T -> boolean    |
| noneMatch | 최종 연산 | boolean      | Predicate\<T>            | T -> boolean    |
| allMatch  | 최종 연산 | boolean      | Predicate\<T>            | T -> boolean    |
| findAny   | 최종 연산 | Optional\<T> | -                        | -               |
| findFirst | 최종 연산 | Optional\<T> | -                        | -               |
| forEach   | 최종 연산 | void         | Consumer\<T>             | T -> void       |
| collect   | 최종 연산 | R            | Collector\<T, A, R>      | -               |
| reduce    | 최종 연산 | Optional\<T> | BinaryOperator\<T>       | (T, T) -> T     |
| count     | 최종 연산 | long         | -                        | -               |

<br/>

# 5.6. 실전 연습

## 5.6.1. 거래자와 트랜잭션

* **Trader 클래스**

  ```java
  package chapter05.traderandtransaction;
  
  public class Trader {
  
    private final String name;
    private final String city;
  
    public Trader(String name, String city) {
      this.name = name;
      this.city = city;
    }
  
    public String getName() {
      return name;
    }
  
    public String getCity() {
      return city;
    }
  
    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("Trader{");
      sb.append("name='").append(name).append('\'');
      sb.append(", city='").append(city).append('\'');
      sb.append('}');
      return sb.toString();
    }
  }
  ```

* **Transaction 클래스**

  ```java
  package chapter05.traderandtransaction;
  
  public class Transaction {
  
    private final Trader trader;
    private final int year;
    private final int value;
  
    public Transaction(Trader trader, int year, int value) {
      this.trader = trader;
      this.year = year;
      this.value = value;
    }
  
    public Trader getTrader() {
      return trader;
    }
  
    public int getYear() {
      return year;
    }
  
    public int getValue() {
      return value;
    }
  
    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("Transaction{");
      sb.append("trader=").append(trader);
      sb.append(", year=").append(year);
      sb.append(", value=").append(value);
      sb.append('}');
      return sb.toString();
    }
  }
  ```

* **Main 클래스**

  ```java
  package chapter05.traderandtransaction;
  
  import java.util.Arrays;
  import java.util.List;
  
  import static java.util.Comparator.comparing;
  import static java.util.stream.Collectors.toList;
  
  public class Main {
  
    public static void main(String[] args) {
      Trader raoul = new Trader("Raoul", "Cambridge");
      Trader mario = new Trader("Mario", "Milan");
      Trader alan = new Trader("Alan", "Cambridge");
      Trader brian = new Trader("Brian", "Cambridge");
  
      List<Transaction> transactions = Arrays.asList(
          new Transaction(brian, 2011, 300),
          new Transaction(raoul, 2012, 1000),
          new Transaction(raoul, 2011, 400),
          new Transaction(mario, 2012, 710),
          new Transaction(mario, 2012, 700),
          new Transaction(alan, 2012, 950)
      );
    }
  
  }
  ```

<br/>

## 5.6.2. 실전 연습 정답

* **예제1) 2011년에 일어난 모든 트랜잭션을 찾아서 값을 오름차순으로 정렬하시오.**

  ```java
  // Comparator 와 Collectors static 선언
  import static java.util.Comparator.comparing;
  import static java.util.stream.Collectors.toList;
  
  List<Transaction> transactionList  = transactions.stream()
    // 2011년에 발생한 트랜잭션을 필터링하도록 프레디케이트를 넘겨줌
    .filter(transaction -> transaction.getYear() == 2011)	
    // 트랜잭션 값으로 요소 정렬
    .sorted(comparing(Transaction::getValue))
    // 결과 스트림의 모든 요소를 리스트로 반환
    .collect(toList());
  ```

<br/>

* **예제2) 거래자가 근무하는 모든 도시를 중복 없이 나열하시오.**

  ```java
  List<String> cityList = transactions.stream()
    // 트랜잭션과 관련한 각 거래자의 도시 추출
    .map(transaction -> transaction.getTrader().getCity())
    // 고유 도시만 선택
    .distinct()
    .collect(toList());
  ```

  > distinct() 대신에 스트림을 집합으로 변환하는 toSet()을 사용할 수 있다.

  ```java
  Set<String> cities =
    transactions.stream()
    .map(transaction -> transaction.getTrader().getCity())
    .collect(Collectors.toSet());
  ```

<br/>

* **예제3) 케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬하시오.**

  ```java
  List<Trader> nameList = transactions.stream()
    // 트랜잭션의 모든 거래자 추출
    .map(Transaction::getTrader)
    // 케임브리지의 거래자만 선택
    .filter(trader -> trader.getCity().equals("Cambridge"))
    // 중복이 없도록 확인
    .distinct()
    // 결과 스트림의 거래자를 이름으로 정렬
    .sorted(comparing(Trader::getName))
    .collect(toList());
  ```

<br/>

* **예제4) 모든 거래자의 이름을 알파벳순으로 정렬해서 반환하시오.**

  ```java
  String traderStr = transactions.stream()
    // 모든 거래자명을 문자열 스트림으로 추출
    .map(transaction -> transaction.getTrader().getName())
    // 중복된 이름 제거
    .distinct()
    // 이름을 알파벳 순으로 정렬
    .sorted()
    // 각각의 이름을 하나의 문자열로 연결하여 결국 모든 이름 연결
    .reduce("", (n1, n2) -> n1 + n2);
  ```

  > 위의 경우는 모든 문자열을 반복적으로 연결해서 새로운 문자열 객체를 만든다. 따라서 효율성이 떨어진다.
  >
  > StringBuilder 에서 joining의 메서드를 사용하게 되면 더욱 깔끔하고 효율적인 코드를 짤 수 있다.

  ```java
  String traderStr2 = transactions.stream()
    .map(transaction -> transaction.getTrader().getName())
    .distinct()
    .sorted()
    .collect(Collectors.joining());
  ```

<br/>

* **예제5) 밀라노에 거래자가 있는가?**

  ```java
  boolean milanBased = transactions.stream()
    // anyMatch에 프레디케이트를 전달해서 밀라노에 거래자가 있는지 확인
    .anyMatch(transaction -> transaction.getTrader().getCity().equals("Milan"));
  ```

<br/>

* **예제6) 케임브리지에 거주하는 거래자의 모든 트랜잭션값을 출력하시오.**

  ```java
  transactions.stream()
    // 케임브리지에 거주하는 거래자의 트랜잭션을 선택
    .filter(transaction -> "Cambridge".equals(transaction.getTrader().getCity()))
    // 각 값을 출력
    .map(Transaction::getValue)
    // 이 거래자들의 값 추출
    .forEach(System.out::println);
  ```

<br/>

* **예제7) 전체 트랜잭션 중 최대값은 얼마인가?**

  ```java
  Optional<Integer> max = transactions.stream()
    // 각 트랜잭션의 값 추출
    .map(Transaction::getValue)
    // 결과 스트림의 최댓값 계산
  	.reduce(Integer::max);
  ```

<br/>

* **예제8) 전체 트랜잭션 중 최솟값은 얼마인가?**

  ```java
  Optional<Transaction> smallestTransaction = transactions.stream()
    // 각 트랜잭션값을 반복 비교해서 가장 작은 트랜잭션 검색
    .reduce((t1, t2) ->
            t1.getValue() < t2.getValue() ? t1 : t2);
  ```

  > Comparator를 인수로 받는 min과 max 메서드를 이용하면 최댓값이나 최솟값을 계산하는데 편리하다.

  ```java
  Optional<Transaction> smallestTransaction2 = transactions.stream()
    .min(comparing(Transaction::getValue));
  ```

<br/>

# 5.7. 숫자형 스트림

* **예시) 메뉴의 칼로리 합계 계산**

  ```java
  int calories = menu.stream()
    .map(Dish::getCalories)
    .reduce(0, Integer::sum);
  ```

  > 위의 코드는 박싱 비용이 숨겨져있다. 내부적으로 합계를 계산하기 전에 Integer를 기본형으로 언박싱해야 한다.

  ```java
  int calories = menu.stream()
    .map(Dish::getCalories)
    .sum();	// 이와 같은 방식으로 sum 메서드를 호출할 수 없을까?
  ```

다행히도 스트림 API 숫자 스트림을 효율적으로 처리할 수 있도록 **기본형 특화 스트림(primitive stream specialization)을** 제공한다.

<br/>

## 5.7.1. 기본형 특화 스트림

자바 8에서는 세 가지 기본형 특화 스트림을 제공한다.

- int 요소에 특화된 **IntStream**
- double 요소에 특화된 **DoubleStream**
- long 요소에 특화된 **LongStream**

<u>특화 스트림은 오직 박싱 과정에서 일어나는 효율성과 관련 있으며 스트림에 추가 기능을 제공하지는 않는다.</u>

<br/>

### 숫자 스트림으로 매핑

스트림을 특화 스트림으로 변환할 때는 **mapToInt, mapToDouble, mapToLong** 세 가지 메서드를 가장 많이 사용한다.

이러한 메서드들은 **Stream\<T> 대신 특화된 스트림을 반환한다.**

- **예시) sum**

  ```java
  int calories2 = menu.stream()		// Stream<Dish> 변환
    .mapToInt(Dish::getCalories)	// IntStream 변환
    .sum();
  ```

  

<br/>

### 객체 스트림으로 복원하기

**boxed** 메서드를 이용해서 특화 스트림을 일반 스트림으로 변환할 수 있다.

- **예제) IntStream 에서 Stream\<Integer> 로 변환**

  ```java
  // 스트림을 숫자 스트림으로 변환
  IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
  // 숫자 스트림을 스트림으로 변환
  Stream<Integer> stream = intStream.boxed();	
  ```

<br/>

### 기본값: OptionalInt

Optional을 Integer, String 등의 참조 형식으로 파라미터화할 수 있다. 또한 **OptionalInt, OptionalDouble, OptionalLong** 세 가지 기본형 특화 스트림 버전도 제공한다.

- **예시) IntStream의 최댓값 요소 찾기**

  ```java
  OptionalInt maxCalories = menu.stream()	// Stream<Dish> 반환
    .mapToInt(Dish::getCalories)					// IntStream 반환
    .max();
  ```

- **예시) 최대값을 명시적으로 설정**

  ```java
  int max = maxCalories.orElse(1);
  ```

OptionalInt를 사용하면 값이 없는 값을 출력하면 **OptionalInt.empty** 출력된다.

<br/>

## 5.7.2. 숫자 범위

프로그램에서는 특정 범위의 숫자를 이용해야 하는 상황이 자주 발생한다. 그래서 자바 8의 **IntStream 과 LongStream 에서는 range 와 rangeClosed 라는 두 가지 정적 메서드를 제공한다.**

- **예시) 1부터 100까지의 짝수를 가져오는 코드**

  ```java
  IntStream evenNumbers = IntStream.rangeClosed(1, 100)	// [1,100]의 범위를 나타낸다.
    .filter(n -> n % 2 == 0);		// 1부터 100까지의 짝수 스트림
  System.out.println(evenNumbers.count());		// 개수 카운트
  ```

  > rangeClosed 대신에 range를 사용하면 (1,100) 에서 1과 100을 포함하지 않는다.

<br/>

## 5.7.3. 숫자 스트림 활용 : 피타고라스 수

### 피타고라스 수

피타고라스는 (a * a) + (b * b) = (c * c) 공식을 만족하는 세 개의 정수 (a, b, c)를 뜻한다.

<br/>

### 세 수 표현하기

우선 세 수를 정의해야 한다.

```java
int[] pythagoras = {3, 4, 5};
```

<br/>

### 좋은 필터링 조합

누군가 세 수 중에서 a, b 두 수만 제공했다고 가정하자. 두 수가 피타고라스 수의 일부가 될 수 있는 좋은 조합인지 어떻게 확인할 수 있을까? 

```java
// a*a + b*b 의 제곱근이 정수인지 판별한다.
Math.sqrt(a*a + b*b) % 1 == 0;
```

이를 filter에 적용할 수 있다.

```java
filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
```

위 코드에서 a라는 값이 주어지고 b는 스트림으로 제공된다고 가정할 때 filter로 a와 함께 피타고라스 수를 구성하는 모든 b를 필터링할 수 있다.

<br/>

### 집합 생성

세 번째 수를 찾아야 한다.

```java
stream.filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
  .map(b -> new int[]{a, b, (int) Math.sqrt(a*a + b*b)});
```

<br/>

### b값 생성

1부터 100까지의 b값 생성

```java
IntStream.rangeClosed(1, 100)	// 1부터 100까지의 b값 생성
  .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
  .boxed()	// boxed를 이용해서 Stream<Integer>로 복원
  .map(b -> new int[]{a, b, (int) Math.sqrt(a*a + b*b)});
```

> mapToObj 메서드를 이용하면 위와 같은 코드를 작성할 수 있다.

```java
IntStream.rangeClosed(1, 100)
  .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
  .mapToObj(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)});
```

<br/>

### a 값 생성

마지막으로 a값을 생성하는 코드를 추가한다.

```java
Stream<int[]> pythagoreanTriples =
  IntStream.rangeClosed(1, 100).boxed()
  .flatMap(a ->
           IntStream.rangeClosed(a, 100)
           .filter(b -> Math.sqrt(a * a + b * b) % 1 == 0)
           .mapToObj(b ->
                     new int[]{a, b, (int) Math.sqrt(a * a + b * b)}));
```

1. 우선 a에 사용할 1부터 100까지의 숫자를 만든다.
2. 주어진 a를 이용해서 세 수의 스트림을 만든다.
3. flatMap 메서드를 이용해서 세 수로 이루어진 스트림을 얻는다. 여기서 b를 a에서 100까지 생성한다.

<br/>

### 코드 실행

이제 코드 구현은 완료되었고 limit을 이용해서 얼마나 많은 세 수를 포함하는 스트림을 만들 것인지만 결정하면 된다.

```java
pythagoreanTriples.limit(5)
    .forEach(t ->
        System.out.println(t[0] + ", " + t[1] + ", " + t[2]));
```

**실행 결과**

```
3, 4, 5
5, 12, 13
6, 8, 10
7, 24, 25
8, 15, 17
```

<br/>

### 개선할 점?

- **개선된 코드**

  ```java
  Stream<double[]> pythagoreanTriples2 =
    IntStream.rangeClosed(1, 100).boxed()
    .flatMap(a -> IntStream.rangeClosed(a, 100)
             .mapToObj(
               // 만들어진 세 수
               b -> new double[]{a, b, Math.sqrt(a * a + b * b)})
             .filter(t -> t[2] % 1 == 0));	// 세 수의 세 번째 요소는 반드시 정수여야 한다.
  ```

  > 즉, 우리가 원하는 조건에 맞는 결과만 필터링하는 것이 더 최적화된 방법이다.

<br/>

# 5.8. 스트림 만들기

## 5.8.1. 값으로 스트림 만들기

임의의 수를 인수로 받는 정적 메서드 Stream.of를 이용해서 스트림을 만들 수 있다.

- **예시) Stream.of 로 문자열 스트림을 만드는 코드**

  ```java
  // 문자열 스트림 생성
  Stream<String> stringStream = Stream.of("Modern ", "Java ", "In ", "Action");
  // 스트림의 모든 문자열을 대문자로 변환한 후 문자열을 하나씩 출력한다.
  stringStream.map(String::toUpperCase).forEach(System.out::println);
  ```

**empty** 메서드를 이용해서 스트림을 비울 수 있다.

```java
Stream<String> emptyStream = Stream.empty();
```

<br/>

## 5.8.2. null이 될 수 있는 객체로 스트림 만들기

자바 9에서는 null이 될 수 있는 객체를 스트림으로 만들 수 있는 새로운 메소드가 추가되었다.

- **자바 9 이전의 코드**

  ```java
  String homeValue = System.getProperty("home");
  Stream<String> homeValueStream =
    homeValue == null ? Stream.empty() : Stream.of(value);
  ```

- **자바 9 이후의 코드**

  ```java
  Stream<String> homeValueStream =
    Stream.ofNullable(System.getProperty("home"));
  ```

- **null이 될 수 있는 객체를 포함하는 스트림값을 flatMap과 함께 사용하는 상황에서는 이 패턴을 더 유용하게 사용할 수 있다.**

  ```java
  Stream<String> values =
    Stream.of("config", "home", "user")
    .flatMap(key -> Stream.ofNullable(System.getProperty(key)));
  ```

<br/>

## 5.8.3. 배열로 스트림 만들기

배열을 인수로 받는 정적 메서드 **Arrays.stream을** 이용해서 스트림을 만들 수 있다.

- **예시) 기본형 int로 이루어진 배열을 IntStream으로 변환**

  ```java
  int[] numbers = {2, 3, 5, 7, 11, 13};
  int sum = Arrays.stream(numbers).sum();
  ```

<br/>

## 5.8.4. 파일로 스트림 만들기

파일을 처리하는 등의 I/O 연산에 사용하는 자바의 NIO API(비블록 I/O)도 스트림 API를 활용할 수 있도록 업데이트되었다.

- **예시) 파일에서 고유한 단어 수를 찾는 프로그램**

  ```java
  long uniqueWords = 0;
  // 스트림은 자원을 자동으로 해제할 수 있는 AutoCloseable이므로 try-finally 가 필요없다.
  try (Stream<String> lines =
      Files.lines(Paths.get("data.txt"), Charset.defaultCharset())) {
    uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))	// 고유 단어 수 계산
      .distinct()	// 중복 제거
      .count();		// 단어 스트림 생성
  } catch (IOException e) {
    // 파일을 열다가 예외가 발생하면 처리한다.
  }
  ```

<br/>

## 5.8.5. 함수로 무한 스트림 만들기

스트림 API는 함수에서 스트림을 만들 수 있는 두 정적 메서드 **Stream.iterate 와 Stream.generate** 를 제공한다. 

두 연산을 이용해서 **무한 스트림(infinite stream),** 즉 고정된 컬렉션에서 고정된 크기로 스트림을 만들었던 것과는 달리 크기가 **고정되지 않은 스트림을 만들 수 있다.** 

Iterate 와 generate 에서 만든 스트림은 요청할 때마다 주어진 함수를 이용해서 값을 만든다. 따라서 무제한으로 값을 계산할 수 있다. 하지만 보통 무한한 값을 출력하지 않도록 **limit(n)** 함수를 함께 사용한다.

<br/>

### iterate 메서드

Iterate를 사용하는 방법

```java
Stream.iterate(0, n -> n + 2)
  .limit(10)
  .forEach(System.out::println);
```

> 짝수 스트림을 생성한다.

- iterate 메서드는 **초깃값(예제에서는 0)과 람다(예제에서는 UnaryOperator\<T>)를** 인수로 받아서 새로운 값을 끊임없이 생산할 수 있다.
- iterate는 요청할 때마다 값을 생산할 수 있으며 끝이 없으므로 **무한 스트림(infinite stream)을** 만든다. 이러한 스트림을 **언바운드 스트림(unbounded stream)** 이라고 표현한다.

<br/>

자바 9의 iterate 메소드는 프레디케이트를 지원한다.

- **예시) 0에서 시작해서 100보다 크면 숫자 생성을 중단하는 코드**

  ```java
  IntStream.iterate(0, n -> n < 100, n -> n + 4)
    .forEach(System.out::println);
  ```

<br/>

위의 동작이 filter 동작으로도 같은 결과를 얻을 수 있다고 생각할 수도 있을 것이다. 하지만 filter 메소드는 언제 이 작업을 중단해야 하는지를 알 수 없기 때문에 위와 같은 실행 결과를 얻을 수 없다. 스트림 쇼트서킷을 지원하는 **takeWhile을** 이용하는 것이 해법이다.

```java
IntStream.iterate(0, n -> n + 4)
  .takeWhile(n -> n < 100)
  .forEach(System.out::println);
```

<br/>

### generate 메서드

iterate와 달리 generate는 생산된 각 값을 연속적으로 계산하지 않는다. generate는 Supllier\<T>를 인수로 받아서 새로운 값을 생산한다.

- **예시) 0에서 1 사이에서 임의의 더블 숫자 다섯 개를 만든다.**

  ```java
  Stream.generate(Math::random)
    .limit(5)
    .forEach(System.out::println);
  ```

  실행 결과

  ```
  0.9337433131563336
  0.4172176470755006
  0.6540317025662936
  0.01832734139794512
  0.9334720065276012
  ```

  > Math.random 은 임의의 새로운 값을 생성하는 정적 메서드다. 이번에도 명시적으로 limit 메서드를 이용해서 스트림의 크기를 한정했다. **limit가 없다면 스트림은 언바운드 상태가 된다.**

<br/>

**IntStream을** 이용하면 박식 연산 문제를 피할 수 있다. IntStream의 generate 메서드는 Supplier\<T> 대신에 IntSupplier를 인수로 받는다.

* **무한 스트림을 생성하는 코드**

  ```java
  IntStream ones = IntStream.generate(() -> 1);
  ```

  > IntSupplier 인터페이스에 정의된 getAsInt를 구현하는 객체를 명시적으로 전달할 수도 있다.

* **피보나치 요소를 반환하는 코드**

  ```java
  IntSupplier fib = new IntSupplier() {
    private int previous = 0;
    private int current = 1;
    @Override
    public int getAsInt() {
      int oldPrevious = this.previous;
      int nextValue = this.previous + this.current;
      this.previous = this.current;
      this.current = nextValue;
      return oldPrevious;
    }
  };
  
  IntStream.generate(fib).limit(10).forEach(System.out::println);
  ```

  > fib 객체는 **가변(mutable)** 상태 객체다.

<br/>

# 5.9. 마치며

* **filter, distinct, takeWhile, dropWhile, skip, limit** 메서드로 스트림을 필터링하거나 자를 수 있다.
* 소스가 정렬되어 있다는 사실을 알고 있을 때 **takeWhile 과 dropWhile** 메서드를 효과적으로 사용할 수 있다.

- **map, flatMap** 메서드로 스트림의 요소를 추출하거나 변환할 수 있다.
- **findFirst, findAny** 메서드로 스트림의 요소를 검색할 수 있다. **allMatch, noneMatch, anyMatch** 메서드를 이용해서 주어진 프레디케이트와 일치하는 요소를 스트림에서 검색할 수 있다.
- 이들 메서드는 **쇼트 서킷(short circuit)** , 즉 결과를 찾는 즉시 반환하며, 전체 스트림을 처리하지 않는다.
- **reduce** 메서드로 스트림의 모든 요소를 반복 조합하며 값을 도출할 수 있다. 예를 들어 reduce로 스트림의 최댓값이나 모든 요소의 합계를 계산할 수 있다.
- **filter, map** 등은 상태를 저장하지 않는 **상태 없는 연산(stateless operation)** 이다. reduce 연산은 값을 계산하는데 필요한 상태를 저장한다. **sorted, distinct** 등의 메서드는 새로운 스트림을 반환하기에 앞서 스트림의 모든 요소를 버퍼에 저장해야 한다. 이런 메서드를 **상태 있는 연산(stateful operation)** 이라고 부른다.
- **IntStream, DoubleStream, LongStream** 은 기본형 특화 스트림이다. 이들 연산은 각각의 기본형에 맞게 특화되어 있다.
- 무한한 개수의 요소를 가진 스트림을 **무한 스트림** 이라 한다.