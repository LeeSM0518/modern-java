package chapter07;

import java.util.Spliterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WordCounter {

  private final int counter;
  private final boolean lastSpace;

  public WordCounter(int counter, boolean lastSpace) {
    this.counter = counter;
    this.lastSpace = lastSpace;
  }

  // 반복 알고리즘처럼 accumulate 메서드는
  // 문자열의 문자를 하나씩 탐색한다.
  public WordCounter accumulate(Character c) {
    if (Character.isWhitespace(c)) {
      return lastSpace ?
          this : new WordCounter(counter, true);
    } else {
      return lastSpace ?
          // 문자를 하나씩 탐색하다 공백 문자를 만나면
          // 지금까지 탐색한 문자를 단어로 간주하여
          // 단어 수를 증가시킨다.
          new WordCounter(counter + 1, false) :
          this;
    }
  }

  public WordCounter combine(WordCounter wordCounter) {
    // 두 WordCounter 의 counter 값을 더한다.
    return new WordCounter(counter + wordCounter.counter,
        // counter 값만 더할 것이므로 마지막 공백은 신경 쓰지 않는다.
        wordCounter.lastSpace);
  }

  private static int countWords(Stream<Character> stream) {
    WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
        WordCounter::accumulate, WordCounter::combine);
    return wordCounter.getCounter();
  }

  public int getCounter() {
    return counter;
  }

  public static void main(String[] args) {
    final String SENTENCE = "There is no requirement that a new or distinct result be returned each " +
        "time the supplier is invoked.";
//    Stream<Character> stream = IntStream.range(0, SENTENCE.length())
//        .mapToObj(SENTENCE::charAt);
//    System.out.println("Found " + countWords(stream.parallel()) + " words");
//    stream = IntStream.range(0, SENTENCE.length())
//        .mapToObj(SENTENCE::charAt);
//    System.out.println("Found " + countWords(stream) + " words");
    Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);
    Stream<Character> stream = StreamSupport.stream(spliterator, true);
    System.out.println("Found " + countWords(stream) + " words");
  }

}
