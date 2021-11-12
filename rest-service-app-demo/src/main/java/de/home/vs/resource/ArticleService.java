package de.home.vs.resource;

import de.home.vs.model.Article;
import de.home.vs.model.DataSource;
import java.util.Set;
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
        try {
            DataSource dataSource = DataSource.getInstance();
            return Response
                    .ok()
                    .entity(new GenericEntity<Set<Article>>(dataSource.getArticles()){})
                    .build();
        }catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response article(@PathParam("id") int id){
        try {
            Article article = DataSource.getInstance().findById(id);
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
    public Response postNotification(Article article) {
        DataSource dataSource = DataSource.getInstance();
        try {
            dataSource.addArticle(article);
            return Response.ok()
                    .entity(String.format("Article with id %s created successfully", article.getId()))
                    .build();
        }catch (DataSource.ArticleAlreadyExistsException e){
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
