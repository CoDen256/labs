package tasks.vs.task1.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketClientV2 {
    public static final String ADDRESS = "localhost";
    public static final int PORT = 5555;
    public static final String REQUEST_TEMPLATE = "%d\n%s";


    public static void main(String[] args) throws Exception {
        System.out.println("Running Client v2...");

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
            if (request.equals("END")) return;
            System.out.printf("Trying to connect to %s:%s...%n", ADDRESS, PORT);
            try(Socket socket = new Socket(remoteAddress, PORT)){
                sendMultipleRequests(request, socket);
            } catch (IOException e) {
                System.out.printf("Unable to connect to %s:%s.%nTrying again in 5s...%n", ADDRESS, PORT);
                Thread.sleep(5000);
            }finally {
                System.out.printf("Disconnected from %s:%s%n", ADDRESS, PORT);
            }
        }
    }

    private static void sendMultipleRequests(String requestContent, Socket socket) throws IOException {
        System.out.println("Connection established successfully.");
        String response;
        int current = 0;
        do {
            String request = requestContent + current++;
            sendRequest(request, socket);
            response = readResponse(socket, Math.max(3, request.length()));
            System.out.println();
        } while (!"END".equals(response));
        System.out.println("Server sent 'END'");
    }

    private static void sendRequest(String request, Socket socket) throws IOException {
        String requestWithHeader = String.format(REQUEST_TEMPLATE, request.length(), request);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(requestWithHeader.getBytes());
        System.out.printf("Request sent: %s%nWaiting for response...%n", requestWithHeader);
    }

    private static String readResponse(Socket socket, int length) throws IOException {
        InputStream inputStream = socket.getInputStream();
        byte[] buffer = new byte[length];
        int bytesRead = inputStream.read(buffer);
        String responseString = bytesToString(buffer, bytesRead);
        System.out.printf("Bytes read: %d.%nResponse:%s%n", bytesRead, responseString);

        return responseString;
    }

    private static String bytesToString(byte[] bytes, int length){
        return new String(bytes, 0, length);
    }
}
