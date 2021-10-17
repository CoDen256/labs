package tasks.vs.task1.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

public class SocketServer {

    public static final int PORT = 5555;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Running Server...");
        try (ServerSocket listenSocket = new ServerSocket(PORT)){
            System.out.printf("Server socket for port %d created successfully.%n", PORT);
            acceptConnections(listenSocket);
        } catch (IOException e) {
            System.out.printf("Creating ServerSocket for port %d failed%n", PORT);
            throw e;
        }
    }

    private static void acceptConnections(ServerSocket listenSocket) throws IOException, InterruptedException {
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

    private static void handleConnection(Socket socket) throws IOException {
        InetSocketAddress clientAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        System.out.printf("Connection with the client established successfully: Address: %s%n", clientAddress.getAddress().toString());
        System.out.println("Reading Request...");
        byte[] readBytes = new byte[3];
        InputStream inputStream = socket.getInputStream();
        int bytesRead = inputStream.read(readBytes);
        System.out.printf("Bytes:%d.%nRequest:%s%n", bytesRead, bytesToString(readBytes));

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(readBytes);
        System.out.printf("Sending back: %s%n", bytesToString(readBytes));
    }

    private static String bytesToString(byte[] bytes){
        return new String(bytes, 0, 3);
    }
}
