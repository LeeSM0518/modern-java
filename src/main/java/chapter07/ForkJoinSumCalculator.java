package chapter07;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

// RecursiveTask 를 상속받아 포크/조인 프레임워크에서 사용할
// 태스크를 생성한다.
public class ForkJoinSumCalculator extends java.util.concurrent.RecursiveTask<Long> {

  private final long[] numbers;  // 더할 숫자 배열
  private final int start;       // 이 서브태스크에서 처리할 배열의 초기 위치
  private final int end;         // 최종 위치
  public static final long THRESHOLD = 10_000;  // 이 값 이하의 서브태스크는 더 이상 분할X

  // 메인 태스크를 생성할 때 사용할 공개 생성자
  public ForkJoinSumCalculator(long[] numbers) {
    this(numbers, 0, numbers.length);
  }

  // 메인 태스크의 서브태스크를 재귀적으로 만들 때 사용할 비공개 생성자
  private ForkJoinSumCalculator(long[] numbers, int start, int end) {
    this.numbers = numbers;
    this.start = start;
    this.end = end;
  }

  // RecursiveTask 의 추상 메서드 오버라이드
  @Override
  protected Long compute() {
    int length = end - start;       // 이 태스크에서 더할 배열의 길이

    if (length <= THRESHOLD) {
      // 기준값과 같거나 작으면 순차적으로 결과를 계산한다.
      return computeSequentially();
    }

    // 배열의 첫 번째 절반을 더하도록 서브태스크를 생성한다.
    ForkJoinSumCalculator leftTask =
        new ForkJoinSumCalculator(numbers, start, start + length/2);
    // ForkJoinPool 의 다른 스레드로 새로 생성한 태스크를 비동기로 실행
    leftTask.fork();

    // 배열의 나머지 절반을 더하도록 서브태스크를 생성
    ForkJoinSumCalculator rightTask =
        new ForkJoinSumCalculator(numbers, start + length/2, end);
    // 두 번째 서브태스크를 동기 실행한다.
    // 이때 추가로 분할이 일어날 수 있다.
    Long rightResult = rightTask.compute();
    // 첫 번째 서브태스크의 결과를 읽거나
    // 아직 결과가 없으면 기다린다.
    Long leftResult = leftTask.join();

    // 두 서브태스크의 결과를 조합한 값이 태스크의 결과다
    return leftResult + rightResult;
  }

  // 더 분할할 수 없을 때 서브태스크의 결과를 계산
  private long computeSequentially() {
    long sum = 0;
    for (int i = start; i < end; i++) {
      sum += numbers[i];
    }
    return sum;
  }

  public static long forkJoinSum(long n) {
    long[] numbers = LongStream.rangeClosed(1, n).toArray();
    ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);

    try{

    }catch (Exception e) {
      e.printStackTrace();
    }

    return new ForkJoinPool().invoke(task);
  }

}