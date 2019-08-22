package optional;

import java.util.Optional;

public class Car {

  private Optional<Insurance> insurance;

  public Car() {}

  public Car(Optional<Insurance> insurance) {
    this.insurance = insurance;
  }

  public void setInsurance(Optional<Insurance> insurance) {
    this.insurance = insurance;
  }

  public Optional<Insurance> getInsurance() {
    return insurance;
  }

}
