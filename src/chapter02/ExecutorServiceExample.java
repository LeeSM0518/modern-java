package chapter02;

import java.util.concurrent.*;

public class ExecutorServiceExample {

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    ExecutorService executorService = Executors.newCachedThreadPool();

    Future<String> threadName1 = executorService.submit(new Callable<String>() {
      @Override
      public String call() throws Exception {
        return Thread.currentThread().getName();
      }
    });

    Future<String> threadName2 = executorService.submit(
        () -> Thread.currentThread().getName());

    System.out.println(threadName1.get());
    System.out.println(threadName2.get());
  }

}
