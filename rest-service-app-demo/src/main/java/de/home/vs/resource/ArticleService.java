package de.home.vs.resource;

import de.home.vs.model.Article;
import de.home.vs.model.DataSource;
import de.home.vs.model.Error;
import java.util.Set;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/article")
public class ArticleService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response articles(){
        DataSource dataSource = DataSource.getInstance();
        return Response
                .ok()
                .entity(new GenericEntity<Set<Article>>(dataSource.getArticles()){})
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response article(@PathParam("id") int id){
        Article article = DataSource.getInstance().findById(id);
        if (article == null){
            return Response.status(404)
                    .entity(new Error(String.format("Article with id %d not found", id)))
                    .build();
        }
        return Response.ok().entity(article).build();
    }

    @POST
    @Path("/post/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postNotification(JsonObject notification) {
        return Response.status(201).entity(notification).build();
    }

}
