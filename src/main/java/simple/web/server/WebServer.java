package simple.web.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class WebServer {
    private static final int PORT = Const.SERVER_PORT;
    private boolean isRunning = true;
    private ServerSocket serverSocket;
    private final ThreadPoolExecutor threadPool; // Limit concurrent threads

    public WebServer() {
        int cores = Runtime.getRuntime().availableProcessors();
        threadPool = new ThreadPoolExecutor(
                cores,                // core threads
                cores * 2,           // max threads
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100), // bounded queue
                new ThreadPoolExecutor.CallerRunsPolicy() // fallback strategy
        );
    }

    public void startup() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handleClient(clientSocket));
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

    void checkQueueHealth() {
        int queueSize = threadPool.getQueue().size();
        int queueCapacity = threadPool.getQueue().size() + threadPool.getQueue().remainingCapacity();
        double queueUtilization = (double) queueSize / queueCapacity * 100;

        System.out.printf("Queue utilization: %.2f%%%n", queueUtilization);
        if (queueUtilization > 80) {
            System.out.println("Warning: High queue utilization!");
        }
    }


    public void shutdown() {
        if (threadPool != null) {
            threadPool.shutdown();
            try {
                // Wait for existing tasks to terminate
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    // Force shutdown if tasks don't complete in time
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

}
