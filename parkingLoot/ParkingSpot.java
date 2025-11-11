package parkingLoot;

class ParkingSpot {
    private final String spotId;
    private final VehicleType allowedType;
    private boolean isFree = true;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, VehicleType allowedType) {
        this.spotId = spotId;
        this.allowedType = allowedType;
    }

    public boolean canFit(Vehicle v) { return isFree && allowedType == v.getType(); }

    public void park(Vehicle v) {
        if (!canFit(v)) throw new IllegalStateException("Spot not available or type mismatch");
        this.parkedVehicle = v;
        this.isFree = false;
    }

    public void free() {
        this.parkedVehicle = null;
        this.isFree = true;
    }

    public String getSpotId() { return spotId; }
    public boolean isFree() { return isFree; }
    public VehicleType getAllowedType() { return allowedType; }
}

