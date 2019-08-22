package optional;

import javax.swing.text.html.Option;
import java.util.*;

import static java.util.stream.Collectors.toSet;

public class Main {

  public static void main(String[] args) {
    Insurance insurance = new Insurance();
    insurance.setName("보험회사");
//    Insurance insurance = null;
    Optional<Insurance> optionalInsurance = Optional.ofNullable(insurance);
    Optional<String> name = optionalInsurance.map(Insurance::getName);
    System.out.println(name);

    Person person = new Person(Optional.of(new Car(Optional.of(new Insurance("보험회사1")))));
    Person person2 = new Person(Optional.of(new Car(Optional.of(new Insurance("보험회사2")))));
    Person person3 = new Person(Optional.of(new Car(Optional.of(new Insurance("보험회사3")))));
    Person person4 = new Person(Optional.of(new Car(Optional.of(new Insurance("보험회사4")))));

    Optional<Person> optionalPerson3 = Optional.of(person3);
    System.out.println(getCarInsuranceName(optionalPerson3));

    List<Person> people = new ArrayList<>(Arrays.asList(person, person2, person3, person4));

    Set<String> set = getCarInsuranceNames(people);
    System.out.println(set);

    System.out.println(nullSafeFindCheapestInsurance(Optional.ofNullable(null), Optional.ofNullable(null)));
  }

  static public String getCarInsuranceName(Optional<Person> person) {
    return person.flatMap(Person::getCar)
        .flatMap(Car::getInsurance)
        .map(Insurance::getName)
        .orElse("Unknown");
  }

  static Set<String> getCarInsuranceNames(List<Person> persons) {
    return persons.stream()
        .map(Person::getCar)
        .map(optCar -> optCar.flatMap(Car::getInsurance))
        .map(optIns -> optIns.map(Insurance::getName))
        .flatMap(Optional::stream)
        .collect(toSet());
  }

  static public Insurance findCheapestInsurance(Person person, Car car) {
    return new Insurance("회사");
  }

  static Optional<Insurance> nullSafeFindCheapestInsurance(
      Optional<Person> person, Optional<Car> car) {
    return person.flatMap(p -> car.map(c -> findCheapestInsurance(person.get(), car.get())));
  }

}
