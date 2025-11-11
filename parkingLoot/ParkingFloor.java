package parkingLoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

 class ParkingFloor {
    private final String floorId;
    final List<ParkingSpot> spots = new ArrayList<>();

    public ParkingFloor(String floorId) { this.floorId = floorId; }

    public void addSpot(ParkingSpot s) { spots.add(s); }
    public List<ParkingSpot> getSpots() { return Collections.unmodifiableList(spots); }
    public String getFloorId() { return floorId; }
}
