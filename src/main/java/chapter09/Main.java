package chapter09;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Main {

  public static void main(String[] args) {

    List<Integer> list = new LinkedList<>();
    list.iterator();
//    Runnable r1 = new Runnable() {
//      @Override
//      public void run() {
//        System.out.println("hello");
//      }
//    };
//
//    Runnable r2 = () -> System.out.println("hello");

//    Map.of()

    List<Integer> list1 = new LinkedList<>();
//    list1.replaceAll();

    int a = 10;
    Runnable r1 = () -> {
//      int a = 2;  // 컴파일 에러
      System.out.println(a);
    };
    Runnable r2 = new Runnable() {
      @Override
      public void run() {
        int a = 2;  // 잘 동작
        System.out.println(a);
      }
    };
  }

}
