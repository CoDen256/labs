package tasks.vs.task1.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class SocketClient {
    public static final String ADDRESS = "localhost";
    public static final int PORT = 5555;

    public static void main(String[] args) throws Exception {
        System.out.println("Running SocketClient...");

        InetAddress remoteAddress;
        try {
            remoteAddress = InetAddress.getByName(ADDRESS);
        } catch (UnknownHostException e) {
            System.out.printf("Unable to get InetAddress for %s%n", ADDRESS);
            throw e;
        }

        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.print("Enter request to send: ");
            String request = scanner.nextLine();
            if (request.equals("STOP")) return;
            System.out.printf("Trying to connect to %s:%s...%n", ADDRESS, PORT);
            try(Socket socket = new Socket(remoteAddress, PORT)){
                System.out.printf("Connection established successfully. Sending the request: %s%n",request);
                sendRequest(request, socket);
            } catch (IOException e) {
                System.out.printf("Unable to connect to %s:%s.%nTrying again in 5s...%n", ADDRESS, PORT);
                Thread.sleep(5000);
            }
        }
    }

    private static void sendRequest(String request, Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(request.getBytes());
        System.out.printf("Request has been sent to the server:%s %n...", Arrays.toString(request.getBytes()));

        System.out.println("Reading response...");
        byte[] readBytes = new byte[4098];
        InputStream inputStream = socket.getInputStream();
        int read = inputStream.read(readBytes);
        System.out.printf("Bytes read: %d%n. Response:%s", read, new String(readBytes));
    }
}
