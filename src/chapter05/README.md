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

