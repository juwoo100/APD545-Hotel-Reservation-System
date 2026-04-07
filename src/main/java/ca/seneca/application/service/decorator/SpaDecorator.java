package ca.seneca.application.service.decorator;

public class SpaDecorator extends BillDecorator {
    private static final double SPA_PRICE = 60.0;

    public SpaDecorator(BillComponent bill) {
        super(bill);
    }

    @Override
    public double getTotal() {
        return bill.getTotal() + SPA_PRICE;
    }

    @Override
    public String getDescription() {
        return bill.getDescription() + ", Spa";
    }
}
