# 4. 스트림 소개

# 4.1. 스트림이란 무엇인가?

**스트림(Stream)**을 이용하면 선언형(즉, 데이터를 처리하는 임시 구현 코드 대신 질의로 표현할 수 있다.)으로 컬렉션 데이터를 처리할 수 있다. 또한 스트림을 이용하면 멀티스레드 코드를 구현하지 않아도 데이터를 **투명하게** 병렬로 처리할 수 있다.

* **예제) 저칼로리의 요리명을 반환하고, 칼로리를 기준으로 요리를 정렬하는 코드**

  자바 7 코드

  ```java
  // 누적자로 요소 필터링
  List<Dish> lowCaloricDishes = new ArrayList<>();
  for (Dish dish : menu) {
    if (dish.getCalories() < 400) {
      lowCaloricDishes.add(dish);
    }
  }
  
  // 익명 클래스로 요리 정렬
  Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
    public int compare(Dish dish1, Dish dish2) {
      return Integer.compare(dish1.getCalories(), dish2.getCalories());
    }
  });
  
  // 정렬된 리스트를 처리하면서 요리 이름 선택
  List<String> lowCaloricDishesName = new ArrayList<>();
  for (Dish dish : lowCaloricDishesName) {
    lowCaloricDishesName.add(dish.getName());
  }
  ```

  자바 8 코드

  ```java
  import static java.util.Comparator.comparing;
  import static java.util.stream.Collectors.toList;
  List<String> lowCaloricDishesName =
    menu.stream()
    		.filter(d -> d.getCalories() < 400)		// 400 칼로리 이하의 요리 선택
    		.sorted(comparing(Dish::getCalories))	// 칼로리로 요리 정렬
    		.map(Dish::getName)										// 요리명 추출
    		.collect(toList());										// 모든 요리명을 리스트에 저장
  ```

  stream() 을 **parallelStream()** 으로 바꾸면 위의 코드를 멀티코어 아키텍처에서 병렬로 실행할 수 있다.

  ```java
  List<String> lowCaloricDishesName =
    menu.parallelStream()
    		.filter(d -> d.getCalories() < 400)		// 400 칼로리 이하의 요리 선택
    		.sorted(comparing(Dish::getCalories))	// 칼로리로 요리 정렬
    		.map(Dish::getName)										// 요리명 추출
    		.collect(toList());										// 모든 요리명을 리스트에 저장
  ```

<br/>

filter, sorted, map, collect 같은 연산들은 **고수준 빌딩 블록(high-level building level)**으로 이루어져 있으므로 특정 스레딩 모델에 제한되지 않고 자유롭게 어떤 상황에서든 사용할 수 있다.

<br/>

자바 8의 스트림 API 특징

* **선언형** : 더 간결하고 가독성이 좋아진다.
* **조립할 수 있음** : 유연성이 좋아진다.
* **병렬화** : 성능이 좋아진다.

<br/>

앞으로 쓰일 예제 코드

* **Dish 클래스**

  ```java
  package chapter04;
  
  public class Dish {
  
    private final String name;
    private final boolean vegetarian;
    private final int calories;
    private final Type type;
  
    public Dish(String name, boolean vegetarian, int calories, Type type) {
      this.name = name;
      this.vegetarian = vegetarian;
      this.calories = calories;
      this.type = type;
    }
  
    public String getName() {
      return name;
    }
  
    public boolean isVegetarian() {
      return vegetarian;
    }
  
    public int getCalories() {
      return calories;
    }
  
    public Type getType() {
      return type;
    }
  
    @Override
    public String toString() {
      return name;
    }
  }
  ```

* **Main 클래스**

  ```java
  package chapter04;
  
  import java.util.Arrays;
  import java.util.List;
  
  public class Main {
  
    public static void main(String[] args) {
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
    }
  
  }
  ```

<br/>

# 4.2. 스트림 시작하기

스트림이란 **'데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소(Sequence of elements)'**로 정의할 수 있다.

* **연속된 요소** : 컬렉션과 마찬가지로 스트림은 특정 요소 형식으로 이루어진 연속된 값 집합의 인터페이스를 제공한다. 즉, 컬렉션의 주제는 데이터고 스트림의 주제는 계산이다.
* **소스** : 스트림은 컬렉션, 배열, I/O 자원 등의 데이터 제공 소스로부터 데이터를 소비한다. 즉, 리스트로 스트림을 만들면 스트림의 요소는 리스트의 요소와 같은 순서를 유지한다.
* **데이터 처리 연산** : 스트림은 함수형 프로그래밍 언어에서 일반적으로 지원하는 연산과 데이터베이스와 비슷한 연산을 지원한다.

<br/>

스트림의 두 가지 중요 특징

* **파이프라이닝(Pipelining)** : 대부분의 스트림 연산은 스트림 연산끼리 연결해서 커다란 파이프 라인을 구성할 수 있도록 스트림 자신을 반환한다. 그 덕분에 <u>게으름(laziness), 쇼트서킷(short-circuiting)</u> 같은 최적화도 얻을 수 있다.
* **내부 반복** : 반복자를 이용해서 명시적으로 반복하는 컬렉션과 달리 스트림은 내부 반복을 지원한다.

<br/>

* **예제**

  ```java
  import static java.util.stream.Collectors.toList;
  List<String> threeHighCaloricDishNames =
    menu.stream()															// 스트림 생성
    .filter(dish -> dish.getCalories() > 300)	// 고칼로리 요리 필터링
    .map(Dish::getName)												// 요리명 추출
    .limit(3)																	// 선착순 세 개만 선택
    .collect(toList());												// 결과를 다른 리스트로 저장
  System.out.println(threeHighCaloricDishNames);
  ```

  **실행 결과**

  ```
  [pork, beef, chicken]
  ```

  * **데이터 소스**는 요리 리스트(메뉴)
  * 데이터 소스는 **연속된 요소**를 스트림에 제공
  * 일련의 **데이터 처리 연산(filter, map, limit, collect)**을 적용
  * collect를 제외한 모든 연산은 서로 **파이프라인**을 형성할 수 있도록 스트림을 반환
  * collect를 호출하기 전까지는 menu에서 무엇도 선택되지 않는다.

<br/>

# 4.3. 스트림과 컬렉션

자바의 기존 컬렉션과 새로운 스트림 모두 연속된 요소 형식의 값을 저장하는 자료구조의 인터페이스를 제공한다.

데이터를 **언제** 계산하느냐가 컬렉션과 스트림의 가장 큰 차이다.

* **컬렉션** : 현재 자료구조가 포함하는 <u>모든</u> 값을 메모리에 저장하는 자료구조다. <u>'적극적 생성'</u>
* **스트림** : 이론적으로 <u>요청할 때만 요소를 계산하는</u> 고정된 자료구조다. 결과적으로 스트림은 생산자와 소비자 관계를 형성한다. 또한 스트림은 게으르게 만들어지는 컬렉션과 같다. <u>'게으른 생성'</u>

<br/>

## 4.3.1. 딱 한 번만 탐색할 수 있다

반복자와 마찬가지로 스트림도 한 번만 탐색할 수 있다. 즉, 탐색된 스트림의 요소는 소비된다.

```java
List<String> title = Arrays.asList("Java8", "In", "Action");
Stream<String> s = title.stream();

// title의 각 단어를 출력
s.forEach(System.out::println));

// java.lang.IllegalStateException: 스트림이 이미 소비되었거나 닫힘
s.forEach(System.out::println));
```

**스트림은 단 한번만 소비할 수 있다!!**

<br/>

## 4.3.2. 외부 반복과 내부 반복

**외부 반복** : 컬렉션 인터페이스를 사용하려면 사용자가 직접 요소를 반복해야 한다.

**내부 반복** : 스트림 라이브러리가 사용하는 방법이며, 이는 반복을 알아서 처리하고 결과 스트림값을 어딘가에 저장해준다.

<br/>

**예제**

* 컬렉션: for-each 루프를 이용하는 외부 반복

  ```java
  List<String> names = new ArrayList<>();
  for(Dish dish : menu) {				// 메뉴 리스트를 명시적으로 순차 반복한다.
    names.add(dish.getName());	// 이름을 추출해서 리스트에 추가한다.
  }
  ```

* 컬렉션: 내부적으로 숨겨졌던 반복자를 사용한 외부 반복

  ```java
  List<String> names = new ArrayList<>();
  Iterator<String> iterator = menu.iterator();
  while(iterator.hasNext()) {			// 명시적 반복
    Dish dish = iterator.next();
    names.add(dish.getName());
  }
  ```

* 스트림 내부 반복

  ```java
  List<String> names = menu.stream()
    											 .map(Dish::getName)	// map 메서드 사용
    											 .collect(toList());	// 파이프라인 실행
  ```

<br/>

# 4.4. 스트림 연산

스트림 인터페이스의 연산을 크게 두 가지로 구분할 수 있다.

* **이전에 등장했던 예제**

  ```java
  List<String> names = menu.stream()	// 요리 리스트에서 스트림 얻기
    									     .filter(dish -> dish.getCalories() > 300)	// 중간 연산
    									     .map(Dish::getName)	// 중간 연산
    									     .limit(3)						// 중간 연산
    									     .collect(toList());	// 스트림을 리스트로 변환
  ```

  * filter, map, limit는 서로 연결되어 파이프라인 형성
  * collect로 파이프라인을 실행한 다음에 닫는다.

<br/>

**중간 연산(intermediate operation)** : 연결할 수 있는 스트림 연산

**최종 연산(terminal operation)** : 스트림을 닫는 연산

<br/>

## 4.4.1. 중간 연산

