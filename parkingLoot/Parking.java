package parkingLoot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

abstract class Gate {
    protected final String gateId;
    protected final GateType type;

    protected Gate(String gateId, GateType type) {
        this.gateId = gateId;
        this.type = type;
    }

    public String getGateId() {
        return gateId;
    }

    public GateType getType() {
        return type;
    }
}

enum GateType {
    ENTRY, EXIT
}

class EntryGate extends Gate {
    private final ParkingManager manager;

    public EntryGate(String gateId, ParkingManager manager) {
        super(gateId, GateType.ENTRY);
        this.manager = manager;
    }

    public Ticket processEntry(Vehicle v) {
        return manager.parkVehicle(v);
    }
}

class ExitGate extends Gate {
    private final ParkingManager manager;

    public ExitGate(String gateId, ParkingManager manager) {
        super(gateId, GateType.EXIT);
        this.manager = manager;
    }

    public double processExit(Ticket t) {
        long hours = Duration.between(t.getEntryTime(), LocalDateTime.now()).toHours();
        PricingStrategy ps = PricingStrategyFactory.getStrategy(t.getVehicle().getType());
        double amount = ps.calculateCharges(Math.max(1, hours), t.getVehicle());
        manager.freeSpot(t);
        return amount;
    }
}

class Vehicle {
    private final String licensePlate;
    private final VehicleType type;

    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getType() {
        return type;
    }
}

enum VehicleType {
    CAR, BIKE, TRUCK
}

class Ticket {
    private final String ticketId;
    private final LocalDateTime entryTime;
    private final ParkingSpot spot;
    private final Vehicle vehicle;

    public Ticket(String ticketId, LocalDateTime entryTime, ParkingSpot spot, Vehicle vehicle) {
        this.ticketId = ticketId;
        this.entryTime = entryTime;
        this.spot = spot;
        this.vehicle = vehicle;
    }

    public String getTicketId() {
        return ticketId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}

class BikePricingStrategy implements PricingStrategy {
    private final int baseRatePerHour = 5;

    public double calculateCharges(long durationHours, Vehicle vehicle) {
        return baseRatePerHour * Math.max(1, durationHours);
    }
}

class CarPricingStrategy implements PricingStrategy {
    private final int baseRatePerHour = 10;

    public double calculateCharges(long durationHours, Vehicle vehicle) {
        return baseRatePerHour * Math.max(1, durationHours);
    }
}

class TruckPricingStrategy implements PricingStrategy {
    private final int baseRatePerHour = 20;

    public double calculateCharges(long durationHours, Vehicle vehicle) {
        return baseRatePerHour * Math.max(1, durationHours);
    }
}

interface PricingStrategy {
    double calculateCharges(long duration, Vehicle vehicle);
}

class PricingStrategyFactory {
    public static PricingStrategy getStrategy(VehicleType type) {
        switch (type) {
            case CAR:
                return new CarPricingStrategy();
            case BIKE:
                return new BikePricingStrategy();
            case TRUCK:
                return new TruckPricingStrategy();
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }
}

class ParkingManager {
    private final List<ParkingFloor> floors;

    public ParkingManager(List<ParkingFloor> floors) {
        this.floors = Objects.requireNonNull(floors);
    }

    public ParkingSpot findSpot(Vehicle v) {
        for (ParkingFloor f : floors) {
            for (ParkingSpot s : f.getSpots()) {
                if (s.isFree() && s.getAllowedType() == v.getType())
                    return s;
            }
        }
        return null;
    }

    public Ticket parkVehicle(Vehicle v) {
        ParkingSpot s = findSpot(v);
        if (s == null)
            throw new IllegalStateException("No spot available for: " + v.getType());
        s.park(v);
        return new Ticket(UUID.randomUUID().toString(), LocalDateTime.now(), s, v);
    }

    public void freeSpot(Ticket t) {
        t.getSpot().free();
    }

    public List<ParkingSpot> getAvailableSpots(VehicleType type) {
        List<ParkingSpot> res = new ArrayList<>();
        for (ParkingFloor f : floors) {
            for (ParkingSpot s : f.getSpots()) {
                if (s.isFree() && s.getAllowedType() == type)
                    res.add(s);
            }
        }
        return res;
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
        ParkingLot lot = new ParkingLot("NeoLot");

        ParkingFloor f1 = new ParkingFloor("F1");
        f1.addSpot(new ParkingSpot("F1-S1", VehicleType.CAR));
        f1.addSpot(new ParkingSpot("F1-S2", VehicleType.BIKE));
        f1.addSpot(new ParkingSpot("F1-S3", VehicleType.TRUCK));
        lot.addFloor(f1);

        EntryGate eg = lot.newEntryGate("E1");
        ExitGate xg = lot.newExitGate("X1");

        Vehicle v = new Vehicle("KA-01-1234", VehicleType.CAR);
        Ticket t = eg.processEntry(v);
        System.out.println("Ticket: " + t.getTicketId());

        // simulate time passing (in real tests, inject a Clock)
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        double pay = xg.processExit(t);
        System.out.println("Amount due: " + pay);

    }
}
