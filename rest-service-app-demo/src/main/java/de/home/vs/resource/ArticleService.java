package de.home.vs.resource;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/article")
public class ArticleService {
    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayInPeace(@PathParam("id") int id){
        return "RIP"+id;
    }

    @POST
    @Path("/post/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postNotification(JsonObject notification) {
        return Response.status(201).entity(notification).build();
    }

}
