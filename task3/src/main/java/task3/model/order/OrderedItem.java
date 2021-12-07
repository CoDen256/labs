package task3.model.order;

public class OrderedItem {
    private final int id;
    private final int quantity;

    public OrderedItem(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }
}
