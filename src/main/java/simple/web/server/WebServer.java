package simple.web.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private static final int PORT = 9090;
    private boolean isRunning = true;
    private ServerSocket serverSocket;
    ExecutorService threadPool = Executors.newFixedThreadPool(10); // Limit concurrent threads


    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (clientSocket; BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            try {

                // Read response
                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println(response);
                }

            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }
}
