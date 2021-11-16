package de.home.vs.resource;

import de.home.vs.model.DataSource;
import de.home.vs.model.item.Item;
import java.util.Optional;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/items")
public class ItemService {
    private final DataSource dataSource = DataSource.getInstance();
    private final ItemSerializer itemSerializer = new ItemSerializer("/items/");

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems(){
        try {
            return Response
                    .ok()
                    .entity(itemSerializer.serializeItems(dataSource.getItems(), itemSerializer::serializeShort))
                    .build();
        }catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemById(@PathParam("id") int id){
        try {
            Optional<Item> item = dataSource.findItemById(id);
            if (!item.isPresent())
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(String.format("Item with id %d not found", id))
                        .build();
            return Response
                    .ok()
                    .entity(itemSerializer.serializeFull(item.get()))
                    .build();
        }catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postItem(JsonObject requestedItem) {
        try {
            Item newItem = createNewArticle(requestedItem);
            dataSource.addItem(newItem);
            return Response.ok()
                    .entity(String.format("Item with id %s created successfully", newItem.getId()))
                    .build();
        }catch (Exception e){
            return Response.serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }

    private Item createNewArticle(JsonObject requestedItem){
        return itemSerializer.deserializeWithId(requestedItem, getNextArticleId());
    }

    private int getNextArticleId() {
        return dataSource.getItems().size() + 1;
    }

}
