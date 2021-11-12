package de.home.vs.resource;

import de.home.vs.model.article.AddArticleRequest;
import de.home.vs.model.article.Article;
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
    private DataSource dataSource = DataSource.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response articles(){
        try {
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
            Article article = dataSource.findArticleById(id);
            if (article == null)
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(String.format("Article with id %d not found", id))
                        .build();
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
    public Response addArticleRequest(AddArticleRequest article) {
        try {
            Article newArticle = createNewArticle(article);
            dataSource.addArticle(newArticle);
            return Response.ok()
                    .entity(String.format("Article with id %s created successfully", newArticle.getId()))
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

    private Article createNewArticle(AddArticleRequest request){
        return new Article(
                getNextArticleId(),
                request.getName(),
                request.getDescription(),
                request.getPrice());
    }

    private int getNextArticleId() {
        return dataSource.getArticles().size() + 1;
    }

}
