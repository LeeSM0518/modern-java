# 9. 리팩터링, 테스팅, 디버깅

# 9.1. 가독성과 유연성을 개선하는 리팩터링

코드 가독성을 높이려면 코드의 문서화를 잘하고, 표준 코딩 규칙을 준수하는 등의 노력을 기울여야 한다.

자바 8 이후의 기능인 **람다, 메서드 참조, 스트림을** 활용하면 코드 가독성을 높일 수 있다.

<br>

## 9.1.2. 익명 클래스를 람다 표현식으로 리팩터링하기

하나의 추상 메서드를 구현하는 **익명 클래스는 람다 표현식으로** 리팩터링 할 수 있다.

<br>

* **예시) Runnable 객체를 만드는 익명 클래스와 람다 표현식**

  ```java
  Runnable r1 = new Runnable() {
    public void run() {
      System.out.println("hello");
    }
  };
  Runnable r2 = () -> System.out.println("hello");
  ```

  * 하지만 모든 익명 클래스를 람다 표현식으로 변환할 수 있는 것은 아니다.

<br>

1. **익명 클래스에서 사용한 this와 super는 람다 표현식에서 다른 의미를 갖는다.** 익명 클래스의 this는 익명클래스 자신을 가리키지만 람다에서 this는 람다를 감싸는 클래스를 가리킨다.

2. 익명 클래스는 감싸고 있는 클래스의 변수를 가릴 수 있다. 하지만 **람다 표현식으로는 변수를 가릴 수 없다.**

   ```java
   int a = 10;
   Runnable r1 = () -> {
     int a = 2;  // 컴파일 에러
     System.out.println(a);
   };
   Runnable r2 = new Runnable() {
     @Override
     public void run() {
       int a = 2;  // 잘 동작
       System.out.println(a);
     }
   };
   ```

3. 익명 클래스를 람다 표현식으로 바꾸면 콘텍스트 오버로딩에 따른 모호함이 초래될 수 있다. 익명 클래스는 인스턴스화할 때 명시적으로 형식이 정해지는 반면 **람다의 형식은 콘텍스트에 따라 달라진다.**

   * **예시) Task 라는 Runnable과 같은 시그니처를 갖는 함수형 인터페이스를 선언**

     ```java
     interface Task {
       public void execute();
     }
     
     public static void doSomething(Runnable r) { r.run() }
     public static void doSomething(Task a) { r.execute(); }
     ```

     Task를 구현하는 익명 클래스를 전달할 수 있다.

     ```java
     doSomething(new Task() {
       public void execute() {
         System.out.print
       }
     })
     ```

     하지만 익명 클래스를 람다 표현식으로 바꾸면 메서드를 호출할 때 Runnable과 Task 모두 대상 형식이 될 수 있으므로 문제가 생긴다.

     ```java
     doSomething(() -> System.out.println("Danger danger!!"));
     ```

     > doSomething(Runnable) 과 doSomthing(Task) 중 어느 것을 가리키는지 알 수 없는 모호함이 발생한다.

     명시적 형변환 (Task) 를 이용해서 모호함을 제거할 수 있다.

     ```java
     doSomthing((Task)() -> System.out.println("Danger danger!!"));
     ```

     <br>

## 9.1.3. 람다 표현식을 메서드 참조로 리팩터링하기

람다 표현식 대신 메서드 참조를 이용하면 가독성을 높일 수 있다. 

* **칼로리 수준으로 요리를 그룹화하는 코드**

  ```java
  Map<CaloricLevel, List<Dish>> dishesByCaloricLevel =
    menu.stream()
    .collect(
    groupingBy(dish -> {
      if (dish.getCalories() <= 400) return CaloricLevel.DIET;
      else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
      else return CaloricLevel.FAT;
    }));
  ```

  람다 표현식을 별도의 메서드로 추출한 다음에 groupingBy에 인수로 전달할 수 있다. 

  ```java
  Map<CaloricLevel, List<Dish>> dishesByCaloricLevel =
    menu.stream().collect(groupingBy(Dish::getCaloricLevel));
  ```

  이제 Dish 클래스에 getCaloricLevel 메서드를 추가해야 한다.

  ```java
  public class Dish {
    ...
    public CaloricLevel getCaloricLevel() {
      if (this.getCalories() <= 400) return CaloricLevel.DIET;
      else if (this.getCalories() <= 700) return CaloricLevel.NORMAL;
      else return CaloricLevel.FAT;
    }
  }
  ```

<br>

또한 comparing과 maxBy 같은 정적 헬퍼 메서드를 활용하는 것도 좋다.

```java
// 비교 구현에 신경 써야 한다.
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
// 코드가 문제 자체를 설명한다.
inventory.sort(comparing(Apple::getWeight));
```

<br>

sum, maximum 등의 Collectors API를 사용하면 코드의 의도가 더 명확해진다.

```java
int totalCalories =
  menu.stream().map(Dish::getCalories)
  .reduce(0, (c1, c2) -> c1 + c2);
```

내장 컬렉터를 이용하면 코드 자체로 문제를 더 명확하게 설명할 수 있다.

```java
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
```

<br>

## 9.1.4. 명령형 데이터 처리를 스트림으로 리팩터링하기

스트림 API는 데이터 처리 파이프라인의 의도를 더 명확하게 보여준다.

* **두 가지 패턴(필터링과 추출)으로 엉킨 코드**

  ```java
  List<String> dishNames = new ArrayList<>();
  for(Dish dish: menu) {
    if (dish.getCalories() > 300) {
      dishNames.add(dish.getName());
    }
  }
  ```

* **스트림 API를 이용**

  ```java
  menu.parallelStream()
    .filter(d -> d.getCalories() > 300)
    .map(Dish::getName)
    .collect(toList());
  ```

<br>

## 9.1.5. 코드 유연성 개선

다양한 람드를 전달해서 다양한 동작을 표현할 수 있다. 따라서 변화하는 요구사항에 대응할 수 있는 코드를 구현할 수 있다.

<br>

### 함수형 인터페이스 적용

먼저 람다 표현식을 이용하려면 함수형 인터페이스가 필요하다. 따라서 함수형 인터페이스를 코드에 추가해야 한다.

**조건부 연기 실행(conditional deferred execution)과 실행 어라운드(execute around),** 즉 두 가지 자주 사용하는 패턴으로 람다 표현식으로 리팩터링 해보자.

<br>

### 조건부 연기 실행

흔히 보안 검사나 로깅 관련 코드는 코드 내부에 제어 흐름문이 복잡하게 얽혀 있다.

* **내장 자바 Logger 클래스 사용 예제**

  ```java
  if (logger.isLoggable(Log.FINER)) {
    logger.finer("Problem: " + generateDiagnostic());
  }
  ```

* **위 코드의 문제점**

  * logger의 상태가 isLoggable 이라는 메서드에 의해 클라이언트 코드로 노출된다.
  * 메시지를 로깅할 때마다 logger 객체의 상태를 매번 확인해야 할까? 이들은 코드를 어지럽힐 뿐이다.

* 메시지가 로깅하기 전에 looger 객체가 적절한 수준으로 설정되었는지 내부적으로 확인하는 log 메서드를 사용하는 것이 바람직하다.