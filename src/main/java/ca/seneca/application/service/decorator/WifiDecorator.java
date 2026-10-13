package ca.seneca.application.service.decorator;

public class WifiDecorator extends BillDecorator {
    private static final double WIFI_PRICE = 15.0; // PER_RESERVATION

    public WifiDecorator(BillComponent bill) {
        super(bill);
    }

    @Override
    public double getTotal() {
        return bill.getTotal() + WIFI_PRICE;
    }

    @Override
    public String getDescription() {
        return bill.getDescription() + ", WiFi";
    }
}
