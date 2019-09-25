package chapter07;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WordCounterSpliterator implements Spliterator<Character> {

  private final String string;
  private int currentChar = 0;

  public WordCounterSpliterator(String string) {
    this.string = string;
  }

  @Override
  public boolean tryAdvance(Consumer<? super Character> action) {
    action.accept(string.charAt(currentChar++));   // 현재 문자를 소비한다.
    return currentChar < string.length();          // 소비할 문자가 남아있으면 true 를 반환
  }

  @Override
  public Spliterator<Character> trySplit() {
    int currentSize = string.length() - currentChar;
    if (currentSize < 10) {
      // 피싱할 문자열을 순차 처리할 수 있을 만큼
      // 충분히 작아졌음을 알리는 null 을 반환한다.
      return null;
    }
    // 피싱할 문자열의 중간을 분할 위치로 설정한다.
    for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
      // 다음 공백이 나올 때까지 분할 위치를 뒤로 이동 시킨다.
      if (Character.isWhitespace(string.charAt(splitPos))) {
        // 처음부터 분할 위치까지 문자열을 파싱할
        // 새로운 WordCounterSpliterator 를 생성한다.
        Spliterator<Character> spliterator =
            new WordCounterSpliterator(string.substring(currentChar,
                splitPos));
        // 이 WordCounterSpliterator 의 시작 위치를 분할 위치로 설정한다.
        currentChar = splitPos;
        // 공백을 찾았고 문자열을 분리했으므로 루프를 종료한다.
        return spliterator;
      }
    }
    return null;
  }

  @Override
  public long estimateSize() {
    return string.length() - currentChar;
  }

  @Override
  public int characteristics() {
    return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
  }

  public static void main(String[] args) {
    final String SENTENCE = "There is no requirement that a new or distinct result be returned each " +
        "time the supplier is invoked.";

//    System.out.println("Found " + );
  }

}
