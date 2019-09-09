package chapter05.traderandtransaction;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class Main {

  public static void main(String[] args) {
    Trader raoul = new Trader("Raoul", "Cambridge");
    Trader mario = new Trader("Mario", "Milan");
    Trader alan = new Trader("Alan", "Cambridge");
    Trader brian = new Trader("Brian", "Cambridge");

    List<Transaction> transactions = Arrays.asList(
        new Transaction(brian, 2011, 300),
        new Transaction(raoul, 2012, 1000),
        new Transaction(raoul, 2011, 400),
        new Transaction(mario, 2012, 710),
        new Transaction(mario, 2012, 700),
        new Transaction(alan, 2012, 950)
    );

    List<Transaction> transactionList
        = transactions.stream()
        .filter(transaction -> transaction.getYear() == 2011)
        .sorted(comparing(Transaction::getValue))
        .collect(toList());
    System.out.println(transactionList);

    List<String> cityList = transactions.stream()
        .map(transaction -> transaction.getTrader().getCity())
        .distinct()
        .collect(toList());
    System.out.println(cityList);

    Set<String> cities = transactions.stream()
        .map(transaction -> transaction.getTrader().getCity())
        .collect(Collectors.toSet());
    System.out.println(cities);

    List<Trader> nameList = transactions.stream()
        .map(Transaction::getTrader)
        .filter(trader -> trader.getCity().equals("Cambridge"))
        .distinct()
        .sorted(comparing(Trader::getName))
        .collect(toList());
    System.out.println(nameList);

    String traderStr = transactions.stream()
        .map(transaction -> transaction.getTrader().getName())
        .distinct()
        .sorted()
        .reduce("", (n1, n2) -> n1 + n2);
    System.out.println(traderStr);

    String traderStr2 = transactions.stream()
        .map(transaction -> transaction.getTrader().getName())
        .distinct()
        .sorted()
        .collect(Collectors.joining());

    boolean milanBased = transactions.stream()
        .anyMatch(transaction -> transaction.getTrader().getCity().equals("Milan"));
    System.out.println(milanBased);

    transactions.stream()
        .filter(transaction -> "Cambridge".equals(transaction.getTrader().getCity()))
        .map(Transaction::getValue)
        .forEach(System.out::println);

    Optional<Integer> max = transactions.stream()
        .map(Transaction::getValue)
        .reduce(Integer::max);
    System.out.println(max);

    Optional<Transaction> smallestTransaction = transactions.stream()
        .reduce((t1, t2) ->
            t1.getValue() < t2.getValue() ? t1 : t2);
    System.out.println(smallestTransaction);

    Optional<Transaction> smallestTransaction2 = transactions.stream()
        .min(comparing(Transaction::getValue));
    System.out.println(smallestTransaction2);

    int a = IntStream.range(1,2).sum();
    System.out.println(a);
  }

}
