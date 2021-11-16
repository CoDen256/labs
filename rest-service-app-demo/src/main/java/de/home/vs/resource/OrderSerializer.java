package de.home.vs.resource;

import de.home.vs.model.order.Order;
import de.home.vs.model.order.OrderedItem;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OrderSerializer {

    private final String baseUrl;

    public OrderSerializer(String baseOrderUrl) {
        this.baseUrl = baseOrderUrl;
    }

    public JsonArray serializeOrders(Collection<Order> orders, Function<Order, JsonObject> serializationMethod) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        orders.forEach(o -> arrayBuilder.add(serializationMethod.apply(o)));
        return arrayBuilder.build();
    }

    public JsonObject serializeFull(Order order) {
        return Json.createObjectBuilder()
                .add("id", order.getId())
                .add("items", serializeItems(order.getItems()))
                .add("link", resolveLink(order, baseUrl))
                .build();
    }

    public JsonObject serializeShort(Order order) {
        return Json.createObjectBuilder()
                .add("id", order.getId())
                .add("link", resolveLink(order, baseUrl))
                .build();
    }

    public JsonArray serializeItems(Collection<OrderedItem> items) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        items.forEach(i -> arrayBuilder.add(serializeItem(i)));
        return arrayBuilder.build();
    }

    public JsonObject serializeItem(OrderedItem item) {
        return Json.createObjectBuilder()
                .add("id", item.getId())
                .add("quantity", item.getQuantity())
                .build();
    }

    public Order deserializeWithNewId(JsonObject object, int id) {
        return new Order(id, deserializeItems(object.getJsonArray("items")));
    }

    private List<OrderedItem> deserializeItems(JsonArray items) {
        return IntStream
                .range(0, items.size())
                .mapToObj(items::getJsonObject)
                .map(this::deserializeItem).collect(Collectors.toList());
    }

    private OrderedItem deserializeItem(JsonObject item) {
        return new OrderedItem(item.getInt("id"), item.getInt("quantity"));
    }

    private String resolveLink(Order order, String baseUrl) {
        return baseUrl + order.getId();
    }
}
