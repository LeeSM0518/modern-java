package optional;

public class Insurance {

  private String name;

  public Insurance() {}

  public Insurance(String name) {
    this.name = name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Insurance{");
    sb.append("name='").append(name).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
