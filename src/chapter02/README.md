# 2. 동작 파라미터화 코드 전달하기

우리의 엔지니어링적인 비용이 가장 최소화될 수 있으면 좋을 것이다. 그뿐 아니라 새로 추가한 기능은 쉽게 구현할 수 있어야 하며 장기적인 관점에서 유지보수가 쉬워야 한다.

</br>

> **동적 파라미터화(behavior parameterization)** 
>
> : 아직은 어떻게 실행할 것인지 결정하지 않은 코드 블록을 의미한다. 이 코드 블록은 나중에 프로그램에서 호출한다.

</br>

**동적 파라미터화(behavior parameterization)를** 이용하면 자주 바뀌는 요구사항에 효과적으로 대응할 수 있다. 

</br>

* **동적 파라미터화 동작 예시**
  * 리스트의 모든 요소에 대해서 '어떤 동작'을 수행할 수 있음
  * 리스트 관련 작업을 끝낸 다음에 '어떤 다른 동작'을 수행할 수 있음
  * 에러가 발생하면 '정해진 어떤 다른 동작'을 수행할 수 있음

</br>

#2.1. 변화하는 요구사항에 대응하기

하나의 예제를 통해 점차 개선하면서 유연한 코드를 만드는 모범사례

* **예시) 농장 재고목록 애플리케이션에 리스트에서 녹색 사과만 필터링하는 기능 추가**

</br>

## 2.1.1. 첫 번째 시도 : 녹색 사과 필터링

* **코드**

  ```java
  enum Color { RED, GREEN }
  
  public static List<Apple> filterGreenApples(List<Apple> inventory) {
    List<Apple> result = new ArrayList<>();		// 사과 누적리스트
    for (Apple apple : inventory) {
      if (GREEN.equals(apple.getColor())) {		// 녹색 사과만 선택
        result.add(apple);
      }
    }
    return result;
  }
  ```

  > 갑자기 농부가 녹색 말고 빨간 사과도 필터링을 하고 싶어지면, 메서드를 복사해서 새로운 메서드를 만들고, if문의 조건을 빨간 사과로 바꿀 수 있다. 하지만 농부의 요구사항이 많아지면 코드가 매우 복잡해진다. 그래서 이런 상황에서는 좋은 규칙이 있다.

  *거의 비슷한 코드가 반복 존재한다면 그 코드를 추상화한다.*

</br>

## 2.1.2. 두 번째 시도 : 색을 파라미터화

색을 파라미터화할 수 있도록 메서드에 파라미터를 추가하면 변화하는 요구사항에 좀 더 유연하게 대응하는 코드를 만들 수 있다.

* **코드**

  ```java
  public static List<Apple> filterAppleByColor(List<Apple> inventory, Color color) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (apple.getColor().equals(color)) {
        result.add(apple);
      }
    }
    return result;
  }
  ```

* **메서드 호출**

  ```java
  List<Apple> greenApples = filterApplesByColor(inventory, GREEN);
  List<Apple> redApples = filterApplesByColor(inventory, RED);
  ```

  > 색을 구분하기에는 정말 편리해졌으나, 무게를 구분하라는 요구사항이 추가되면 메서드의 매개 변수가 변경될 것이다.

* **변경된 코드**

  ```java
  public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (apple.getWeight() > weight) {
        result.add(apple);
      }
    }
    return result;
  }
  ```

  > 하지만 구현 코드를 자세히 보면 목록을 검색하고, 각 사과에 필터링 조건을 적용하는 부분의 코드가 색 필터링 코드와 대부분 중복된다. 이는 소프트웨어 공학의 **DRY(don't repeat yourself, 같은 것을 반복하지 말 것)** 원칙을 어기는 것이다.

따라서 색이나 무게 중 어떤 것을 기준으로 필터링할지 가리키는 플래그를 추가할 수 있다.

</br>

## 2.1.3. 세 번째 시도 : 가능한 모든 속성으로 필터링

* **모든 속성을 메서드 파라미터로 추가한 코드**

  ```java
  public static List<Apple> filterApples(List<Apple> inventory, Color color, 
                                        int weight, boolean flag) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      // 색이나 무게를 선택하는 방법이 마음에 들지 않는다.
      if ((flag && apple.getColor().equals(color)) ||
         (!flag && apple.getWeight() > weight)) {
        result.add(apple);
      }
    }
    return result;
  }
  ```

* **호출 코드**

  ```java
  List<Apple> greenApples = filterApples(inventory, GREEN, 0, true);
  List<Apple> heavyApples = filterApples(inventory, null, 150, false);
  ```

  > 이와 같은 코드는 요구사항이 바뀌었을 때 유연하게 대응할 수 없으므로 형편없는 코드다.

</br>

# 2.2. 동작 파라미터화

사과의 어떤 속성에 기초해서 불리언값을 반환하는 방법이 있다. 참 또는 거짓을 반환하는 함수를 **프레디케이트** 라고 한다. **선택 조건을 결정하는 인터페이스를** 정의하자.

* **인터페이스**

  ```java
  public interface ApplePredicate {
    boolean test(Apple apple);
  }
  ```

* **다양한 선택 조건을 대표하는 구현 클래스들**

  ```java
  // 무거운 사과만 선택
  public class AppleHeavyWeightPredicate implements ApplePredicate {
    public boolean test(Apple apple) {
      return apple.getWeight() > 150;
    }
  }
  
  // 녹색 사과만 선택
  public class AppleGreenColorPredicate implements ApplePredicate {
    public boolean test(Apple apple) {
      return GREEN.equals(apple.getColor());
    }
  }
  ```

* **사과를 선택하는 다양한 전략**

  <img src="../capture/스크린샷 2019-08-14 오후 5.55.22.png">

  > 위 조건에 따라 filter 메서드가 다르게 동작할 것이라고 예상할 수 있다. 이를 **전략 디자인 패턴(strategy design pattern)** 이라 한다.

  * **전략 디자인 패턴** : 각 알고리즘을 캡슐화하는 알고리즘 패밀리를 정의해둔 다음에 런타임에 알고리즘을 선택하는 기법이다.
  * 즉, 메서드가 다양한 동작(또는 전략)을 받아서 내부적으로 다양한 동작을 수행할 수 있도록 **동작 파라미터화 시킨다.**

</br>

## 2.2.1. 네 번째 시도 : 추상적 조건으로 필터링

* **filterApples(), apple을 필터링할 메소드 구현**

  ```java
  public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      // 프레디케이트 객체로 사과 검사 조건을 캡슐화하였다.
      if (p.test(apple)) {
        result.add(apple);
      }
    }
    return result;
  }
  ```

</br>

### 코드/동작 전달하기

위의 예제에서 가장 중요한 구현은 test 메서드다. filterApples 메서드의 새로운 동작을 정의하는 것이 test 메서드다. 안타깝게도 **메서드는 객체만 인수로 받으므로** test 메서드를 ApplePredicate 객체로 감싸서 전달해야 한다.

</br>

### 한 개의 파라미터, 다양한 동작

지금까지 살펴본 것 처럼 컬렉션 탐색 로직과 각 항목에 적용할 동작을 분리할 수 있다는 것이 **동작 파라미터화의 강점이다.**

</br>

# 2.3. 복잡한 과정 간소화

자바는 클래스의 선언과 인스턴스화를 동시에 수행할 수 있도록 **익명 클래스(anonymous class)라는** 기법을 제공한다. 

## 2.3.1. 익명 클래스

* **익명 클래스** 
  * 자바의 지역 클래스(local class, 블록 내부에 선언된 클래스)와 비슷한 개념이다. 
  * 말 그대로 이름이 없는 클래스다. 
  * 클래스 선언과 인스턴스화를 동시에 할 수 있다.

</br>

## 2.3.2. 다섯 번째 시도 : 익명 클래스 사용

* **예시1**

  ```java
  List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
    public boolean test(Apple a) {
      return RED.equals(a.getColor());
    }
  });
  button.setOnAction(new EventHandler<ActionEvent>() {
    public void handle(ActionEvent event) {
      System.out.println("Whoooo a click!");
    }
  });
  ```

  > 따로 인터페이스를 구현하지 않고 클래스 선언과 인스턴스화를 동시에 해줌으로써 좀 편리해졌지만, 코드가 반복되어 지저분한 코드가 발생한다.

</br>

* **예시2**

  ```java
  public class MeaningOfThis {
    
    public final int value = 4;
    
    public void doIt() {
      int value = 6;
      Runnable r = new Runnable() {
        public final int value = 5;
        public void run() {
          int value = 10;
          System.out.println(this.value);
        }
      };
      r.run();
    }
    
    public static void main(String...args) {
      MeaningOfThis m = new MeaningOfThis();
      m.doIt();	// 이 행의 출력 결과는?
    }
    
  }
  ```

  > **정답 : 코드에서 this는 MeaningOfThis가 아니라 Runnable을 참조하므로 5가 정답이다.**

</br>

익명 클래스로 인터페이스를 구현하는 여러 클래스를 선언하는 과정을 조금 줄일 수 있지만, 코드가 **장황(vebosity)** 해져서 구현하고 유지보수하는 데 시간이 오래 걸린다.

</br>

## 2.3.3. 여섯 번째 시도 : 람다 표현식 사용

* **자바 8의 람다 표현식을 사용한 코드**

  ```java
  List<Apple> result = filterApples(inventory, 
                                    (Apple apple) -> RED.equals(apple.getColor()));
  ```

  > 이전 코드보다 훨씬 간단하고 문제를 더 잘 설명하는 코드가 되었다.

</br>

## 2.3.4. 일곱 번째 시도 : 리스트 형식으로 추상화

* **filter 메소드를 리스트 형식을 처리하는 방식으로 구현**

  ```java
  public interface Predicate<T> {
    boolean test(T t);
  }
  
  public static <T> List<T> filter(List<T> list, Predicate<T> p) {
    List<T> result = new ArrayList<>();
    for (T e : list) {
      if (p.test(e)) {
        result.add(e);
      }
    }
    return result;
  }
  ```

*  **람다식으로 filter 메소드 실행**

  ```java
  // 빨간색 사과를 필터링하도록 메소드호출
  List<Apple> redApples = filter(inventory, 
                                 (Apple apple) -> RED.equals(apple.getColor()));
  // 짝수인 수들을 필터링하도록 메소드호출
  List<Integer> evenNumbers = filter(numbers, (Integer i) -> i % 2 == 0);
  ```

  > 이처럼 유연성과 간결함이라는 두 마리 토끼를 모두 잡은 코드를 확인할 수 있다.

</br>

# 2.4. 실전 예제

**동작 파라미터화 패턴은** 동작을 캡슐화한 다음에 메서드로 전달해서 메서드의 동작을 파라미터화한다.

</br>

## 2.4.1. Comparator로 정렬하기

자바 8의 List에는 sort 메서드가 포함되어 잇다. 다음과 같은 인터페이스를 갖는 java.util.Comparator 객체를 이용해서 sort의 동작을 파라미터화 할 수 있다.

* **sort 동작 파라미터화**

  ```java
  // java.util.Comparator
  public interface Comparator<T> {
    int compare(T o1, T o2);
  }
  ```

* **예시) 무게가 적은 순서로 익명 객체를 통해 정렬**

  ```java
  inventory.sort(new Comparator<Apple>() {
    public int compare(Apple a1, Apple a2) {
      return a1.getWeight().compareTo(a2.getWeight());
    }
  });
  ```

* **람다 표현식을 이용한 정렬**

  ```java
  inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
  ```

</br>

## 2.4.2. Runnable로 코드 블록 실행하기

자바 8 이전 까지는 Thread 생성자에 객체만을 전달할 수 있었으므로 보통 결과를 반환하지 않는 void run 메소드를 포함하는 **익명 클래스가 Runnable 인터페이스를 구현하도록 하는 것이** 일반적인 방법이었다.

* **예시) 익명 객체를 통한 Runnable 구현**

  ```java
  Thread t = new Thread(new Runnable() {
    public void run() {
      System.out.println("Hello world");
    }
  });
  ```

자바 8 부터는 지원하는 람다 표현식을 이용하면 다음처럼 스레드를 구현할 수 있다.

* **예시) 람다식을 통한 Runnable 구현**

  ```java
  Thread t = new Thread(() -> System.out.println("Hello world"));
  ```

</br>

## 2.4.3. GUI 이벤트 처리하기

자바 5부터 지원하는 **ExecutorService 인터페이스는** 태스크 제출과 실행 과정의 연관성을 끊어준다. 

ExecutorService를 이용하면 태스크를 스레드 풀로 보내고 결과를 Future로 저장할 수 있다는 점이 스레드와 Runnable을 이용하는 방식과는 다르다. 

ExecutorService를 이용하려면 **Callable 인터페이스를 이용해** 결과를 반환하는 태스크를 만들면 된다.

* **예시) 익명 구현 객체를 통한 ExecutorService 이용**

  ```java
  public interface Callable<V> {
    V call();
  }
  
  ExecutorService executorService = Executors.newCachedThreadPool();
  Future<String> threadName1 = executorService.submit(new Callable<String>() {
    @Override
    public String call() throws Exception {
      return Thread.currentThread().getName();
    }
  });
  ```

* **예시) 람다식을 통한 ExecutorService 이용**

  ```java
  Future<String> threadName2 = executorService.submit(
    () -> Thread.currentThread().getName());
  ```

</br>

## 2.4.4. GUI 이벤트 처리하기

GUI 프로그래밍에서도 변화에 대응할 수 있는 유연한 코드가 필요하다. 모든 동작에 반응할 수 있어야 하기 때문이다.

자바FX에서는 setOnAction 메서드에 EventHandler를 전달함으로써 이벤트에 어떻게 반응할지 설정할 수 있다.

* **예시) 익명 객체로 EventHandler 사용**

  ```java
  Button button = new Button("Send");
  button.setOnAction(new EventHandler<ActionEvent>() {
    public void handle(ActionEvent event) {
      label.setText("Sent!!");
    }
  });
  ```

* **예시) 람다 표현식으로 EventHandler 사용**

  ```java
  button.setOnAction((ActionEvent event) -> label.setText("Sent!!"));
  ```

</br>

# 2.5. 마치며

* 동작 파라미터화에서는 메서드 내부적으로 다양한 동작을 수행할 수 있도록 **코드를 메서드 인수로 전달한다.**
* 동작 파라미터화를 이용하면 **변화하는 요구사항에 더 잘 대응할 수 있는 코드를 구현할 수 있다.**
* 자바 8에서는 **인터페이스를 상속받아 여러 클래스를 구현해야 하는 수고를 없앨 수 있는 방법을 제공한다.**
* 자바 API의 많은 메서드는 **정렬, 스레드, GUI 처리 등을 포함한 다양한 동작으로 파라미터화할 수 있다.**