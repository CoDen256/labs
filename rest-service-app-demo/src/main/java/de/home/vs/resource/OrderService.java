package de.home.vs.resource;

import de.home.vs.model.DataSource;
import de.home.vs.model.order.Order;
import de.home.vs.model.order.OrderedItem;
import java.util.List;
import java.util.Optional;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/orders")
public class OrderService {
    private final DataSource dataSource = DataSource.getInstance();
    private final OrderSerializer orderSerializer = new OrderSerializer("/rest/orders/");
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders(){
        try {
            return Response
                    .ok()
                    .entity(orderSerializer.serializeOrders(dataSource.getOrders(), orderSerializer::serializeShort))
                    .build();
        }catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderById(@PathParam("id") int id){
        try {
            Optional<Order> order = dataSource.findOrderById(id);
            if (order.isEmpty()){
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(String.format("Order with id %d not found", id))
                        .build();
            }
            return Response
                    .ok()
                    .entity(orderSerializer.serializeFull(order.get()))
                    .build();
        }catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postOrder(JsonObject requestedOrder) {
        try {
            Order newOrder = createNewOrder(requestedOrder);
            verifyNewOrder(newOrder);
            dataSource.addOrder(newOrder);
            return Response.ok()
                    .entity(String.format("Order with id %s created successfully", newOrder.getId()))
                    .build();
        }
        catch (InvalidItemException e){
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        catch (Exception e){
            return Response.serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }


    private Order createNewOrder(JsonObject requestedOrder){
        return orderSerializer.deserializeWithNewId(requestedOrder, getNextOrderId());
    }

    private int getNextOrderId(){
        return dataSource.getOrders().size() + 1;
    }

    private void verifyNewOrder(Order order){
        List<OrderedItem> items = order.getItems();
        items.forEach(this::verifyItem);
    }

    private void verifyItem(OrderedItem i) {
        if (dataSource.findItemById(i.getId()).isEmpty()){
            throw new InvalidItemException(String.format("Item with id %s not found", i.getId()));
        }
        if (i.getQuantity() <= 0){
            throw new InvalidItemException(String.format("Item with id %s has non-positive quantity", i.getId()));
        }
    }

}
