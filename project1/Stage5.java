package project1;

import java.util.ArrayList;
import java.util.List;

// Abstract Class
abstract class Vehicle {
    private String brand;
    private String model;
    private double rentPerDay;
    private boolean isAvailable = true;

    public Vehicle(String brand, String model, double rentPerDay) {
        this.brand = brand;
        this.model = model;
        this.rentPerDay = rentPerDay;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public double getRentPerDay() {
        return rentPerDay;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public abstract void showDetails();
}

// Interface
interface Rentable {
    void rent();

    void returnVehicle();
}

// Concrete class: Car
class Car extends Vehicle implements Rentable {
    private int seats;

    public Car(String brand, String model, double rentPerDay, int seats) {
        super(brand, model, rentPerDay);
        this.seats = seats;
    }

    @Override
    public void showDetails() {
        System.out.println(
                "Car - " + getBrand() + " " + getModel() + " | Seats: " + seats + " | Rent/day: $" + getRentPerDay());
    }

    @Override
    public void rent() {
        if (isAvailable()) {
            setAvailable(false);
            System.out.println(getBrand() + " " + getModel() + " has been rented.");
        } else {
            System.out.println(getBrand() + " " + getModel() + " is already rented.");
        }
    }

    @Override
    public void returnVehicle() {
        if (!isAvailable()) {
            setAvailable(true);
            System.out.println(getBrand() + " " + getModel() + " has been returned.");
        } else {
            System.out.println(getBrand() + " " + getModel() + " was not rented.");
        }
    }
}

// Concrete class: Bike
class Bike extends Vehicle implements Rentable {
    private int engineCC;

    public Bike(String brand, String model, double rentPerDay, int engineCC) {
        super(brand, model, rentPerDay);
        this.engineCC = engineCC;
    }

    @Override
    public void showDetails() {
        System.out.println("Bike - " + getBrand() + " " + getModel() + " | Engine: " + engineCC + "cc | Rent/day: $"
                + getRentPerDay());
    }

    @Override
    public void rent() {
        if (isAvailable()) {
            setAvailable(false);
            System.out.println(getBrand() + " " + getModel() + " has been rented.");
        } else {
            System.out.println(getBrand() + " " + getModel() + " is already rented.");
        }
    }

    @Override
    public void returnVehicle() {
        if (!isAvailable()) {
            setAvailable(true);
            System.out.println(getBrand() + " " + getModel() + " has been returned.");
        } else {
            System.out.println(getBrand() + " " + getModel() + " was not rented.");
        }
    }
}

// Manager class
class RentalSystem {
    private List<Vehicle> vehicles = new ArrayList<>();

    public void addVehicle(Vehicle v) {
        vehicles.add(v);
    }

    public void showAvailableVehicles() {
        System.out.println("\nAvailable Vehicles:");
        for (Vehicle v : vehicles) {
            if (v.isAvailable())
                v.showDetails();
        }
    }
}

// Main class
public class Stage5 {
    public static void main(String[] args) {
        RentalSystem system = new RentalSystem();

        Car car1 = new Car("Toyota", "Innova", 80.0, 7);
        Bike bike1 = new Bike("Royal Enfield", "Classic 350", 35.0, 350);

        system.addVehicle(car1);
        system.addVehicle(bike1);

        system.showAvailableVehicles();

        System.out.println("\n--- Renting Vehicle ---");
        car1.rent();

        system.showAvailableVehicles();

        System.out.println("\n--- Returning Vehicle ---");
        car1.returnVehicle();

        system.showAvailableVehicles();
    }
}

// Encapsulation - Private fields in Vehicle with getters/setters
// Inheritance - Car and Bike extend Vehicle
// Polymorphism - Common method showDetails() and rent() used differently in
// each subclass
// Abstraction - Abstract class Vehicle and interface Rentable
// Composition - RentalSystem manages a list of Vehicle objects
