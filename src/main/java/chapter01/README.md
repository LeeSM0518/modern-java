# 1. 자바 8, 9, 10, 11: 무슨 일이 일어나고 있는가?

<br>

# 1.1. 역사의 흐름은 무엇인가?

* **사과 목록을 무게순으로 정렬하는 고전적 코드**

  ```java
  Collection.sort(inventory, new Comparator<Apple>() {
    public int compare(Apple a1, Apple a2) {
      return a1.getWeight().compareTo(a2.getWeight());
    }
  })
  ```

* **자바 8을 이용하여 자연어에 더 가깝게 간단한 방식으로 구현한 코드**

  ```java
  inventory.sort(comparing(Apple::getWeight))
  ```

  > 훨씬 간단하게 구현되는 것을 확인할 수 있다.

<br>

자바 8에서 부터는 병렬 실행을 새롭고 단순한 방식으로 접근할 수 있는 방법을 제공한다. 하지만 이 새로운 기법을 이용하려면 몇 가지 규칙을 지켜야 하는데 그 규칙들을 학습해보도록 하자.

<br>

* **자바 8의 기반이 되는 요구 사항**
  * 간결한 코드, 멀티코어 프로세서의 쉬운 활용 => **함수형 프로그래밍(functional-style programming)**
    * **스트림 API**
    * 메서드에 코드를 전달하는 기법 **(메서드 참조와 람다)** : 새롭고 간결한 방식으로 동작 파라미터화(behavior parameterization) 구현 가능
    * 인터페이스의 **디폴트 메서드**

<br>

# 1.2. 자바 8 설계의 밑바탕

## 1.2.1. 스트림 처리

* **스트림** : 한 번에 한 개씩 만들어지는 연속적인 데이터 항목들의 모임이다.
  * 자바 8에는 java.util.stream 패키지에 스트림 API가 추가되었다. 스트림 패키지에 정의된 Stream\<T>는 T 형식으로 구성된 일련의 항목을 의미한다.
  * 자바 8에서는 우리가 하려는 작업을 고수준 추상화해서 일련의 스트림으로 만들어 한 번에 처리할 수 있다.
  * 스트림 파이프라인을 이용해서 입력 부분을 여러 CPU 코어에 쉽게 할당할 수 있다.

</br>

## 1.2.2. 동작 파라미터화로 메서드에 코드 전달하기

* 자바 8에서는 메서드(우리 코드)를 다른 메서드의 인수로 넘겨주는 기능을 제공한다. 이러한 기능을 이론적으로 **동작 파라미터화(behavior parameterization)** 라 한다.

</br>

## 1.2.3. 병렬성과 공유 가변 데이터

* 스트림 메서드로 전달하는 코드는 다른 코드와 동시에 실행하더라도 안전하게 실행될 수 있어야 한다.
  * **안전하게 실행할 수 있는 코드란** 공유된 가변 데이터에 접근을 하지 않게 하는 코드이다. 이러한 함수를 순수 함수, 부작용 없는 함수, 상태 없는 함수라 한다.
* 공유되지 않은 가변 데이터, 메서드, 함수 코드를 다른 메서드로 전달하는 두가지 기능은 **함수형 프로그래밍** 패러다임의 핵심적인 사항이다. 

</br>

# 1.3. 자바 함수

프로그래밍 언어에서 **함수(function)** 라는 용어는 **메서드(method)** 특히 정적 메서드와 같은 의미로 사용된다. 

자바의 함수는 이베 더해 **수학적인 함수** 처럼 사용되며 부작용을 일으키지 않는 함수를 의미한다.

* **왜 함수가 필요할까?**
  * 프로그래밍 언어의 핵심은 값을 바꾸는 것이다. 프로그래밍 언어에서는 이 값을 일급값(시민)이라 부른다.
  * 메서드를 값으로 만들면 프로그래밍에 유용하게 활용할 수 있다.

</br>

## 1.3.1. 메서드와 람다를 일급 시민으로

자바 8의 기능들

* **메서드 참조(method refernce)**

  * **예시) 숨겨진 파일을 필터링**

    자바 8 이전의 코드

    ```java
    File[] hiddenFiles = new File(".").listFiles(new FileFilter() {
      public boolean accept(File file) {
        return file.isHidden();						// 숨겨진 파일 필터링!
      }
    });
    ```

    > 단 세 행의 코드지만 각 행이 무슨 작업을 하는지 투명하지 않다.

    자바 8 코드

    ```java
    File[] hiddenFiles = new File(".").listFiles(File::isHidden);
    ```

    > 자바 8의 **메서드 참조(::)를** 이용해서 listFiles에 직접 전달할 수 있게 되어 코드가 훨씬 깔끔해졌다.

</br>

* **람다 : 익명 함수(annoymous functions)**

  ```java
  (int x) -> x + 1
  ```

  > x 라는 인수로 호출하면 x + 1을 반환하는 동작을 수행하도록 코드를 구현할 수 있다.

</br>

## 1.3.2. 코드 넘겨주기: 예제

Apple 클래스와 getColor 메서드가 있고, Apples 리스트를 포함하는 변수 inventory가 있다. 이때 모든 녹색 사과를 선택해서 리스트를 반환하는 프로그램을 구현해봐라.

* **자바 8 이전의 코드**

  ```java
  public static List<Apple> filterGreenApples(List<Apple> inventory) {
    List<Apple> result = new ArrayList<>();
    
    for (Apple apple : inventory) {
      if (GREEN.equals(apple.getColor()))) {
        result.add(apple);
      }
    } 
    return result;
  }
  ```

* **자바 8 코드**

  ```java
  public static boolean isGreenApple(Apple apple) {
    return GREEN.equals(apple.getColor());
  }
  
  public static boolean isHeavyApple(Apple apple) {
    return apple.getWeight() > 150;
  }
  
  public interface Predicate<T> {
    boolean test(T t);
  }
  
  // 메서드 p라는 이름의 predicate 파라미터가 전달된다.
  static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple: inventory) {
      // 사과는 p가 제시하는 조건에 맞는가?
      if (p.test(apple)) {
        result.add(apple);
      }
    }
    return result;
  }
  ```

  > 위와 같은 코드는 다음처럼 메서드를 호출할 수 있다.

  ```java
  filterApples(inventory, Apple::isGreenApple);
  filterApples(inventory, Apple::isHeavyApple);
  ```

</br>

## 1.3.3. 메서드 전달에서 람다로

isHeavyApple, isGreenAple 와 같은 메서드들을 매번 정의하는 것은 귀찮은 일이다.

* **자바 8에서의 코드**

  ```java
  filterApples(inventory, (Apple a) -> GREEN.equals(a.getColor()));
  filterApples(inventory, (Apple a) -> a.getWeight() > 150);
  filterApples(inventory, (Apple a) -> a.getWeight() < 80 || RED.equals(a.getColor()));
  ```

  > 한 번만 사용할 메서드는 따로 정의를 구현할 필요가 없다. 하지만 람다가 몇 줄 이상으로 길어진다면 익명 람다보다는 메서드를 정의하고 참조를 활용하는 것이 바람직하다.

</br>

# 1.4. 스트림

* **스트림을 이용하기 전 코드**

  ```java
  Map<Currency, List<Transaction>> transactionByCurrencies = 
    new HashMap<>();	// 그룹화된 트랜잭션을 더할 Map 생성
  for (Transaction transaction : transactions) {	// 트랜잭션의 리스트를 반복
    if (transaction.getPrice() > 1000) {	// 고가의 트랜잭션을 필터링
      Currency currency = transaction.getCurrency();	// 트랜잭션의 통화 추출
      List<Transaction> transactionForCurrency =
        transactionByCurrencies.get(currency);
      if (transactionForCurrency == null) {		// 현재 통화의 그룹화된 맵에 항목이 없으면 새로 만듬.
        transactionForCurrency = new ArrayList<>();
        transactionByCurrencise.put(currency,
                                   transactionForCurrency);
      }
      // 현재 탐색된 트랜잭션을 같은 통화의 트랜잭션 리스트에 추가한다.
      transactionForCurrency.add(transaction);
    }  
  }
  ```

* **스트림 API를 이용한 코드**

  ```java
  import static java.util.stream.Collectors.groupingBy;
  
  Map<Currency, List<Transaction>> transactionByCurrencies =
    transaction.stream()
    				.filter((Transaction t) -> t.getPrice() > 1000)		// 고가의 트랜잭션 필터링
    				.collect(groupingBy(Transaction::getCurrency));		// 통화로 그룹화함
  ```

  > 스트림 API를 이용하면 컬렉션 API와는 상당히 다른 방식으로 데이터를 처리할 수 있다.
  >
  > 컬렉션 API는 **외부 반복을** 사용하고 스트림 API는 **내부 반복을** 사용한다.

</br>

## 1.4.1. 멀티스레딩은 어렵다

**자바 8은 스트림 API(java.util.stream)로 '컬렉션을 처리하면서 발생하는 모호함과 반복적인 코드 문제', 그리고 '멀티코어 활용 어려움' 이라는 두 가지 문제를 모두 해결 했다.**

* **스트림 기능**
  * 데이터를 **필터링(filtering)** 하거나 데이터를 **추출(extracting)** 하거나, 데이터를 **그룹화(grouping)** 하는 등의 기능
  * 한 CPU는 리스트의 앞부분을 처리하고, 다른 CPU는 리스트의 뒷부분을 처리하는 **포킹 단계(forking step)** 기능

</br>

* **순차 처리 방식 코드**

  ```java
  import static java.util.stream.Collectors.toList;
  List<Apple> heavyApples =
    inventory.stream().filter((Apple a) -> a.getWeight() > 150)
    									. collect(toList());
  ```

  > stream() 을 사용

* **병렬 처리 방식의 코드**

  ```java
  import static java.util.stream.Collectors.toList;
  List<Apple> heavyApples =
    inventory.parallelStream().filter((Apple a) -> a.getWeight() > 150)
    													.collect(toList());
  ```

  > parallelStream()을 사용

</br>

# 1.5. 디폴트 메서드와 자바 모듈

* 자바 9의 모듈 시스템은 모듈을 정의하는 문법을 제공하므로 이를 이용해 패키지 모음을 포함하는 **모듈을** 정의할 수 있다.

* 자바 8에서는 인터페이스를 **쉽게 바꿀 수 있도록 디폴트 메서드를** 지원한다. 디폴트 메서드를 이용하면 기존의 코드를 건드리지 않고도 원래의 인터페이스 설계를 자유롭게 확장할 수 있다.

  * **예시) List 인터페이스에 정렬 디폴트 메소드 정의**

    ```java
    default void sort(Comparator<? super E> c) {
      Collections.sort(this, c);
    }
    ```

    > 자바 8 이전에는 List를 구현하는 모든 클래스가 sort를 구현해야 했지만 자바 8부터는 디폴트 sort를 구현하지 않아도 된다.

</br>

# 1.6. 함수형 프로그래밍에서 가져온 다른 유용한 아이디어

자바 8에서는 NullPointer 예외를 피할 수 있도록 도와주는 **Optional\<T> 클래스를** 제공한다.

*  **Optional\<T>**
  * 값을 갖거나 갖지 않을 수 있는 컨테이너 객체이다.

</br>

* **패턴 매칭**

  스칼라 프로그래밍 언어

  ```scala
  def simpleifyExpression(expr: Expr): Expr = expr match {
    case BinOp("+", e, Number(0)) => e
    case BinOp("-", e, Number(0)) => e
    case BinOp("*", e, Number(1)) => e
    case BinOp("/", e, Number(1)) => e
    case _ => expr
  }
  ```

  > 데이터 형식 분류와 분석을 한 번에 수행할 수 있다.

</br>

# 1.7. 마치며

* 함수는 일급값이다. **메서드를 어떻게 함수형값으로 넘겨주는지, 익명 함수(람다)를 어떻게 구현하는지** 기억하자.
* 자바 8의 스트림 개념 중 일부는 컬렉션에서 가져온 것이다. **스트림과 컬렉션을 적절하게 활용하면 스트림의 인수를 병렬로 처리할 수 있으며 더 가독성이 좋은 코드를 구현할 수 있다.**
* 자바 9에서는 **모듈을 이용해 시스템의 구조를 만들 수 있고 디폴트 메소드를 이용해 기존 인터페이스를 구현하는 클래스를 바꾸지 않고도 인터페이스를 변경할 수 있다.**
* 함수형 프로그래밍에서 **null 처리 방법과 패턴 매칭 활용** 등 흥미로운 기법을 발견했다.

C, C++ 는 프로그래밍 안전성은 부족하지만 작은 런타임 풋프린트(footprint) 덕분에 운영체제와 다양한 임베디드 시스템에서 여전히 인기를 끌고 있다. 하지만 C, C++의 낮은 안정성 때문에 프로그램이 예기치 않게 종료되거나 바이러스 등이 침투할 수 있는 보안 구멍이 있을 수 있다. 

 자바는 지난 1995년 첫 베타 버전이 공개된 이후로 경쟁 언어로 대신하며 커다란 생태계를 성공적으로 구축했다.
