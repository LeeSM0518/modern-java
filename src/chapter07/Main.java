package chapter07;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Main {

  public static void main(String[] args) {
    printRunTime(() -> sequentialSum(1_000_000_0));
    printRunTime(() -> iterativeSum(1_000_000_0));
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
