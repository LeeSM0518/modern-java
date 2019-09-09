package chapter06;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static chapter06.Type.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class Main {

  public enum CaloricLevel { DIET, NORMAL, FAT }

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

    Map<Type, List<Dish>> dishedByType =
        menu.stream().collect(groupingBy(Dish::getType));
    System.out.println(dishedByType);

    Map<CaloricLevel, List<Dish>> dishesByCaloricLevel =
        menu.stream().collect(
            groupingBy(dish -> {
              if (dish.getCalories() <= 400) return CaloricLevel.DIET;
              else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
              else return CaloricLevel.FAT;
            }));

    System.out.println(dishesByCaloricLevel);
  }

}
