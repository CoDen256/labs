package tasks.vs.task1.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    public static final int PORT = 5555;
    public static final int BUFFER_SIZE = 10;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Running Server...");
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
            try(Socket socket = listenSocket.accept()){
                handleConnection(socket);
            } catch (IOException e){
                System.out.println("Accepting incoming connection failed\nTrying again in 5s...");
                Thread.sleep(5000);
            }
        }
    }

    private static void handleConnection(Socket socket) throws IOException, InterruptedException {
        InetSocketAddress clientAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        System.out.printf("Connection with the client established successfully: Address: %s%n", clientAddress.getAddress().toString());
        String request;
        while (true){
            request = readRequest(socket);
            String response = request + request; // just duplicate strings
            Thread.sleep(1000); // simulate long computing
            if (request.endsWith("9")){
                sendResponse(socket, "END");
                return;
            }
            sendResponse(socket, response);
            System.out.println();
        }
    }

    private static String readRequest(Socket socket) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        InputStream inputStream = socket.getInputStream();
        int bytesRead = inputStream.read(buffer);
        String request = bytesToString(buffer, bytesRead);
        System.out.printf("Bytes read:%d.%nRequest:%s%n", bytesRead, request);
        return request;
    }

    private static void sendResponse(Socket socket, String response) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(response.getBytes());
        System.out.printf("Response sent: %s%n", response);
    }

    private static String bytesToString(byte[] bytes, int length){
        return new String(bytes, 0, length);
    }
}
