package task1.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerWorker extends Thread{
    public static final int BUFFER_SIZE = 10;
    public static final String EOF = "\r\n.\r\n";
    private final Socket socket;


    public ServerWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            handleConnection();
            socket.close();
        } catch (IOException|InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleConnection() throws IOException, InterruptedException {
        InetSocketAddress clientAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        log("Connection with the client established successfully: Address: %s%n", clientAddress.getAddress().toString());

        while (true){
            String request = readRequest();
            Thread.sleep(2000);
            String response = request;
            if (request.startsWith("9")){
                sendResponse(socket, "END");
                return;
            }
            sendResponse(socket, response);
            System.out.println();
        }
    }


    private String readRequest() throws IOException {
        StringBuilder requestBuilder = new StringBuilder();
        InputStream inputStream = socket.getInputStream();
        while (true){
            requestBuilder.append((char)inputStream.read());
            int length = requestBuilder.length();
            if (length >= 5 && EOF.equals(requestBuilder.substring(length - 5)))
                break;
        }

        // remove EOF from the request body
        log("Length:%d%n", requestBuilder.length());
        log("Request:%s%n", escape(requestBuilder.toString()));
        return requestBuilder.delete(requestBuilder.length()-5, requestBuilder.length()).toString();
    }


    private void sendResponse(Socket socket, String response) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(response.getBytes());
        log("Response sent: %s%n", escape(response));
    }

    private void log(String format, Object... objects){
        System.out.printf("[%s] ", Thread.currentThread().getName());
        System.out.printf(format, objects);
    }

    private static String escape(String string) {
        return string.replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
