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
            socket.close();
        } catch (IOException|InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleConnection() throws IOException, InterruptedException {
        InetSocketAddress clientAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        log("Connection with the client established successfully: Address: %s%n", clientAddress.getAddress().toString());

        while (true){
            int length = readRequestLength();
            String request = readRequest(length);
            Thread.sleep(2000);
            String response = request;
            if (request.endsWith("9")){
                sendResponse(socket, "END");
                return;
            }
            sendResponse(socket, response);
            System.out.println();
        }
    }

    private int readRequestLength() throws IOException {
        byte[] lengthBuffer = new byte[BUFFER_SIZE];
        InputStream inputStream = socket.getInputStream();
        int index = 0;
        byte character;
        while ((character = (byte) inputStream.read()) != '\n'){
            lengthBuffer[index++] = character;
        }
        int length = Integer.parseInt(new String(lengthBuffer, 0, index));
        log("Length of the request: %d%n", length);
        return length;
    }


    private String readRequest(int length) throws IOException {
        byte[] buffer = new byte[length];
        InputStream inputStream = socket.getInputStream();
        int bytesRead = inputStream.read(buffer);
        String request = bytesToString(buffer, bytesRead);
        log("Bytes read:%d%n", bytesRead);
        log("Request:%s%n", request);
        return request;
    }

    private void sendResponse(Socket socket, String response) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(response.getBytes());
        log("Response sent: %s%n", response);
    }

    private String bytesToString(byte[] bytes, int length){
        return new String(bytes, 0, length);
    }
    private void log(String format, Object... objects){
        System.out.printf("[%s] ", Thread.currentThread().getName());
        System.out.printf(format, objects);
    }
}
