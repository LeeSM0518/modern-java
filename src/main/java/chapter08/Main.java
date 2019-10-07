package chapter08;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Map.entry;

public class Main {

  public static void main(String[] args) {
//    List<String> friends = new ArrayList<>();
//    friends.add("Raphael");
//    friends.add("Olivia");
//    friends.add("Thibaut");
//    List<String> friends = Arrays.asList("Raphael", "Olivia", "Thibaut");
//    List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
//    System.out.println(friends);
//    Set<String> friends = Set.of("Raphael", "Olivia", "Thibaut");
//    System.out.println(friends);
//    Map<String, Integer> ageOfFriends
//        = Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
//    System.out.println(ageOfFriends);

//    Map<String, Integer> ageOfFriends =
//        Map.ofEntries(entry("Raphael", 30),
//            entry("Olivia", 25),
//            entry("Thibaut", 26));
//    System.out.println(ageOfFriends);

//    Map<String, String> favouriteMovies
//        = Map.ofEntries(entry("Raphael", "Star Wars"),
//        entry("Cristina", "Matrix"),
//        entry("Olivia", "James Bond"));
//
//    favouriteMovies
//        .entrySet()
//        .stream()
//        .sorted(Map.Entry.comparingByKey())
//        .forEachOrdered(System.out::println);
//    Map<String, String> favouriteMovies
//        = Map.ofEntries(entry("Raphael", "Star Wars"),
//        entry("Olivia", "James Bond"));
//
//    System.out.println(favouriteMovies.getOrDefault("Olivia", "Matrix"));
//    System.out.println(favouriteMovies.getOrDefault("Thibaut", "Matrix"));
//    Map<String, byte[]> dataToHash = new HashMap<>();
//    try {
//      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
//    } catch (NoSuchAlgorithmException e) {
//      e.printStackTrace();
//    }

//    String friend = "Raphael";

//    Map<String, String> family = Map.ofEntries(
//        entry("Teo", "Star Wars"), entry("Cristina", "James Bond"));
//    Map<String, String> friends = Map.ofEntries(
//        entry("Raphael", "Star Wars"));
//    Map<String, String> everyone = new HashMap<>(family);
//    everyone.putAll(friends);
//    System.out.println(everyone);

//    Map<String, String> family = Map.ofEntries(
//        entry("Teo", "Star Wars"), entry("Cristina", "James Bond"));
//    Map<String, String> friends = Map.ofEntries(
//        entry("Raphael", "Star Wars"), entry("Cristina", "Matrix"));
//    Map<String, String> everyone = new HashMap<>(family);
//    friends.forEach((k, v) ->
//        everyone.merge(k, v, (movie1, movie2) -> movie1 + " & " + movie2));
//    System.out.println(everyone);

//    Map<String, Long> moviesToCount = new HashMap<>();
//    String movieName = "JamesBond";
//    Long count = moviesToCount.get(movieName);
//    if (count == null) {
//      moviesToCount.put(movieName, 1L);
//    } else {
//      moviesToCount.put(movieName, count + 1);
//    }
//    moviesToCount.merge(movieName, 1L, (key, count) -> count + 1L);
    ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
    long parallelismThreshold = 1;
    Optional<Long> maxValue =
        Optional.ofNullable(map.reduceValues(parallelismThreshold, Long::max));
  }

}
