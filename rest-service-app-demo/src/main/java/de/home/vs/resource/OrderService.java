package de.home.vs.resource;

import de.home.vs.model.DataSource;
import de.home.vs.model.order.Order;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/order")
public class OrderService {


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response article(@PathParam("id") int id){
        try {
            Order article = DataSource.getInstance().findOrderById(id);
            if (article == null){
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(String.format("Article with id %d not found", id))
                        .build();
            }
            return Response
                    .ok()
                    .entity(article)
                    .build();
        }catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postNotification(Order order) {
        DataSource dataSource = DataSource.getInstance();
        try {
            dataSource.addOrder(order);
            return Response.ok()
                    .entity(String.format("Order with id %s created successfully", order.getId()))
                    .build();
        }catch (DataSource.OrderAlreadyExistsException e){
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }catch (Exception e){
            return Response.serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }

}
