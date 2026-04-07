package ca.seneca.application.service.decorator;

public class BaseBill implements BillComponent {
    private final double total;
    private final String description;

    public BaseBill(double total, String description) {
        this.total = total;
        this.description = description;
    }


    @Override
    public double getTotal() {
        return total;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
