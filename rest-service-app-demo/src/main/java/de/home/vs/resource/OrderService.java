package de.home.vs.resource;

import de.home.vs.model.DataSource;
import de.home.vs.model.article.Article;
import de.home.vs.model.order.AddOrderRequest;
import de.home.vs.model.order.MultipleArticles;
import de.home.vs.model.order.Order;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
    DataSource dataSource = DataSource.getInstance();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response article(@PathParam("id") int id){
        try {
            Order article = dataSource.findOrderById(id);
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
    public Response addNewOrder(AddOrderRequest request) {
        try {
            Order newOrder = createNewOrder(request);
            dataSource.addOrder(newOrder);
            return Response.ok()
                    .entity(String.format("Order with id %s created successfully", newOrder.getOrderId()))
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


    private Order createNewOrder(AddOrderRequest request){
        List<Article> articles = new ArrayList<Article>();
        for (MultipleArticles multipleArticles: request.getArticles()){
            Article article = dataSource.findArticleById(multipleArticles.getArticleId());
            for (int i = 0; i < multipleArticles.getCount(); i++) {
                articles.add(article);
            }
        }
        return new Order(getNextOrderId(), articles);
    }

    private int getNextOrderId(){
        return dataSource.getOrders().size() + 1;
    }
}
