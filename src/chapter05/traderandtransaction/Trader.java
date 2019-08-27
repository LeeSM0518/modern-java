package chapter05.traderandtransaction;

public class Trader {

  private final String name;
  private final String city;

  public Trader(String name, String city) {
    this.name = name;
    this.city = city;
  }

  public String getName() {
    return name;
  }

  public String getCity() {
    return city;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Trader{");
    sb.append("name='").append(name).append('\'');
    sb.append(", city='").append(city).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
