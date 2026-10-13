package ca.seneca.application.service.decorator;

public class ParkingDecorator extends BillDecorator {
    private static final double PARKING_PER_NIGHT = 25.0;
    private final long nights;

    public ParkingDecorator(BillComponent bill, long nights) {
        super(bill);
        this.nights = nights;
    }

    @Override
    public double getTotal() {
        return bill.getTotal() + (PARKING_PER_NIGHT * nights);
    }

    @Override
    public String getDescription() {
        return bill.getDescription() + ", Parking";
    }
}
