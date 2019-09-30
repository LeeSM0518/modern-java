package chapter02;

public class Apple {

  private Color color;
  private int weight;

  public Apple(Color color, int weight) {
    this.color = color;
    this.weight = weight;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Apple{");
    sb.append("color=").append(color);
    sb.append(", weight=").append(weight);
    sb.append('}');
    return sb.toString();
  }

}
