package task3.model;

import task3.model.item.Item;
import task3.model.order.Order;
import task3.model.order.OrderedItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DataSource {

	private static DataSource instance = null;
	private final Set<Item> items = new LinkedHashSet<>();
	private final Set<Order> orders = new LinkedHashSet<>();

	private DataSource() {
	}
	
	public static DataSource getInstance() {
		if (instance == null) {
			instance = new DataSource();
		}
		return instance;
	}
	
	public void prefillData() {
		addItem(new Item(0, "Alpha", "AAA", 1000));
		addItem(new Item(1, "Beta", "BBB", 0));
		addItem(new Item(2, "Gamma", "GGG", 13000));
		addItem(new Item(3, "Delta", "DDD", 50));
		addItem(new Item(4, "Epsilon", "EEE", 9000));

		List<OrderedItem> orderedItems = new ArrayList<>();
		orderedItems.add(new OrderedItem(0, 10));
		orderedItems.add(new OrderedItem(2, 2));
		orderedItems.add(new OrderedItem(4, 1));
		addOrder(new Order(
				0,
				orderedItems
		));

		List<OrderedItem> orderedItems2 = new ArrayList<>();
		orderedItems2.add(new OrderedItem(3, 30));
		addOrder(new Order(
				1,
				orderedItems2
		));
	}

	public Optional<Item> findItemById(int id){
		return items.stream().filter(i -> i.getId() == id).findAny();
	}

	public Set<Item> getItems(){
		return Collections.unmodifiableSet(items);
	}

	public void addItem(Item a){
		items.add(a);
	}

	public Optional<Order> findOrderById(int id){
		return orders.stream().filter(i -> i.getId() == id).findAny();
	}

	public Set<Order> getOrders(){
		return new LinkedHashSet<>(orders);
	}

	public void addOrder(Order a){
		orders.add(a);
	}

	public int getNextOrderId(){
		return orders.size();
	}
	public int getNextItemId(){
		return items.size();
	}
}
