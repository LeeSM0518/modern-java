package chapter07;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class Main {

  public static void main(String[] args) {
    final String SENTENCE = "There is no requirement that a new or distinct result be returned each " +
        "time the supplier is invoked.";
    System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");
  }

  public static int countWordsIteratively(String s) {
    int counter = 0;
    boolean lastSpace = true;
    for (char c : s.toCharArray()) {
      if (Character.isWhitespace(c)) {
        lastSpace = true;
      } else {
        if (lastSpace) counter++;
        lastSpace = false;
      }
    }
    return counter;
  }

  public static long sequentialSum(long n) {
    return Stream.iterate(1L, i -> i + 1)
        .limit(n)
        .reduce(0L, Long::sum);
  }

  public static long iterativeSum(long n) {
    long result = 0;
    for (long i = 1L; i <= n; i++) {
      result += i;
    }
    return result;
  }

  public static long parallelSum(long n) {
    return Stream.iterate(1L, i -> i + 1)
        .limit(n)
        .parallel()
        .reduce(0L, Long::sum);
  }

  public static void printRunTime(Supplier supplier) {
    long beforeTime = System.currentTimeMillis();
    supplier.get();
    long afterTime = System.nanoTime();
    System.out.println("run time : " + (afterTime - beforeTime));
  }

}
