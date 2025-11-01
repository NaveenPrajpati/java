package abstraction;

// Stage 4  Abstraction

abstract class Vehicle {
    protected String brand;
    protected int speed;

    public Vehicle(String brand, int speed) {
        this.brand = brand;
        this.speed = speed;
    }

    public abstract void startEngine();

    public void displayDetails() {
        System.out.println("Brand: " + brand);
        System.out.println("Speed: " + speed);
    }
}

interface FuelType {
    void fuelUsed();
}

class Car extends Vehicle implements FuelType {

    public Car(String brand, int speed) {
        super(brand, speed);
    }

    @Override
    public void startEngine() {
        System.out.println("Car engine started");
    }

    @Override
    public void fuelUsed() {
        System.out.println("Diesel used in car");
    }
}

class Bike extends Vehicle implements FuelType {

    public Bike(String brand, int speed) {
        super(brand, speed);
    }

    @Override
    public void startEngine() {
        System.out.println("Bike engine started");
    }

    @Override
    public void fuelUsed() {
        System.out.println("Petrol used in bike");
    }
}

public class Stage4 {
    public static void main(String[] args) {
        Vehicle car = new Car("Mahindra", 160);
        Vehicle bike = new Bike("RE", 160);
        FuelType carFuel = new Car("Mahindra", 160);

        System.out.println("=== Car Details ===");
        car.startEngine();
        car.displayDetails();
        carFuel.fuelUsed();

        System.out.println("\n=== Bike Details ===");
        bike.startEngine();
        bike.displayDetails();
        ((FuelType) bike).fuelUsed();

    }
}
