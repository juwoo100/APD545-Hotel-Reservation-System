package ca.seneca.application.service.decorator;

public class BreakfastDecorator extends BillDecorator{
    private static final double BREAKFAST_PER_NIGHT = 20.0;
    private final long nights;

    public BreakfastDecorator(BillComponent bill, long nights) {
        super(bill);
        this.nights = nights;
    }

    @Override
    public double getTotal() {
        return bill.getTotal() + (BREAKFAST_PER_NIGHT * nights);
    }

    @Override
    public String getDescription() {
        return bill.getDescription() + ", Breakfast";
    }
}
