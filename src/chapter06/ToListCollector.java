package chapter06;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {

  @Override
  public Supplier<List<T>> supplier() {
    return ArrayList::new;
  }

  @Override
  public BiConsumer<List<T>, T> accumulator() {
    return null;
  }

  @Override
  public BinaryOperator<List<T>> combiner() {
    return null;
  }

  @Override
  public Function<List<T>, List<T>> finisher() {
    return null;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return null;
  }

}