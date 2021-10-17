package tasks.vs.task1.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerWorker extends Thread{
    public static final int BUFFER_SIZE = 10;
    private final Socket socket;


    public ServerWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            handleConnection();
        } catch (IOException|InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleConnection() throws IOException, InterruptedException {
        InetSocketAddress clientAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        System.out.printf("Connection with the client established successfully: Address: %s%n", clientAddress.getAddress().toString());

        InputStream inputStream = socket.getInputStream();
        int length = readRequestLength(inputStream);
        String request = readRequest(inputStream, length);
        sendResponse(socket, request);
        sendResponse(socket, "END");
    }

    private int readRequestLength(InputStream inputStream) throws IOException {
        byte[] lengthBuffer = new byte[BUFFER_SIZE];
        int index = 0;
        byte character;
        while ((character = (byte) inputStream.read()) != '\n'){
            lengthBuffer[index++] = character;
        }
        int length = Integer.parseInt(new String(lengthBuffer, 0, index));
        System.out.printf("Length of the request: %d%n", length);
        return length;
    }


    private String readRequest(InputStream inputStream, int length) throws IOException {
        byte[] buffer = new byte[length];
//        InputStream inputStream = socket.getInputStream(); but it should work as well
        int bytesRead = inputStream.read(buffer);
        String request = bytesToString(buffer, bytesRead);
        System.out.printf("Bytes read:%d.%nRequest:%s%n", bytesRead, request);
        return request;
    }

    private void sendResponse(Socket socket, String response) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(response.getBytes());
        System.out.printf("Response sent: %s%n", response);
    }

    private String bytesToString(byte[] bytes, int length){
        return new String(bytes, 0, length);
    }

}
