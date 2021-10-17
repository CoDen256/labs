package tasks.vs.task1.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    public static final int PORT = 5555;

    public static void main(String[] args) throws IOException {
        System.out.println("Running Server...");
        try (ServerSocket listenSocket = new ServerSocket(PORT)){
            System.out.printf("Server socket for port %d created successfully.%n", PORT);
            acceptConnections(listenSocket);
        } catch (IOException e) {
            System.out.printf("Creating ServerSocket for port %d failed%n", PORT);
            throw e;
        }
    }

    private static void acceptConnections(ServerSocket listenSocket) throws IOException {
        while(true){
            try(Socket socket = listenSocket.accept()){
                handleConnection(socket);
            } catch (IOException e){
                System.out.println("Accepting incoming connection failed");
                throw e;
            }
        }
    }

    private static void handleConnection(Socket socket) throws IOException {
        byte[] buffer = new byte[4096];
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        int read = inputStream.read(buffer);
        outputStream.write(buffer);
    }
}
