package de.home.vs.rest_service_app;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import de.home.vs.model.DataSource;


public class RestService 
{
    public static void main( String[] args )
    {
        System.out.println( "starting REST service demo...." );
        InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 8080);
        Server server = new Server(addr);

        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
                
        ctx.setContextPath("/");
        server.setHandler(ctx);

        ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/rest/*");
        
        serHol.setInitOrder(1);
        serHol.setInitParameter("jersey.config.server.provider.packages", "de.home.vs.resource");
//        serHol.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");

        DataSource ds = DataSource.getInstance();
        ds.prefillData();
        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            Logger.getLogger(RestService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            server.destroy();
        }        
    }
}
