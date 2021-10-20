package task1.client;

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
    public static final String EOF = "\r\n.\r\n";

    public static void main(String[] args) throws Exception {
        System.out.println("Running Client v2...");

        InetAddress remoteAddress;
        try {
            remoteAddress = InetAddress.getByName(ADDRESS);
        } catch (UnknownHostException e) {
            System.out.printf("Unable to get InetAddress for %s%n", ADDRESS);
            throw e;
        }

        while (true){
            String request = readRequest();
            if (request.contains(EOF)) {
                System.out.println("Cannot use EOF in body of the request");
                continue;
            }
            connectAndSendRequests(remoteAddress, request);
        }
    }

    private static void connectAndSendRequests(InetAddress remoteAddress, String request) throws InterruptedException {
        System.out.printf("Trying to connect to %s:%s...%n", ADDRESS, PORT);

        try(Socket socket = new Socket(remoteAddress, PORT)){
            System.out.println("Connection established successfully.");
            sendMultipleRequests(request, socket);
        } catch (IOException e) {
            System.out.printf("Unable to connect to %s:%s.%nTrying again in 5s...%n", ADDRESS, PORT);
            Thread.sleep(5000);
        }finally {
            System.out.printf("Disconnected from %s:%s%n", ADDRESS, PORT);
        }
    }

    private static void sendMultipleRequests(String requestContent, Socket socket) throws IOException {
        String response;
        int currentRequest = 0;
        do {
            String request = currentRequest++ + requestContent; // just add a new number each time
            sendRequest(request, socket);
            response = readResponse(socket, request.length()); // server will echo it
            System.out.println();
        } while (!"END".equals(response));
        System.out.println("Server sent 'END'");
    }

    private static void sendRequest(String request, Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        String requestWithEOF = request + EOF;
        outputStream.write(requestWithEOF.getBytes());
        System.out.printf("Request sent: %s%nWaiting for response...%n", requestWithEOF);
    }

    private static String readResponse(Socket socket, int length) throws IOException {
        InputStream inputStream = socket.getInputStream();
        byte[] buffer = new byte[Math.max(3, length)]; // for END
        int bytesRead = inputStream.read(buffer);
        String responseString = bytesToString(buffer, bytesRead);
        System.out.printf("Response(%dB):%s%n", bytesRead, responseString);

        return responseString;
    }

    private static String bytesToString(byte[] bytes, int length){
        return new String(bytes, 0, length);
    }

    private static String readRequest(){
        System.out.print("Enter request to send: ");

        Scanner scanner = new Scanner(System.in);
        StringBuilder stringBuilder = new StringBuilder();
        String nextLine;
        while(true){
            nextLine = scanner.nextLine();
            if (nextLine.isEmpty()) break;
            stringBuilder.append(nextLine).append("\r\n");
        }
        return stringBuilder.toString();
    }
}
