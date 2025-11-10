package parkingLoot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

class Gate {
    String gateId;
    GateType type;

    public Gate(String gateId, GateType type) {
        this.gateId = gateId;
    }
}

enum GateType {
    ENTRY, EXIT
}

class EntryGate extends Gate {
    private ParkingManager parkingManager;

    public EntryGate(String gateId, ParkingManager parkingManager) {
        super(gateId, GateType.ENTRY);
        this.parkingManager = parkingManager;
    }

    Ticket processEntry(Vehicle vehicle) {
        Ticket ticket = parkingManager.parkVehicle(vehicle);
        return ticket;
    }
}

class ExitGate extends Gate {
    private ParkingManager parkingManager;

    public ExitGate(String gateId, ParkingManager parkingManager) {
        super(gateId, GateType.EXIT);
        this.parkingManager = parkingManager;
    }

    double processExit(Ticket ticket) {
        long duration = Duration.between((Temporal) ticket.entryTime, LocalDateTime.now()).toHours();
        PricingStrategy ps = PricingStrategyFactory.getStrategy(ticket.vehicle.type);
        double amount = ps.calculateCharges(duration, ticket.vehicle);
        parkingManager.freeSpot(ticket);
        return amount;
    }
}

class Vehicle {
    String licensePlate;
    VehicleType type;
}

enum VehicleType {
    CAR, BIKE, TRUCK
}

class Ticket {
    String ticketId;
    Date entryTime;
    ParkingSpot spot;
    Vehicle vehicle;
    // Gate entryGate;
}

class BikePricingStrategy implements PricingStrategy {

    int baseRate = 10; // per hour

    @Override
    public double calculateCharges(long duration, Vehicle vehicle) {
        return baseRate * duration;
    }

}

class CarPricingStrategy implements PricingStrategy {

    int baseRate = 20; // per hour

    @Override
    public double calculateCharges(long duration, Vehicle vehicle) {
        return baseRate * duration;
    }

}

class TruckPricingStrategy implements PricingStrategy {

    int baseRate = 50; // per hour

    @Override
    public double calculateCharges(long duration, Vehicle vehicle) {
        return baseRate * duration;
    }

}

interface PricingStrategy {
    double calculateCharges(long duration, Vehicle vehicle);
}

class PricingStrategyFactory {
    static PricingStrategy getStrategy(VehicleType type) {
        switch (type) {
            case CAR:
                return new CarPricingStrategy();
            case BIKE:
                return new BikePricingStrategy();
            case TRUCK:
                return new TruckPricingStrategy();
            default:
                throw new IllegalArgumentException("Unknown type");
        }
    }
}

class ParkingManager {
    private List<ParkingFloor> floors;

    public ParkingManager(List<ParkingFloor> floors) {
        this.floors = floors;
    }

    public ParkingSpot findSpot(Vehicle vehicle) {
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.spots) {
                if (spot.isFree && spot.allowedType == vehicle.type) {
                    return spot;
                }
            }
        }
        return null;
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = findSpot(vehicle);
        if (spot == null) {
            throw new RuntimeException("No Spot Available");
        }

        spot.isFree = false;
        spot.parkedVehicle = vehicle;

        // return new Ticket(
        // UUID.randomUUID().toString(),
        // LocalDateTime.now(),
        // spot,
        // vehicle
        // );
        return new Ticket();
    }

    public void freeSpot(Ticket ticket) {
        ParkingSpot spot = ticket.spot;
        spot.isFree = true;
        spot.parkedVehicle = null;
    }

    public List<ParkingSpot> getAvailableSpots(VehicleType type) {

        List<ParkingSpot> list = new ArrayList<ParkingSpot>();
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.spots) {
                if (spot.isFree && spot.allowedType == type) {
                    list.add(spot);
                }
            }
        }
        return list;
    }

}

class PaymentProcessor {
    void processPayment(String ticketId, double amount) {
        System.out.println("Processing payment of $" + amount + " for Ticket ID: " + ticketId);
    }

}

class ParkingLot {
    private final String name;
    private final List<ParkingFloor> floors = new ArrayList<>();
    private final ParkingManager manager = new ParkingManager(floors);

    public ParkingLot(String name) {
        this.name = name;
    }

    public void addFloor(ParkingFloor f) {
        floors.add(f);
    }

    public List<ParkingSpot> getAvailableSpotsByType(VehicleType t) {
        return manager.getAvailableSpots(t);
    }

    public EntryGate newEntryGate(String id) {
        return new EntryGate(id, manager);
    }

    public ExitGate newExitGate(String id) {
        return new ExitGate(id, manager);
    }
}

public class Parking {

    public static void main(String[] args) {
        System.out.println("Welcome to ParkingLoot System");
        Vehicle vehicle = new Vehicle();

    }
}
