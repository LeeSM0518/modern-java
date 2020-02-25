package chapter03;

import chapter02.Apple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import static chapter02.Color.GREEN;
import static chapter02.Color.RED;

public class Main {

  public static void main(String[] args) {
    List<Apple> inventory = List.of(new Apple(GREEN, 100), new Apple(RED, 150));
    List<Apple> greenApples = filter(inventory, (Apple a) -> GREEN.equals(a.getColor()));
    List<Apple> greenApples2 = filter(inventory, (Apple a) -> GREEN.equals(a.getColor()));
    System.out.println(greenApples);
    String a = "awdwd";
//    print(a, a::length);
  }

  public static <T> List<T> filter(List<T> list, Predicate<T> p) {
    List<T> result = new ArrayList<>();
    for(T e : list) {
      if (p.test(e)) {
        result.add(e);
      }
    }
    return result;
  }

  public static void print(String a, Function<String, Integer> c) {
    System.out.println(c.apply(a));
  }

}