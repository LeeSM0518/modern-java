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

     

