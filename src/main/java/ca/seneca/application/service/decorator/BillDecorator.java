package ca.seneca.application.service.decorator;

public abstract class BillDecorator implements BillComponent {
    protected final BillComponent bill;

    public BillDecorator(BillComponent bill) {
        this.bill = bill;
    }

    @Override
    public double getTotal() {
        return bill.getTotal();
    }

    @Override
    public String getDescription() {
        return bill.getDescription();
    }
}
