package project2;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

// Car class
class Car extends Vehicle implements Rentable {
    private int seats;

    public Car(String brand, String model, double rentPerDay, int seats) {
        super(brand, model, rentPerDay);
        this.seats = seats;
    }

    @Override
    public void showDetails() {
        System.out.println("Car - " + getBrand() + " " + getModel() +
                " | Seats: " + seats +
                " | Rent/day: $" + getRentPerDay() +
                " | Available: " + (isAvailable() ? "Yes" : "No"));
    }

    @Override
    public void rent() {
        if (isAvailable()) {
            setAvailable(false);
            System.out.println(getBrand() + " " + getModel() + " has been rented successfully!");
        } else {
            System.out.println(getBrand() + " " + getModel() + " is already rented.");
        }
    }

    @Override
    public void returnVehicle() {
        if (!isAvailable()) {
            setAvailable(true);
            System.out.println(getBrand() + " " + getModel() + " has been returned successfully!");
        } else {
            System.out.println(getBrand() + " " + getModel() + " was not rented.");
        }
    }
}

// Bike class
class Bike extends Vehicle implements Rentable {
    private int engineCC;

    public Bike(String brand, String model, double rentPerDay, int engineCC) {
        super(brand, model, rentPerDay);
        this.engineCC = engineCC;
    }

    @Override
    public void showDetails() {
        System.out.println("Bike - " + getBrand() + " " + getModel() +
                " | Engine: " + engineCC + "cc" +
                " | Rent/day: $" + getRentPerDay() +
                " | Available: " + (isAvailable() ? "Yes" : "No"));
    }

    @Override
    public void rent() {
        if (isAvailable()) {
            setAvailable(false);
            System.out.println(getBrand() + " " + getModel() + " has been rented successfully!");
        } else {
            System.out.println(getBrand() + " " + getModel() + " is already rented.");
        }
    }

    @Override
    public void returnVehicle() {
        if (!isAvailable()) {
            setAvailable(true);
            System.out.println(getBrand() + " " + getModel() + " has been returned successfully!");
        } else {
            System.out.println(getBrand() + " " + getModel() + " was not rented.");
        }
    }
}

// Rental System Manager
class RentalSystem {
    private List<Vehicle> vehicles = new ArrayList<>();

    public void addVehicle(Vehicle v) {
        vehicles.add(v);
    }

    public void showAllVehicles() {
        System.out.println("\n--- Vehicle List ---");
        for (int i = 0; i < vehicles.size(); i++) {
            System.out.print((i + 1) + ". ");
            vehicles.get(i).showDetails();
        }
    }

    public void rentVehicle(int index) {
        if (index >= 0 && index < vehicles.size()) {
            Vehicle v = vehicles.get(index);
            ((Rentable) v).rent();
        } else {
            System.out.println("Invalid vehicle index!");
        }
    }

    public void returnVehicle(int index) {
        if (index >= 0 && index < vehicles.size()) {
            Vehicle v = vehicles.get(index);
            ((Rentable) v).returnVehicle();
        } else {
            System.out.println("Invalid vehicle index!");
        }
    }
}

// Main Class
public class Stage6 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RentalSystem system = new RentalSystem();

        // Adding sample vehicles
        system.addVehicle(new Car("Toyota", "Innova", 80.0, 7));
        system.addVehicle(new Bike("Royal Enfield", "Classic 350", 35.0, 350));
        system.addVehicle(new Car("Mahindra", "Thar", 120.0, 5));
        system.addVehicle(new Bike("Honda", "CBR 250R", 50.0, 250));

        int choice;
        do {
            System.out.println("\n==== Vehicle Rental Menu ====");
            System.out.println("1. View All Vehicles");
            System.out.println("2. Rent a Vehicle");
            System.out.println("3. Return a Vehicle");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    system.showAllVehicles();
                    break;
                case 2:
                    system.showAllVehicles();
                    System.out.print("Enter vehicle number to rent: ");
                    int rentChoice = sc.nextInt();
                    system.rentVehicle(rentChoice - 1);
                    break;
                case 3:
                    system.showAllVehicles();
                    System.out.print("Enter vehicle number to return: ");
                    int returnChoice = sc.nextInt();
                    system.returnVehicle(returnChoice - 1);
                    break;
                case 4:
                    System.out.println("Thank you for using the Vehicle Rental System!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again!");
            }
        } while (choice != 4);

        sc.close();
    }
}