package chapter08;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

  public static void main(String[] args) {
//    List<String> friends = new ArrayList<>();
//    friends.add("Raphael");
//    friends.add("Olivia");
//    friends.add("Thibaut");
//    List<String> friends = Arrays.asList("Raphael", "Olivia", "Thibaut");
    List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
    System.out.println(friends);
  }

}
