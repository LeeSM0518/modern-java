package chapter04;

import static java.util.stream.Collectors.toList;
import java.util.Arrays;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    List<Dish> menu = Arrays.asList(
      new Dish("pork", false, 800, Type.MEAT),
      new Dish("beef", false, 700, Type.MEAT),
      new Dish("chicken", false, 400, Type.MEAT),
      new Dish("french fries", true, 530, Type.MEAT),
      new Dish("rice", true, 350, Type.MEAT),
      new Dish("season", true, 120, Type.MEAT),
      new Dish("pizza", true, 550, Type.MEAT),
      new Dish("prawns", false, 300, Type.MEAT),
      new Dish("salmon", false, 450, Type.MEAT)
    );

    List<String> names =
        menu.stream()
        .filter(dish -> {
          System.out.println("filtering: " + dish.getName());
          return dish.getCalories() > 300;
        })
        .map(dish -> {
          System.out.println("mapping: " + dish.getName());
          return dish.getName();
        })
        .limit(3)
        .collect(toList());

    System.out.println(names);

    menu.stream().forEach(System.out::println);
  }

}
