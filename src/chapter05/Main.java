package chapter05;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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

    List<Dish> vegetarianMenu = menu.stream()
        .filter(Dish::isVegetarian)
        .collect(toList());
//    System.out.println(vegetarianMenu);

    List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
    numbers.stream()
        .filter(i -> i % 2 == 0)
        .distinct()
        .forEach(System.out::println);

    List<Dish> sliceMenu2 =
        menu.stream()
            .dropWhile(dish -> dish.getCalories() < 320)
            .collect(toList());
    System.out.println(sliceMenu2);

    List<Dish> dishes = menu.stream()
        .filter(dish -> dish.getCalories() > 300)
        .limit(3)
        .collect(toList());
    System.out.println(dishes);

    List<Dish> dishes1 = menu.stream()
        .filter(d -> d.getCalories() > 300)
        .skip(2)
        .collect(toList());
    System.out.println(dishes1);

    List<String> dishNames = menu.stream()
        .map(Dish::getName)
        .collect(toList());
    System.out.println(dishNames);

    List<String> words = Arrays.asList("Modern", "Java", "In", "Action");
    List<Integer> wordLengths = words.stream()
        .map(String::length)
        .collect(toList());
    System.out.println(wordLengths);

    List<Integer> dishNameLengths = menu.stream()
        .map(Dish::getName)
        .map(String::length)
        .collect(toList());
    System.out.println(dishNameLengths);

    String[] arrayOfWords = {"Goodbye", "World"};
    Stream<String> streamOfWords = Arrays.stream(arrayOfWords);
//    List<String> strings = streamOfWords
//        .map(word -> word.split(""))
//        .map(Arrays::stream)
//        .distinct()
//        .collect(toList());
    List<String> uniqueCharacters = streamOfWords
        .map(word -> word.split(""))
        .flatMap(Arrays::stream)
        .distinct()
        .collect(toList());
    System.out.println(uniqueCharacters);

    if (menu.stream().anyMatch(Dish::isVegetarian)) {
      System.out.println("The menu is (somewhat) vegetarian friendly!!");
    }

//    boolean isHealthy = menu.stream()
//        .allMatch(dish -> dish.getCalories() < 1000);
//    System.out.println(isHealthy);

    boolean isHealthy = menu.stream()
        .noneMatch(d -> d.getCalories() >= 1000);
    System.out.println(isHealthy);

    menu.stream()
        .filter(Dish::isVegetarian)
        .findAny()
        .ifPresent(dish -> System.out.println(dish.getName()));

    List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
    someNumbers.stream()
        .map(n -> n * n)
        .filter(n -> n % 3 == 0)
        .findFirst()
        .ifPresent(System.out::println);

    numbers.stream().reduce(Integer::sum).ifPresent(System.out::println);

    Optional<Integer> min = numbers.stream().reduce(Integer::min);
    System.out.println(min);
    Optional<Integer> max = numbers.stream().reduce(Integer::max);

    int calories = menu.stream()
        .map(Dish::getCalories)
        .reduce(0, Integer::sum);
    System.out.println(calories);

    int calories2 = menu.stream()
        .mapToInt(Dish::getCalories)
        .sum();
    System.out.println(calories2);

    IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
    Stream<Integer> stream = intStream.boxed();

    OptionalInt maxCalories = menu.stream()
        .mapToInt(Dish::getCalories)
        .max();
    System.out.println();

    IntStream evenNumbers = IntStream.rangeClosed(1, 100)
        .filter(n -> n % 2 == 0);
    System.out.println(evenNumbers.count());

    Stream<int[]> pythagoreanTriples =
        IntStream.rangeClosed(1, 100).boxed()
            .flatMap(a ->
                IntStream.rangeClosed(a, 100)
                    .filter(b -> Math.sqrt(a * a + b * b) % 1 == 0)
                    .mapToObj(b ->
                        new int[]{a, b, (int) Math.sqrt(a * a + b * b)}));

    pythagoreanTriples.limit(5)
        .forEach(t ->
            System.out.println(t[0] + ", " + t[1] + ", " + t[2]));

    Stream<double[]> pythagoreanTriples2 =
        IntStream.rangeClosed(1, 100).boxed()
            .flatMap(a -> IntStream.rangeClosed(a, 100)
                .mapToObj(
                    b -> new double[]{a, b, Math.sqrt(a * a + b * b)})
                .filter(t -> t[2] % 1 == 0));

    Stream<String> stringStream = Stream.of("Modern ", "Java ", "In ", "Action");
    stringStream.map(String::toUpperCase).forEach(System.out::println);

    Stream.iterate(0, n -> n + 2)
        .limit(3)
        .forEach(System.out::println);

    IntStream.iterate(0, n -> n < 100, n -> n + 4)
        .forEach(System.out::println);

    IntStream.iterate(0, n -> n + 4)
        .takeWhile(n -> n < 100)
        .forEach(System.out::println);

    Stream.generate(Math::random)
        .limit(5)
        .forEach(System.out::println);

    IntSupplier fib = new IntSupplier() {
      private int previous = 0;
      private int current = 1;
      @Override
      public int getAsInt() {
        int oldPrevious = this.previous;
        int nextValue = this.previous + this.current;
        this.previous = this.current;
        this.current = nextValue;
        return oldPrevious;
      }
    };

    IntStream.generate(fib).limit(10).forEach(System.out::println);
  }

}
