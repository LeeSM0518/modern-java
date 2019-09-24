package chapter06;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;

public class Main {

  public enum CaloricLevel {DIET, NORMAL, FAT}

  public static void main(String[] args) {
    List<Dish> menu = Arrays.asList(
        new Dish("pork", false, 800, Type.MEAT),
        new Dish("beef", false, 700, Type.MEAT),
        new Dish("chicken", false, 400, Type.MEAT),
        new Dish("french fries", true, 530, Type.OTHER),
        new Dish("rice", true, 350, Type.OTHER),
        new Dish("season", true, 120, Type.OTHER),
        new Dish("pizza", true, 550, Type.OTHER),
        new Dish("prawns", false, 300, Type.FISH),
        new Dish("salmon", false, 450, Type.FISH)
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

    System.out.println();
    Map<Type, List<Dish>> caloricDishesByType =
        menu.stream().filter(dish -> dish.getCalories() > 500)
            .collect(groupingBy(Dish::getType));
    System.out.println(caloricDishesByType);

    Map<Type, List<Dish>> caloricDishesByType2 =
        menu.stream()
            .collect(groupingBy(Dish::getType,
                filtering(dish -> dish.getCalories() > 500, toList())));
    System.out.println(caloricDishesByType2);

    Map<Type, List<String>> dishNamesByType =
        menu.stream()
            .collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));
    System.out.println(dishNamesByType);

    Map<String, List<String>> dishTags = new HashMap<>();
    dishTags.put("pork", asList("greasy,", "salty"));
    dishTags.put("beef", asList("salty", "roasted"));
    dishTags.put("chicken", asList("fried", "crisp"));
    dishTags.put("french fries", asList("greasy", "fried"));
    dishTags.put("rice", asList("light", "natural"));
    dishTags.put("season", asList("fresh", "natural"));
    dishTags.put("pizza", asList("tasty", "salty"));
    dishTags.put("prawns", asList("tasty", "roasted"));
    dishTags.put("salmon", asList("delicious", "fresh"));

    Map<Type, Set<String>> dishNamesByType2 =
        menu.stream()
            .collect(groupingBy(Dish::getType,
                flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));
    System.out.println(dishNamesByType2);

    Map<Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel =
        menu.stream().collect(
            groupingBy(Dish::getType,
                groupingBy(dish -> {
                  if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                  else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                  else return CaloricLevel.FAT;
                })
            )
        );
    System.out.println();
    System.out.println(dishesByTypeCaloricLevel);

    Map<Type, Long> typesCount = menu.stream().collect(
        groupingBy(Dish::getType, counting()));
    System.out.println(typesCount);

    System.out.println();
    Map<Type, Optional<Dish>> mostCaloricByType =
        menu.stream()
            .collect(groupingBy(Dish::getType,
                maxBy(comparing(Dish::getCalories))));
    System.out.println(mostCaloricByType);

    System.out.println();
    Map<Type, Dish> mostCaloricByType2 =
        menu.stream()
            .collect(groupingBy(Dish::getType,
                collectingAndThen(
                    maxBy(comparing(Dish::getCalories)),
                    Optional::get)));
    System.out.println(mostCaloricByType2);

    System.out.println();
    Map<Type, Integer> totalCaloriesByType =
        menu.stream().collect(groupingBy(Dish::getType,
            summingInt(Dish::getCalories)));
    System.out.println(totalCaloriesByType);

    System.out.println();
    Map<Type, Set<CaloricLevel>> caloricLevelsByType =
        menu.stream().collect(
            groupingBy(Dish::getType, mapping(dish -> {
              if (dish.getCalories() <= 400) return CaloricLevel.DIET;
              else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
              else return CaloricLevel.FAT;
            }, toSet())));
    System.out.println(caloricLevelsByType);

    System.out.println();
    Map<Type, Set<CaloricLevel>> caloricLevelsByType2 =
        menu.stream().collect(
            groupingBy(Dish::getType, mapping(dish -> {
              if (dish.getCalories() <= 400) return CaloricLevel.DIET;
              else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
              else return CaloricLevel.FAT;
            }, toCollection(HashSet::new))));
    System.out.println(caloricLevelsByType2);

    System.out.println();
    Map<Boolean, List<Dish>> partitionedMenu =
        menu.stream().collect(partitioningBy(Dish::isVegetarian));
    System.out.println(partitionedMenu);

    System.out.println();
    Map<Boolean, Map<Type, List<Dish>>> vegetarianDishesByType =
        menu.stream().collect(
            partitioningBy(Dish::isVegetarian,
                groupingBy(Dish::getType)));
    System.out.println(vegetarianDishesByType);

    System.out.println();
    Map<Boolean, Dish> mostCaloricPartitionedByVegetarian =
        menu.stream().collect(
            partitioningBy(Dish::isVegetarian,
                collectingAndThen(maxBy(comparingInt(Dish::getCalories)),
                    Optional::get)));
    System.out.println(mostCaloricPartitionedByVegetarian);

    System.out.println();

    List<Dish> dishes = menu.stream()
        .collect(
            ArrayList::new,   // 발행
            List::add,        // 누적
            List::addAll);    // 합침

    System.out.println(dishes);

    long fastest = Long.MAX_VALUE;
    for (int i = 0; i < 10; i++) {
      long start = System.nanoTime();
//      partitionPrimesWithCustomCollector(1_000_000);
      long duration = (System.nanoTime() - start) / 1_000_000;
      if (duration < fastest) fastest = duration;
    }
    System.out.println("Fastest execution done in " + fastest + "msecs");
  }

  public static boolean isPrime(int candidate) {
    int candidateRoot = (int) Math.sqrt(candidate);
    return IntStream.range(2, candidateRoot)
        .noneMatch(i -> candidate % i == 0);
  }

  public static boolean isPrime(List<Integer> primes, int candidate) {
    int candidateRoot = (int) Math.sqrt(candidate);
    return primes.stream()
        .takeWhile(i -> i <= candidateRoot)
        .noneMatch(i -> candidate % i == 0);
  }

//  public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
////    return IntStream.rangeClosed(2, n).boxed()
////        .collect(new PrimeNumbersCollector());
//    return IntStream.rangeClosed(2, n).boxed()
//        .collect(
//            () -> new HashMap<>() {{
//              put(true, new ArrayList<>());
//              put(false, new ArrayList<>());
//            }},
//            (acc, candidate) -> {
//              acc.get(isPrime(acc.get(true), candidate)).add(candidate);
//            },
//            (map1, map2) -> {
//              map1.get(true).addAll(map2.get(true));
//              map1.get(false).addAll(map2.get(false));
//            });
//  }
//
//  public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
//    return IntStream.rangeClosed(2, n).boxed()
//        .collect(partitioningBy(Main::isPrime));
//  }


}
