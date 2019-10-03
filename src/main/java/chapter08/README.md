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
* 하지만 이런 제약이 꼭 나쁜 것은 아니다. **컬렉션이 의도치 않게 변하는 것을 막을 수 있기 때문이다.**
* 리스트를 바꿔야 하는 상황이라면 직접 리스트를 만들면 된다.
* null 요소는 금지하므로 의도치 않은 버그를 방지하고 조금 더 간결한 내부 구현을 해준다.

<br>

### 오버로딩 vs 가변 인수

List.of 는 다양한 오버로드 버전이 있다.

```java
static <E> List<E> of(E e1, E e2, E e3, E e4)
static <E> List<E> of(E e1, E e2, E e3, E e4, E e5)
```

하지만 왜 다중 요소(...)를 받을 수 있도록 구현하지 않았을까?

```java
static <E> List<E> of(E... elements)
```

왜냐하면 가변 인수 버전은 추가 배열을 할당해서 리스트로 감싼다. 따라서 배열을 할당하고 초기화하며 나중에 가비지 컬렉션을 하는 비용을 지불해야 하기 때문이다.

그래서 최대 열 개까지는 API로 정의하고, List.of로 열 개 이상의 요소를 가진 리스트를 만들게 되면 이 때는 가변 인수를 이용한다.

<br>

### Collectors.toList()

Collectors.toList() 컬렉터로 스트림을 리스트로 변환할 수 있다. **데이터 처리 형식을 설정하거나 데이터를 변환할 필요가 없다면 사용하기 간편한 팩토리 메서드를 이용할 것을 권장한다.**

<br>

## 8.1.2. 집합 팩토리

```java
Set<String> friends = Set.of("Raphael", "Olivia", "Thibaut");
System.out.println(friends);
```

* 집합은 오직 고유의 요소만 포함할 수 있으므로 중복되는 요소를 같이 집합으로 만들려고 하면 **IllegalArgumentException이** 발생한다.

<br>

## 8.1.3. 맵 팩토리

Map.of 팩토리 메서드에 키와 값을 번갈아 제공하는 방법으로 맵을 만들 수 있다.

```java
Map<String, Integer> ageOfFriends
        = Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
```

* 열 개 이하의 키와 값 쌍을 가진 작은 맵을 만들 때는 이 메소드가 유용하다.

<br>

Map.Entry\<K, V> 객체를 인수로 받으며 가변 인수로 구현된 Map.ofEntries 팩토리 메서드를 이용하는 것이 좋다.

```java
import static java.util.Map.entry;

Map<String, Integer> ageOfFriends =
  Map.ofEntries(entry("Raphael", 30),
               entry("Olivia", 25),
               entry("Thibaut", 26));
```

<br>

# 8.2. 리스트와 집합 처리

자바 8에서는 List, Set 인터페이스에 다음과 같은 메서드를 추가했다.

* **removeIf** : 프레디케이트를 만족하는 요소를 제거한다.
* **replaceAll** : 리스트에서 이용할 수 있는 기능으로 UnaryOperator 함수를 이용해 요소를 바꾼다.
* **sort** : List 인터페이스에서 제공하는 기능으로 리스트를 정렬한다.

<br>

## 8.2.1. removeIf 메서드

removeIf 메서드는 삭제할 요소를 가리키는 프레디케이트를 인수로 받는다.

```java
transactions.removeIf(transaction ->
                     Character.isDigit(transaction.getReferenceCode().charAt(0)));
```

* 때로는 요소를 제거하는 게아니라 바꿔야 하는 상황이 온다. 이때는 **replaceAll** 을 사용한다.

<br>

## 8.2.2. replaceAll 메서드

replaceAll 메서드를 이용해 리스트의 각 요소를 새로운 요소로 바꿀 수 있다.

```java
referenceCodes.replaceAll(code ->
                          Character.toUpperCase(code.charAt(0)) + code.substring(1));
```

<br>

# 8.3. 맵 처리

자바 8에서는 Map 인터페이스에 몇 가지 디폴트 메서드를 추가했다.

<br>

## 8.3.1. forEach 메서드

자바 8에서 부터 Map 인터페이스는 **BiConsumer(키와 값을 인수로 받음)를** 인수로 받는 forEach 메서드를 지원하므로 코드를 조금 더 간단하게 구현할 수 있다.

```java
ageOfFriends.forEach((friend, age) -> System.out.println(
  friend + " is " + age + " year old"));
```

<br>

## 8.3.2. 정렬 메서드

두 개의 새로운 유틸리티를 이용하면 맵의 항목을 값 또는 키를 기준으로 정렬할 수 있다.

* **Entry.comparingByValue**
* **Entry.comparingByKey**

<br>

* **예시) 사람의 이름을 알파벳 순으로 스트림 요소 처리**

  ```java
  Map<String, String> favouriteMovies
          = Map.ofEntries(entry("Raphael", "Star Wars"),
                          entry("Cristina", "Matrix"),
                          entry("Olivia", "James Bond"));
  
  favouriteMovies
    .entrySet()
    .stream()
    .sorted(Map.Entry.comparingByKey())
    .forEachOrdered(System.out::println);
  ```

* **실행 결과**

  ```
  Cristina=Matrix
  Olivia=James Bond
  Raphael=Star Wars
  ```

<br>

### HashMap 성능

버킷이 너무 커질 경우 이를 O(log(n))의 시간이 소요되는 **정렬된 트리를 이용해 동적으로 치환해 충돌이 일어나는 요소 반환 성능을 개선했다.** 하지만 키가 String, Number 클래스 같은 Comparable의 형태여야만 정렬된 트리가 지원된다.

<br>

## 8.3.3. getOrDefault 메서드

기존에는 찾으려는 키가 존재하지 않으면 NullPointerException을 방지하려면 요청 결과가 널인지 확인해야 했다. 기본 값을 반환하는 방식으로 이 문제를 해결할 수 있다. 

* **getOrDefault 메서드**
  * **첫 번째 인수** : 키
  * **두 번째 인수** : 기본값

<br>

예시 코드

```java
Map<String, String> favouriteMovies
        = Map.ofEntries(entry("Raphael", "Star Wars"),
                        entry("Olivia", "James Bond"));

System.out.println(favouriteMovies.getOrDefault("Olivia", "Matrix"));
System.out.println(favouriteMovies.getOrDefault("Thibaut", "Matrix"));
```

실행 결과

```
James Bond
Matrix
```

> 키가 존재하더라도 값이 널인 상황에서는 getOrDefault가 널을 반환할 수 있다.

<br>

## 8.3.4. 계산 패턴

맵에 키가 존재하는지 여부에 따라 어떤 동작을 실행하고 결과를 저장해야 하는 상황이 필요한 때가 있다.

* **위와 같은 상황을 개선하는 연산들 **
  * **computeIfAbsent** : 제공된 키에 해당하는 값이 없으면(값이 없거나 널), 키를 이용해서 값을 계산하고 맵에 추가한다.
  * **computeIfPresent** : 제공된 키가 존재하면 새 값을 계산하고 맵에 추가한다.
  * **compute** : 제공된 키로 새 값을 계산하고 맵에 저장한다.

<br>

정보를 캐시할 때 **computeIfAbsent를** 활용할 수 있다. 파일 집합의 각 행을 파싱해 SHA-256을 계산한다고 가정하자.

* **MessageDisget 인스턴스로 SHA-256 해시를 계산할 수 있다.**

  ```java
  Map<String, byte[]> dataToHash = new HashMap<>();
  MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
  ```

  데이터를 반복하면서 결과를 캐시한다.

  ```java
  lines.forEach(line ->
               dataToHash.computeIfAbsent(line,  // line은 맵에서 찾은 키
                                          // 키가 존재하지 않으면 동작을 실행
                                         this::calculateDigest));
  
  // 헬퍼가 제공된 키의 해시를 계산할 것이다.
  private byte[] calculateDigest(String key) {
    return messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
  }
  ```

<br>

여러 값을 저장하는 맵을 처리할 때도 위와 같은 패턴을 유용하게 사용할 수 있다.

* **예시) Raphael 에게 줄 영화 목록 만들기**

  ```java
  String friend = "Raphael";
  List<String> movies = friendsToMovies.get(friends);
  if (movies == null) {       // 리스트가 초기화되었는지 확인
    movies = new ArrayList<>();
    friendsToMovies.put(friend, movies);
  }
  movies.add("Star Wars");		// 영화를 추가
  ```

* **위의 코드를 computeIfPresent 메서드로 구현한 코드**

  ```java
  friendsToMovies.computeIfAbsent("Raphael", name -> new ArrayList<>())
    .add("Star Wars");
  ```

<br>

**computeIfPresent 메서드는** 현재 키와 관련된 값이 맵에 존재하며 널이 아닐 때만 새 값을 계산한다.

즉, 값을 만드는 함수가 널을 반환하면 현재 매핑을 맵에서 제거한다.

<br>

## 8.3.5. 삭제 패턴

* **키가 특정한 값과 연관되었을 때만 항목을 제거하는 오버로드 버전 메소드 사용**

  ```java
  String key = "Raphael";
  String value = "Jack Reacher 2";
  if (favouriteMovies.containsKey(key) &&
     Objects.equals(favouriteMovies.get(key), value)) {
    favoriteMovies.remove(key);
    return true;
  }  else {
    return false;
  }
  ```

* **위의 코드를 보완한 코드**

  ```java
  favoriteMovies.remove(key, value);
  ```

<br>

## 8.3.6. 교체 패턴

맵의 항목을 바꾸는 데 사용할 수 있는 두 개의 메서드

* **replaceAll** : BiFunction을 적용한 결과로 각 항목의 값을 교체한다. 이 메서드는 이전에 살펴본 List 의 replaceAll과 비슷한 동작을 수행한다.
* **Replace** : 키가 존재하면 맵의 값을 바꾼다. 키가 특정 값으로 매핑되었을 때만 값을 교체하는 오버로드 버전도 있다.

<br>

* **맵의 모든 값의 형식을 바꾸는 예시**

  ```java
  Map<String, String> favoriteMovies = new HashMap<>();
  favoriteMovies.put("Raphael", "Star Wars");
  favoriteMovies.put("Olivia", "james bond");
  favoriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
  ```

<br>

## 8.3.7. 합침