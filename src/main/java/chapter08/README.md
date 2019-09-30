# 8. 컬렉션 API 개선

# 8.1. 컬렉션 팩토리

자바 9에서는 작은 컬렉션 객체를 쉽게 만들 수 있는 몇 가지 방법을 제공한다.

* **예시) 휴가를 함께 보내려는 친구 이름을 포함하는 그룹**

  ```java
  List<String> friends = new ArrayList<>();
  friends.add("Raphael");
  friends.add("Olivia");
  friends.add("Thibaut");
  ```

  **Arrays.asList() 팩토리 메서드 사용**

  ```java
  List<String> friends = Arrays.asList("Raphael", "Olivia", "Thibaut");
  ```

  * 고정 크기의 리스트를 만들었으므로 요소를 갱신할 순 있지만 새 요소를 추가하거나 요소를 삭제할 순 없다.

  * 요소를 추가하려 하면 **UnsupportedOperationException** 이 발생한다.

  <br>

### UnsupportedOperationException 예외 발생

내부적으로 고정된 크기의 변환할 수 있는 배열로 구현되었기 때문에 이와 같은 일이 일어난다.

* **예시) Set 데이터 저장**

  ```java
  // 리스트를 인수로 받는 HashSet 생성자 사용
  Set<String> friends = new HashSet<>(Arrays.asList("Raphael", "Olivia", "Thibaut"));
  // 스트림 API 사용
  Set<String> friedns = Stream.of("Raphael", "Olivia", "Thibaut")
    .collect(Collectors.toSet());
  ```

  * 두 방법은 불필요한 객체 할당을 필요로 하며, 결과는 변환할 수 있는 집합이므로 요소를 추가, 삭제를 할 수 없다.

  <br>

이러한 점들을 보완하기 위해, **자바 9에서 작은 리스트, 집합, 맵을 쉽게 만들 수 있도록 팩토리 메서드를 제공한다.**

<br>

## 8.1.1. 리스트 팩토리

**List.of** 팩토리 메소드를 이용해서 간단하게 리스트를 만들 수 있다.

```java
List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
System.out.println(friends);
```

* friends 리스트에 요소를 추가하면 **java.lang.UnsupportedOperationException** 이 발생한다. 왜냐하면, **변경할 수 없는 리스트가 만들어졌기 때문이다.**
* 