package chapter05.traderandtransaction;

public class Transaction {

  private final Trader trader;
  private final int year;
  private final int value;

  public Transaction(Trader trader, int year, int value) {
    this.trader = trader;
    this.year = year;
    this.value = value;
  }

  public Trader getTrader() {
    return trader;
  }

  public int getYear() {
    return year;
  }

  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Transaction{");
    sb.append("trader=").append(trader);
    sb.append(", year=").append(year);
    sb.append(", value=").append(value);
    sb.append('}');
    return sb.toString();
  }
}
