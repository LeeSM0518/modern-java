# 6. 스트림으로 데이터 수집

스트림은 중간 연산과 최종 연산으로 구분할 수 있다. 중간 연산은 스트림 파이프라인을 구성하며, 스트림의 요소를 **소비(consum)하지** 않는다. 반면 최종 연산은 스트림의 요소를 소비해서 최종 결과를 도출한다.

스트림의 최종 연산 **collect는 다양한 요소 누적 방식을 인수로 받아서 스트림을 최종 결과로 도출하는** 리듀싱 연산을 수행할 수 있다.

다양한 요소 누적 방식은 Collector 인터페이스로 정의되어 있다.

<br>

* **collect와 컬렉터로 구현할 수 있는 예제**

  * 통화별로 트랜잭션을 그룹화한 다음에 해당 통화로 일어난 모든 트랜잭션 합계를 계산하시오(Map\<Currency, Integer> 반환).
  * 트랜잭션을 비싼 트랜잭션과 저렴한 트랜잭션 두 그룹으로 분류하시오(Map\<Boolean, List\<Transaction>> 반환).
  * 트랜잭션을 도시 등 다수준으로 그룹화하시오. 그리고 각 트랜잭션이 비싼지 저렴한지 구분하시오(Map\<String, Map\<Boolean, List\<Transaction>>> 반환).

* **코드**

  * 통화별로 트랜잭션을 그룹화한 코드

    **스트림 사용X**

    ```java
    // 그룹화한 트랜잭션을 저장할 맵을 생성
    Map<Currency, List<Transaction>> transactionsByCurrencies 
    	= new HashMap<>();
    
    // 트랜잭션 리스트를 반복한다.
    for (Transaction transaction : transactions) {
      Currency currency = transaction.getCurrency();
      List<Transaction> transactionForCurrency =
        transactionsByCurrencies.get(currency);
      // 현재 통화를 그룹화하는 맵에 항목이 없으면 항목을 만든다.
      if (transactionsForCurrency == null) {
        transactionsForCurrency = new ArrayList<>();
        transactionsByCurrency.put(currency, transactionsForCurrency);
      }
      // 같은 통화를 가진 트랜잭션 리스트에 현재 탐색 중인 트랜잭션을 추가한다.
      transactionsForCurrency.add(transaction);
    }
    ```

    **스트림 사용O**

    ```java
    Map<Currency, List<Transaction>> transactionsByCurrencies =
      transactions.stream().collect(groupingBy(Transaxtion::getCurrency));
    ```

    > 스트림을 사용했을 때, 훨씬 코드가 간결해진 것을 확인 할 수 있다.

<br>

# 6.1. 컬렉터란 무엇인가?

함수형 프로그래밍에서는 **'무엇'을 원하는지 직접 명시할 수 있어서** 어떤 방법으로 이를 얻을지는 신경 쓸 필요가 없다. 이전 예제에서 collect 메서드로 Collector 인터페이스 구현을 전달했다. **Collector 인터페이스** 구현은 스트림의 요소를 어떤 식으로 도출할지 지정한다. 

그리고, 함수형 프로그래밍을 사용하면 필요한 컬렉터를 쉽게 추가할 수 있다.

<br>

## 6.1.1. 고급 리듀싱 기능을 수행하는 컬렉터

함수형 API의 장점은 **높은 수준의 조합성과 재사용성을** 꼽을 수 있다.

**collect로** 결과를 수집하는 과정을 **간단하면서도 유연한 방식으로** 정의할 수 있다.

스트림에서 collect를 호출하면 내부적으로 **리듀싱 연산이** 실행된다. 즉, collect에서는 리듀싱 연산을 이용해서 스트림의 각 요소를 방문하면서 컬렉터가 작업을 처리한다.

**Collector 인터페이스의 메서드를 어떻게 구현하느냐에 따라** 스트림에 어떤 리듀싱 연산을 수행할지 결정된다.

<br>

## 6.1.2. 미리 정의된 컬렉터

Collectors에서 제공하는 메서드의 기능은 크게 세 가지로 구분할 수 있다.

* **스트림 요소를 하나의 값으로 리듀스하고 요약**
* **요소 그룹화**
* **요소 분할**

<br>

# 6.2. 리듀싱과 요약

**컬렉터(Stream.collect 메서드의 인수)로** 스트림의 항목을 컬렉션으로 재구성 할 수 있다.

<br>

## 6.2.1. 스트림값에서 최댓값과 최솟값 검색

**Collectors.maxBy, Collectors.minBy** 두 개의 메서드를 이용해서 스트림의 최댓값과 최솟값을 계산할 수 있다. 두 컬렉터는 스트림의 요소를 비교하는 데 사용할 **Comparator를 인수로 받는다.** 

* **칼로리로 요리를 비교하는 Comparator를 구현한 다음 Collectors.maxBy로 전달하는 코드**

  ```java
  Comparator<Dish> dishCaloriesComparator =
    Comparator.comparingInt(Dish::getCalories);
  Optional<Dish> mostCalorieDish =
    menu.stream()
        .collect(maxBy(dishCaloriesComparator));
  ```

<br>

스트림에 있는 객체의 숫자 필드의 합계나 평균 등을 반환하는 연산에도 리듀싱 연산이 자주 사용된다. 이러한 연산을 **요약(summarization)연산** 이라 부른다.

<br>

## 6.2.2. 요약 연산

Collectors 클래스는 **Collectors.summingInt** 라는 특별한 요약 팩토리 메서드를 제공한다.

**summingInt는** 객체를 int로 매핑하는 함수를 인수로 받는다.

summingInt의 인수로 전달된 함수는 **객체를 int로 매핑한 컬렉터를 반환한다.**

**Collectors.summingLong** 과 **Collectors.summingDouble** 메서드는 같은 방식으로 동작하며 **각각 long 또는 double** 형식의 데이터로 요약한다는 점만 다르다.

<br>

**Collectors.averagingInt, averagingLong, averagingDouble** 등으로 다양한 형식으로 이루어진 숫자 빕합의 평균을 계산할 수 있다.

* **코드**

  ```java
  double avgCalories =
    menu.stream().collect(averagingInt(Dish::getCalories));
  ```

<br>

**Collectors.summarizingInt** 를 사용하면 한 번에 요소 수, 합계, 평균, 최댓값, 최솟값 등을 계산할 수 있다.

* **코드**

  ```java
  IntSummaryStatistics menuStatistics =
    menu.stream().collect(summarizingInt(Dish::getCalories));
  ```

  **객체 출력 결과**

  ```
  IntSummaryStatistics{count=9, sum=4300, min=120, average=477.777778, max=800}
  ```

  * 위 메소드는 int 뿐 아니라 long 이나 double에 대응하는 **summarizingLong, summarizingDouble** 메서드와 관련된 **LongSummaryStatistics, DoubleSummaryStatistics** 클래스도 있다.

<br>

## 6.2.3. 문자열 연결

컬렉터에 **joining** 팩토리 메서드를 이용하면 스트림의 각 객체에 **toString 메서드를 호출해서 추출한 모든 문자열을 하나의 문자열로 연결해서 반환한다.**

* **예시) 모든 요리명 연결 코드**

  ```java
  String shortMenu = menu.stream().map(Dish::getName).collect(joining());
  ```

* **예시) 연결될 두 요소 사이에 문자 추가 코드**

  ```java
  String shortMenu = menu.stream().map(Dish::getName).collect(joining(", "));
  ```

<br>

## 6.2.4. 범용 리듀싱 요약 연산

* **예시) reducing 메서드로 모든 메뉴의 칼로리 계산**

  ```java
  int totalCalories = menu.stream().collect(reducing(0, 
                                                    Dish::getCalories, (i, j) -> i + j));
  ```

  * reducing은 **인수 세 개를 받는다.** 
    * **첫 번째 인수:** 리듀싱 연산의 시작값이거나 스트림에 인수가 없을 때는 반환값이다.
    * **두 번째 인수:** 요리를 칼로리 정수로 변환할 때 사용한 변환 함수
    * **세 번째 인수:** 두 항목을 하나의 값으로 더하는 BinaryOperator다.

<br>

* **예시) 한 개의 인수를 가진 reducing 버전을 이용해서 가장 칼로리가 높은 요리 찾기**

  ```java
  Optional<Dish> mostCalorieDish =
    menu.stream().collect(reducing(
      (d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
  ```

  * 한 개의 인수를 갖는 reducing은 빈 스트림이 넘겨졌을 때 시작값이 설정되지 않았기 때문에, 빈 객체를 저장한 **Optional\<T> 객체를 반환한다.**

<br>

* **예시) collect의 toList 대신 reduce를 사용**

  ```java
  Stream<Integer> stream = Arrays.asList(1, 2, 3, 4, 5, 6).stream();
  List<Integer> numbers = stream.reduce(
                                 new ArrayList<Integer>(),
                                 (List<Integer> l, Integer e) -> {
                                   l.add(e);
                                   return l; },
                                 (List<Integer> l1, List<Integer> l2) -> {
                                   l1.addAll(l2);
                                   return l1;})
  ```

  * 위와 같이 매번 새로운 리스트를 할당해야 하고 따라서 객체를 할당하느라 성능이 저하된다. 즉, 가변 컨테이너 관련 작업이면서 병렬성을 확보하려면 collect 메서드로 리듀싱 연산을 구현하는 것이 바람직하다.

<br>

### 컬렉션 프레임워크 유연성 : 같은 연산도 다양한 방식으로 수행할 수 있다.

reducing 컬렉터를 사용한 이전 예제에서 람다 표현식 대신 Integer 클래스의 sum 메서드 참조를 이용하면 코드를 좀 더 단순화할 수 있다.

* **예시) 칼로리 합계 연산**

  ```java
  int totalCalories = menu.stream().collect(reducing(0,                 // 초기값
                                                    Dish::getCalories,  // 변환 함수
                                                    Integer::sum));     // 합계 함수
  ```

  * 누적자를 초깃값으로 초기화하고, 합계 함수를 이용해서 각 요소에 변환 함수를 적용한 결과 숫자를 반복적으로 조합한다.

* **counting 컬렉터도 세 개의 인수를 갖는 reducing 팩토리 메서드를 이용해서 구현할 수 있다.** 즉, 다음 코드처럼 스트림의 Long 객체 형식의 요소를 1로 변환한 다음에 모두 더할 수 있다.

  ```java
  public static <T> Collector<T, ?, Long> counting() {
    return reducing(0L, e -> 1L, Long::sum);
  }
  ```

<br>

**제네릭 와일드카드 '?' 사용법**

?는 컬렉터의 누적자 형식이 알려지지 않았음을, 즉 누적자의 형식이 자유로움을 의미한다. 위 예제에서는 Collectors 클래스에서 원래 정의된 메서드 시그니처를 그대로 사용했을 뿐이다.

<br>

* **예시) 컬렉터를 사용하지 않고 합계를 구한 코드**

  ```java
  int totalCalories =
    menu.stream().map(Dish::getCalories).reduce(Integer::sum).get
  ```

  * 위 예시에서는 요리 스트림이 비어있지 않다는 사실을 알고 있으므로 **get을** 자유롭게 썼지만, Optional의 값을 얻어올 때는 **orElse, orElseGet** 등을 이용해서 얻어오는 것이 좋다.

<br>

* **예시) IntStream으로 매핑한 다음 sum 호출**

  ```java
  int totalCalories =
    menu.stream().mapToInt(Dish::getCalories).sum();
  ```

<br>

### 자신의 상황에 맞는 최적의 해법 선택

문제를 해결할 수 있는 다양한 해결 방법을 확인한 다음에 가장 일반적으로 문제에 특화된 해결책을 고르는 것이 바람직하다. 예를 들어 메뉴의 전체 칼로리를 계산하는 예제에서는 가장 마지막에 확인한 해결 방법이 **가독성이 가장 좋고 간결하다.** 또한 IntStream 덕분에 **자동 언박싱** 연산을 수행하거나 Integer를 int로 변환하는 과정을 피할 수 있으므로 **성능까지 좋다.**

<br>

# 6.3. 그룹화

자바 8의 함수형을 이용하면 가독성 있는 한 줄의 코드로 그룹화를 구현할 수 있다.

* **예시) 요리의 메뉴를 그룹화하는 코드**

  ```java
  Map<Type, List<Dish>> dishedByType =
    menu.stream().collect(groupingBy(Dish::getType));
  ```

  * 스트림의 각 요리에서 Type과 일치하는 모든 요리를 추출하는 함수를 groupingBy 메서드로 전달한다. 이 함수를 기준으로 스트림이 그룹화되므로 이를 **분류 함수(classification function)라고** 부른다.
  * 그룹화 연산의 결과로 **그룹화 함수가 반환하는 키** 그리고 **각 키에 대응하는 스트림의 모든 항목 리스트를 값으로** 갖는 맵이 반환된다.

<br>

* **예시) 더 복잡한 분류 기준이 필요한 상황에서의 코드**

  0 ~ 400 = 'diet', 400~700 = 'normal', 700 ~ = 'fat' 으로 분류

  ```java
  public enum CaloricLevel { DIET, NORMAL, FAT }
  
  Map<CaloricLevel, List<Dish>> dishesByCaloricLevel =
    menu.stream().collect(
    groupingBy(dish -> {
      if (dish.getCalories() <= 400) return CaloricLevel.DIET;
      else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
      else return CaloricLevel.FAT;
    }));
  ```

<br>

## 6.3.1. 그룹화된 요소 조작

