package de.home.vs.model.order;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {
    private final int id;
    private final List<OrderedItem> items;

    public Order(int id, List<OrderedItem> items) {
        this.id = id;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public List<OrderedItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
