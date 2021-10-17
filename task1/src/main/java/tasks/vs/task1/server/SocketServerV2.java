package tasks.vs.task1.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServerV2 {

    public static final int PORT = 5555;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Running Server v2...");
        try (ServerSocket listenSocket = new ServerSocket(PORT)){
            acceptConnections(listenSocket);
        } catch (IOException e) {
            System.out.printf("Creating ServerSocket for port %d failed%n", PORT);
            throw e;
        }
    }

    private static void acceptConnections(ServerSocket listenSocket) throws IOException, InterruptedException {
        System.out.printf("Server socket for port %d created successfully.%n", PORT);
        while(true){
            System.out.printf("Waiting for incoming connections on port %d...%n", PORT);
            try{
                Socket socket = listenSocket.accept();
                new ServerWorker(socket).start(); // non-blocking
            } catch (IOException e){
                System.out.println("Accepting incoming connection failed\nTrying again in 5s...");
                Thread.sleep(5000);
            }
        }
    }
}
