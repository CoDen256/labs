package de.home.vs.resource;

import de.home.vs.model.DataSource;
import de.home.vs.model.item.Item;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;


@Path("/items")
public class ItemService {
    private final DataSource dataSource = DataSource.getInstance();
    private final ItemSerializer itemSerializer = new ItemSerializer("/rest/items/");

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems() {
        try {
            return Response
                    .ok()
                    .entity(itemSerializer.serializeItems(dataSource.getItems(), itemSerializer::serializeShort))
                    .build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemById(@PathParam("id") int id) {
        try {
            Optional<Item> item = dataSource.findItemById(id);
            if (item.isEmpty())
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(String.format("Item with id %d not found", id))
                        .build();
            return Response
                    .ok()
                    .entity(itemSerializer.serializeFull(item.get()))
                    .build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postItem(JsonObject requestedItem) {
        try {
            Item newItem = createNewItem(requestedItem);
            verifyItem(newItem);
            dataSource.addItem(newItem);
            return Response.ok()
                    .entity(String.format("Item with id %s created successfully", newItem.getId()))
                    .build();
        } catch (InvalidItemException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }

    private Item createNewItem(JsonObject requestedItem) {
        return itemSerializer.deserializeWithId(requestedItem, getNextArticleId());
    }

    private int getNextArticleId() {
        return dataSource.getItems().size() + 1;
    }

    private void verifyItem(Item item) {
        if (item.getPrice() <= 0) {
            throw new InvalidItemException("Item has non-positive price");
        }
    }
}
