package chapter07;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(jvmArgs={"-Xms4G", "-Xm4G"})
public class ParallelStreamBenchmark {

  private static final long N = 10_000_000L;

  @Benchmark
  public long sequentialSum() {
    return Stream.iterate(1L, i -> i + 1)
        .limit(N)
        .reduce(0L, Long::sum);
  }

  @TearDown(Level.Invocation)
  public void tearDown() {
    System.gc();
  }

  public static void main(String[] args) {
    ParallelStreamBenchmark ps = new ParallelStreamBenchmark();
    ps.sequentialSum();
  }

}
