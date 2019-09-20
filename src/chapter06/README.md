# 6. 스트림으로 데이터 수집

스트림은 중간 연산과 최종 연산으로 구분할 수 있다. **중간 연산은 스트림 파이프라인을 구성하며, 스트림의 요소를 소비(consum)하지 않는다. 반면 최종 연산은 스트림의 요소를 소비해서 최종 결과를 도출한다.**

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
  int totalCalories = menu.stream().collect(reducing(0,             // 초기값
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

요소를 그룹화한 다음에 각 결과 그룹의 요소를 조작하는 연산이 필요하다.

* **예시) 500 칼로리가 넘는 요리를 필터**

  ```java
  Map<Type, List<Dish>> caloricDishesByType =
    menu.stream().filter(dish -> dish.getCalories() > 500)
    .collect(groupingBy(Dish::getType));
  ```

  실행 결과

  ```
  {OTHER=[french fries, pizza], MEAT=[pork, beef]}
  ```

  <br>

위처럼 코드를 작성하면 필터에 만족하는 요리만 맵으로 만들어져서 다른 종류의 요리들은 키 자체가 저장이 되지 않는다. 하지만  **groupingBy 팩토리 메서드를** 오버로드해 이 문제를 해결할 수 있다.

<br>

* **예시) groupingBy 오버로드**

  ```java
  Map<Type, List<Dish>> caloricDishesByType2 =
    menu.stream()
    .collect(groupingBy(Dish::getType,
                        filtering(dish -> dish.getCalories() > 500, toList())));
  ```

  실행 결과

  ```
  {FISH=[], OTHER=[french fries, pizza], MEAT=[pork, beef]}
  ```

  * **filtering 메소드는** Collectors 클래스의 또 다른 정적 팩토리 메서드로 프레디케이트를 인수로 받는다. 이 프레디케이트로 **각 그룹의 요소와 필터링 된 요소를 재그룹화 한다.**

<br>

그룹화된 항목을 조작하는 다른 유용한 기능 중 또 **다른 하나로 맵핑 함수를 이용해 요소를 변환하는 작업이 있다.** **mapping 메서드를** 사용하면 그룹의 각 요리를 관련 이름 목록으로 변환할 수 있다.

<br>

* **예시) mapping 메서드를 이용**

  ```java
  Map<Type, List<String>> dishNamesByType =
    menu.stream()
    .collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));
  ```

  실행 결과

  ```
  {FISH=[prawns, salmon], OTHER=[french fries, rice, season, pizza], MEAT=[pork, beef, chicken]}
  ```

  * 맵의 각 그룹은 요리가 아니라 문자열 리스트다.

<br>

groupingBy와 연계해 세 번째 컬렉터를 사용해서 일반 맵이 아닌 **flatMap 변환을** 수행할 수 있다. 다음처럼 태그 목록을 가진 각 요리로 구성된 맵이 있다고 가정하자.

```java
{FISH=salmon, OTHER=pizza, MEAT=pork}Map<String, List<String>> dishTags = new HashMap<>();
dishTags.put("pork", asList("greasy,", "salty"));
dishTags.put("beef", asList("salty", "roasted"));
dishTags.put("chicken", asList("fried", "crisp"));
dishTags.put("french fries", asList("greasy", "fried"));
dishTags.put("rice", asList("light", "natural"));
dishTags.put("season fruit", asList("fresh", "natural"));
dishTags.put("pizza", asList("tasty", "salty"));
dishTags.put("prawns", asList("tasty", "roasted"));
dishTags.put("salmon", asList("delicious", "fresh"));
```

<br>

- **예제) flatMapping 컬렉터를 이용하면 각 형식의 요리의 태그를 간편하게 추출할 수 있다.**

  ```java
  Map<Type, Set<String>> dishNamesByType2 =
    menu.stream()
    .collect(groupingBy(Dish::getType,
                        flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));
  ```

  실행 결과

  ```
  {FISH=[roasted, tasty, fresh, delicious], OTHER=[salty, greasy, natural, light, tasty, fresh, fried], MEAT=[salty, greasy,, roasted, fried, crisp]}
  ```

  - 두 수준의 리스트를 한 수준으로 평면화하려면 **flatMap을** 수행해야 한다.
  - 각 그룹에 수행한 flatMapping 연산 결과를 수집해서 리스트가 아니라 **집합으로 그룹화해 중복 태그를 제거한다.**

<br>

## 6.3.2. 다수준 그룹화

두 인수를 받는 팩토리 메서드 **Collectors.groupingBy** 를 이용해서 항목을 다수준으로 그룹화할 수 있다.

**Collectors.groupingBy는** 일반적인 분류 함수와 컬렉터를 인수로 받는다. 즉, 바깥쪽 groupingBy 메서드에 스트림의 항목을 분류할 두 번재 기준을 정의하는 **내부 groupingBy를 전달해서 두 수준으로 스트림의 항목을 그룹화할 수 있다.**

<br>

- **예제) 다수준 그룹화**

  ```java
  Map<Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel =
    menu.stream().collect(
    groupingBy(Dish::getType,
               groupingBy(dish -> {
                 if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                 else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                 else return CaloricLevel.FAT;
               })
         )
  );
  ```

  출력 결과

  ```
  {FISH={DIET=[prawns], NORMAL=[salmon]}, 
    OTHER={DIET=[rice, season], NORMAL=[french fries, pizza]},  
    MEAT={DIET=[chicken], FAT=[pork], NORMAL=[beef]}}
  ```

  - 두 수준의 맵은 첫 번째 키와 두 번째 키의 기준에 부합하는 요소 리스트를 값(salmon, pizza 등)으로 갖는다.
  - 다수준 그룹화 연산은 다양한 수준으로 확장할 수 있다.
  - **n 수준 그룹화의 결과는 n 수준 맵이 된다.**

<br>

## 6.3.3. 서브그룹으로 데이터 수집

이전 예제에서는 groupingBy 컬렉터를 외부 컬렉터로 전달해서 요리의 수를 종류별로 계산했다. 

다수준 그룹화 연산을 구현하는 방식은 두 번째 인수로 counting 컬렉터를 전달해서 메뉴에서 요리의 수를 종류별로 계산할 수 있다.

- **예제) counting 컬렉터 사용**

  ```java
  Map<Type, Long> typesCount = menu.stream().collect(
    groupingBy(Dish::getType, counting()));
  ```

  출력 결과

  ```
  {FISH=2, OTHER=4, MEAT=3}
  ```

<br>

분류 함수 한 개의 인수를 갖는 **groupingBy(f)는 사실 groupingBy(f, toList())의 축약형이다.**

<br>

- **예제) 요리의 종류를 분리하는 컬렉터로 메뉴에서 가장 높은 칼로리를 가진 요리 찾기**

  ```java
  Map<Type, Optional<Dish>> mostCaloricByType =
    menu.stream()
    .collect(groupingBy(Dish::getType,
                        maxBy(comparing(Dish::getCalories))));
  ```

  - 그룹화의 결과로 **요리의 종류를 키로, Optional\<Dish>를 값으로** 갖는 맵이 반환

<br>

### 컬렉터 결과를 다른 형식에 적용하기

마지막 그룹화 연산에서 맵의 모든 값을 Optional로 감쌀 필요가 없으므로 Optional을 삭제할 수 있다.

즉, 다음처럼 팩토리 메서드 **Collectors.collectingAndThen** 으로 컬렉터가 반환한 결과를 다른 형식으로 활용할 수 잇다.

- **예제) 각 서브그룹에서 가장 칼로리가 높은 요리 찾기**

  ```java
  Map<Type, Dish> mostCaloricByType2 =
    menu.stream()
    .collect(groupingBy(Dish::getType,			// 분류 함수
                        collectingAndThen(	// 감싸인 컬렉터
                          maxBy(comparing(Dish::getCalories)),
                          Optional::get)));	// 변환 함수
  
  ```

  출력 결과

  ```
  {FISH=salmon, OTHER=pizza, MEAT=pork}
  ```

  * collectingAndThen은 **적용할 컬렉터와 변환 함수를** 인수로 받아 다른 컬렉터를 변환한다.

<br>

### groupingBy와 함께 사용하는 다른 컬렉터 예제

일반적으로 스트림에서 같은 그룹으로 분류된 모든 요소에 리듀싱 작업을 수행할 때는 팩토리 메서드 groupingBy에 두 번째 인수로 전달한 컬렉터를 사용한다.

- **예제) 메뉴에 있는 모든 요리의 칼로리 합계**

  ```java
  Map<Type, Integer> totalCaloriesByType =
    menu.stream().collect(groupingBy(Dish::getType,
                                     summingInt(Dish::getCalories)));
  ```

  출력 결과

  ```
  {FISH=750, OTHER=1550, MEAT=1900}
  ```

<br>

mapping 메서드는 **스트림의 인수를 변환하는 함수** 와 **변환 함수의 결과 객체를 누적하는 컬렉터를** 인수로 받는다.

mapping은 입력 요소를 누적하기 전에 매핑 함수를 적용해서 **다양한 형식의 객체를 주어진 형식의 컬렉터에 맞게 변환하는 역할을 한다.**

- **예제) 각 요리 형식에 존재하는 모든 CaloricLevel 구하기**

  ```java
  Map<Type, Set<CaloricLevel>> caloricLevelsByType =
    menu.stream().collect(
    groupingBy(Dish::getType, mapping(dish -> {
      if (dish.getCalories() <= 400) return CaloricLevel.DIET;
      else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
      else return CaloricLevel.FAT; }, toSet())));
  ```

  출력 결과

  ```
  {FISH=[DIET, NORMAL], OTHER=[DIET, NORMAL], MEAT=[FAT, DIET, NORMAL]}
  ```

  - mapping 메서드에 전달한 변환 함수는 Dish를 CaloricLevel로 매핑한다.
  - 그룹화 함수로 생성된 서브스트림에 mapping 함수를 적용한다.

<br>

이때 **toCollection을** 이용하면 원하는 방식으로 결과를 제어할 수 있다.

- **예제) toCollection 사용**

  ```java
  Map<Type, Set<CaloricLevel>> caloricLevelsByType2 =
    menu.stream().collect(
    groupingBy(Dish::getType, mapping(dish -> {
      if (dish.getCalories() <= 400) return CaloricLevel.DIET;
      else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
      else return CaloricLevel.FAT;
    }, toCollection(HashSet::new))));
  ```

  - 이전 코드와 결과는 똑같으나 이전에는 **Set의 형식이 정해져 있지 않았다.** 이때 toCollection을 이용하면 **원하는 타입으로 결과를 제어할 수 있다.**

<br>

# 6.4. 분할

분할은 **분할 함수(partitioning function)라** 불리는 프레디케이트를 분류 함수로 사용하는 특수한 그룹화 기능이다. **분할 함수는 불리언을 반환하므로 맵의 키 형식은 Boolean이다.** 결과적으로 그룹화 맵은 최대 (true or false) 두 개의 그룹으로 분리된다.

- **예제) 채식 요리 분류**

  ```java
  Map<Boolean, List<Dish>> partitionedMenu =
    menu.stream().collect(partitioningBy(Dish::isVegetarian));
  ```

  출력 결과

  ```
  {false=[pork, beef, chicken, prawns, salmon], 
    true=[french fries, rice, season, pizza]}
  ```

  - **partitioningBy** 라는 분할 함수를 사용하였다.

<br>

## 6.4.1. 분할의 장점

분할 함수가 반환하는 참, 거짓 두 가지 요소의 스트림 리스트를 모두 유지한다는 것이 분할의 장점이다.

- **예제) 채식이 아닌 모든 요리 리스트 구하기**

  ```java
  Map<Boolean, Map<Type, List<Dish>>> vegetarianDishesByType =
    menu.stream().collect(
    partitioningBy(Dish::isVegetarian,						// 분할 함수
                   groupingBy(Dish::getType)));		// 두 번째 컬렉터
  ```

  출력 결과

  ```
  {false={FISH=[prawns, salmon], MEAT=[pork, beef, chicken]}, 
    true={OTHER=[french fries, rice, season, pizza]}}
  ```

  - 컬렉터를 두 번째 인수로 전달할 수 있는 오버로드 된 버전의 partitioningBy 메서드이다.

<br>

- **예제) 채식 요리와 채식이 아닌 요리 각각의 그룹에서 가장 칼로리가 높은 요리**

  ```java
  Map<Boolean, Dish> mostCaloricPartitionedByVegetarian =
    menu.stream().collect(
    partitioningBy(Dish::isVegetarian,
                   collectingAndThen(maxBy(comparingInt(Dish::getCalories)),
                                     Optional::get)));
  ```

  출력 결과

  ```
  {false=pork, true=pizza}
  ```

<br>

partitioningBy가 반환한 맵 구현은 참과 거짓 두 가지 키만 포함하므로 더 **간결하고 효과적이다.**

<br>

## 6.4.2. 숫자를 소수와 비소수로 분할하기

정수 n을 인수로 받아서 2에서 n까지의 자연수를 **소수(prime) 와 비소수(nonprime)** 로 나누는 프로그램을 구현해보자.

<br>

1. 소수인지 아닌지 판단하는 프레디케이트 구현

   ```java
   public boolean isPrime(int candidate) {
     return IntStream.range(2, candidate)
       .noneMatch(i -> candidate % i == 0);
   }
   ```

2. 소수의 대상을 주어진 수의 제곱근 이하의 수로 제한

   ```java
   public boolean isPrime(int candidate) {
     int candidateRoot = (int) Math.sqrt((double)candidate);
     return IntStream.range(2, candidateRoot)
       .noneMatch(i -> candidate % i == 0);
   }
   ```

3. n개의 숫자를 포함하는 스트림을 만들고 **isPrime 메서드를 프레디케이트로** 이용하여 **partitioningBy 컬렉터로 리듀스해서 소수와 비소수를 분류한다.**

   ```java
   public Map<Boolean, List<Integer>> partitionPrimes(int n) {
     return IntStream.rangeClosed(2, n).boxed()
       .collect(partitioningBy(this::isPrime));
   }
   ```

<br>

### Collectors 클래스의 정적 팩토리 메서드

| 팩토리 메서드     | 반환 형식               | 사용 예제                                                    | 활용예                                                       |
| ----------------- | ----------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| toList            | List\<T>                | 스트림의 모든 항목을 리스트로 수집                           | List\<Dish> dishes = menuStream.collect(toList());           |
| toSet             | Set\<T>                 | 중복이 없는 집합으로 수집                                    | Set\<Dish> dishes = menuStream.collect(toSet());             |
| toCollection      | Collection\<T>          | 발행자가 제공하는 컬렉션으로 수집                            | Collection\<Dish> dishes = menuStream.collect(toCollection(), ArrayList::new); |
| counting          | Long                    | 스트림의 항목 수 계산                                        | long howManyDishes = meunStream.collect(counting());         |
| summingInt        | Integer                 | 스트림의 항목에서 정수 프로퍼티값을 더함                     | int totalCalories = menuStream.collect(<br>summingInt(Dish::getCalories)); |
| averagingInt      | Double                  | 정수 프로퍼티의 평균값 계산                                  | double avgCalories = menuStream.collect(<br>averagingInt(Dish::getCalories)); |
| summarizingInt    | IntSummaryStatistics    | 최대값, 최솟값, 합계, 평균 등의 정보 통계 수집               | IntSummaryStatistics menuStatistics =<br>menuStream.collect(<br>summarizingInt(Dish::getCalories)); |
| joining           | String                  | 각 항목에 toString 메서드를 호출한 결과를 연결               | String shortMenu = menuStream.map(Dish::getName)<br>.collect(joining(", ")); |
| maxBy             | Optional\<T>            | 주어진 비교자를 이용해서 최댓값 요소를 Optional로 반환       | Optional\<Dish> fattest = menuStream.collect(<br>maxBy(comparingInt(Dish::getCalories))); |
| minBy             | Optional\<T>            | 주어진 비교자를 이용해서 최솟값 요소를 Optinal로 반환        | Optional\<Dish> lightest = menuStream.collect(<br>minBy(comparing(Dish::getCalories))); |
| reducing          | 리듀스 연산의 타입      | 누적자를 초깃값으로 설정한 다음<br>각 요소를 반복적으로 누적자와 합쳐<br>스트림을 하나의 값으로 리듀싱 | int totalCalories = menuStream<br>.collect(reducing(0, Dish::getCalories, <br>Integer::sum)); |
| collectingAndThen | 변환 함수의 반환 타입   | 다른 컬렉터를 감싸고 그 결과에 변환 함수 적용                | int howManyDishes = <br>menuStream.collect(<br>collectingAndThen(toList(), List::size)); |
| groupingBy        | Map\<K, List\<T>>       | 하나의 프로퍼티값을 기준으로 스트림의 항목을 그룹화 하며<br>기준 프로퍼티 값을 결과 맵의 키로 사용 | Map\<Type, List\<Dish>> dishesByType =<br>menuStream.collect(groupingBy(<br>Dish::getType)); |
| partitioningBy    | Map\<Boolean, List\<T>> | 프레디케이트를 스트림의 각 항목에<br>적용한 결과로 항목 분할 | Map\<Boolean, List\<Dish>> vegetarianDishes =<br>menuStream.collect(<br>partitioningBy(Dish::isVegetarian)); |

<br>

# 6.5. Collector 인터페이스

Collector 인터페이스는 리듀싱 연산(컬렉터)을 어떻게 구현할지 제공하는 메서드 집합으로 구성된다.

Collector 인터페이스를 직접 구현해서 더 효율적으로 문제를 해결하는 컬렉터를 만드는 방법을 살펴보자.

<br>

**Collector 인터페이스의 시그니처와 다섯 개의 메서드 정의**

```java
public interface Collector<T, A, R> {
  Supplier<A> supplier();
  BiConsumer<A, T> accumulator();
  Function<A, R> finisher();
  BinaryOperator<A> combiner();
  Set<Characteristics> characteristics();
}
```

- **T** : 수집될 스트림 항목의 제네릭 형식이다.
- **A** : 누적자, 즉 수집 과정에서 중간 결과를 누적하는 객체의 형식이다.
- **R** : 수집 연산 결과 객체의 형식이다.

<br>

예를 들어 Stream\<T>의 모든 요소를 List\<T>로 수집하는 ToListCollector\<T> 라는 클래스를 구현할 수 있다.

```java
public class ToListCollector<T> implements Collector<T, List<T>, List<T>>
```

<br>

## 6.5.1. Collector 인터페이스의 메서드 살펴보기

### supplier 메서드: 새로운 결과 컨테이너 만들기

supplier 메서드는 빈 결과로 이루어진 Supplier를 반환해야 한다. 즉, <u>supplier는 수집 과정에서 빈 누적자 인스턴스를 만드는 파라미터가 없는 함수다.</u>

- **ToListCollector에서 supplier**

  ```java
  public Supplier<List<T>> supplier() {
    return () -> new ArrayList<>();
  }
  ```

  > 생성자 참조를 전달하는 방법도 있다.

  ```java
  public Supplier<List<T>> supplier() {
    return ArrayList::new;
  }
  ```

<br>

### accumulator 메서드 : 결과 컨테이너에 요소 추가하기

accumulator 메서드는 <u>리듀싱 연산을 수행하는 함수를 반환한다.</u> 즉 누적자(스트림의 첫 n-1개 항목을 수집한 상태)와 n번째 요소를 함수에 적용한다.

- **ToListCollector에서 accumulator**

  ```java
  public BiConsumer<List<T>, T> accumulator() {
    return (list, item) -> list.add(item);
  }
  ```

  > 생성자 참조를 전달하는 방법도 있다.

  ```java
  public BiConsumer<List<T>, T> accumulator() {
    return List::add;
  }
  ```

<br>

### finisher 메서드 : 최종 변환값을 결과 컨테이너로 적용하기

finisher 메서드는 스트림 탐색을 끝내고 <u>누적자 객체를 최종 결과로 변환하면서 누적 과정을 끝낼 때 호출할 함수를 반환해야 한다.</u> 때로는 누적자 객체가 이미 최종 결과인 상황도 있다. 이런 때는 변환 과정이 필요하지 않으므로 finisher 메서드는 항등 함수를 반환한다.

- **ToListCollector에서 finisher**

  ```java
  public Function<List<T>, List<T>> finisher() {
    return Function.identity();	// 항등 함수
  }
  ```

<br>

### 순차 리듀싱 과정의 논리적 순서

<img src="http://cfile7.uf.tistory.com/image/22053A3C588D90420C1209">

<br>

### combiner 메서드 : 두 결과 컨테이너 병합

combiner는 스트림의 서로 다른 서브파트를 병렬로 처리할 때 누적자가 이 결과를 어떻게 처리할지 정의한다. 즉, 스트림의 두 번째 서브파트에서 수집한 항목 리스트를 첫 번째 서브파트 결과 리스트의 뒤에 추가하면 된다.

```java
public BinaryOperator<List<T>> combiner() {
  return (list1, list2) -> {
    list1.addAll(list2);
    return list1;
  };
}
```

- **병렬화 리듀싱 과정에서 combiner 메서드 활용**

  <img src="../capture/스크린샷 2019-09-19 오후 11.38.03.png">

  1. 스트림은 분할해야 하는지 정의하는 조건이 거짓으로 바뀌기 전까지 원래 **스트림을 재귀적으로 분할한다.**
  2. 모든 서브스트림(substream)의 각 요소에 **리듀싱 연산을 순차적으로 적용해서 서브스트림을 병렬로 처리할 수 있다.**
  3. 마지막에는 컬렉터의 combiner 메서드가 반환하는 함수로 모든 부분결과를 쌍으로 합친다. 즉, **분할된 모든 서브스트림의 결과를 합치면서 연산이 완료된다.**

<br>

combiner 메서드를 이용하면 **스트림의 리듀싱을 병렬로 수행할 수 있다.**

<br>

### Characteristics 메서드

characteristics 메서드는 컬렉터의 연산을 정의하는 Characteristics 형식의 불변 집합을 반환한다. **Characteristics는 스트림을 병렬로 리듀스할 것인지 그리고 병렬로 리듀한다면 어떤 최적화를 선택해야 할지 힌트를 제공한다.**

<br>

**Characteristics 세 항목을 포함하는 열거형**

- **UNORDERED** : 리듀싱 결과는 스트림 요소의 방문 순서나 누적 순서에 영향을 받지 않는다.
- **CONCURRENT** : 다중 스레드에서 accumulator 함수를 동시에 호출할 수 있으며 이 컬렉터는 스트림의 병렬 리듀싱을 수행할 수 있다. 컬렉터의 플래그에 UNORDERED를 함께 설정하지 않았다면 데이터 소스가 정렬되어 있지 않은 상황에서만 병렬 리듀싱을 수행할 수 있다.
- **IDENTITY_FINISH** : finisher 메서드가 반환하는

