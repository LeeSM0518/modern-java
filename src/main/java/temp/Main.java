package temp;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {

  public static void main(String[] args) {

    A a = new A();
    Supplier<String> c = String::new;

    Consumer<String> consumer = a::print;

    // (String s1, String s2) -> a.print(s1, s2);

    consumer.accept("안녕");
  }

}
