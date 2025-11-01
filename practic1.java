class GymMembership {
    protected String name;
    protected String type;
    protected int duration;

    public GymMembership(String name, String type, int duration) {
        this.name = name;
        this.type = type;
        this.duration = duration;
    }

    public double calculateFees() {
        double monthlyRate;
        switch (type.toLowerCase()) {
            case "basic":
                monthlyRate = 500;
                break;
            case "standard":
                monthlyRate = 800;
                break;
            case "premium":
                monthlyRate = 1200;
                break;
            default:
                monthlyRate = 600; // default rate
        }
        return monthlyRate * duration;
    }

    public boolean hasSpecialOffer() {
        return duration >= 12; // Offer for 1-year memberships
    }

    public void displayInfo() {
        System.out.println("Member Name: " + name);
        System.out.println("Membership Type: " + type);
        System.out.println("Duration: " + duration + " months");
        System.out.println("Total Fees: ₹" + calculateFees());
        System.out.println("Eligible for Special Offer: " + (hasSpecialOffer() ? "Yes" : "No"));
    }

}

class PremiumMembership extends GymMembership {

    private boolean personalTrainer;
    private boolean spaAccess;

    public PremiumMembership(String name, String type, int duration, boolean personalTrainer, boolean spaAccess) {
        super(name, type, duration);
        this.personalTrainer = personalTrainer;
        this.spaAccess = spaAccess;
    }

    @Override
    public double calculateFees() {
        double baseFee = super.calculateFees();
        double trainerFee = personalTrainer ? 300 * duration : 0;
        double spaFee = spaAccess ? 200 * duration : 0;
        return baseFee + trainerFee + spaFee;
    }

    @Override
    public void displayInfo() {

        super.displayInfo();
        System.out.println("Personal Trainer: " + (personalTrainer ? "Included" : "Not Included"));
        System.out.println("Spa Access: " + (spaAccess ? "Included" : "Not Included"));
    }
}

public class practic1 {
    public static void main(String[] args) {

        GymMembership member1 = new GymMembership("naveen", "standard", 1);
        member1.displayInfo();
        GymMembership member2 = new PremiumMembership("jack", "bronze", 12, true, false);
        member2.displayInfo();
        PremiumMembership member3 = new PremiumMembership("ram", "gold", 15, true, true);
        member3.displayInfo();
    }

}
