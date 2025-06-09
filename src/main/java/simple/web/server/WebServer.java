package simple.web.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.web.server.enums.HttpStatusCode;
import simple.web.server.hanlder.RequestHandler;
import simple.web.server.models.responses.HttpWebResponse;
import simple.web.server.request.HTTPRequestParserImpl;
import simple.web.server.response.ResponseSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.*;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    private static final int PORT = Const.Server.PORT;
    private boolean isRunning = true;
    private ServerSocket serverSocket;
    private final ThreadPoolExecutor threadPool;
    private final ResponseSender responseSender;// Limit concurrent threads
    private final HttpWebResponse serviceUnavailableResponse;
    private final RequestHandler requestHandler;

    public WebServer(ResponseSender responseSender, RequestHandler requestHandler) {
        this.responseSender = responseSender;
        this.requestHandler = requestHandler;
        int cores = Runtime.getRuntime().availableProcessors();
        serviceUnavailableResponse = HttpWebResponse.builder().statusCode(HttpStatusCode.SERVER_UNAVAILABLE_ERROR).build();
        threadPool = new ThreadPoolExecutor(
                cores,                // core threads
                cores * 2,           // max threads
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100), // bounded queue
                new ThreadPoolExecutor.CallerRunsPolicy() // fallback strategy
        );
    }

    public void startup() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                try {
                    threadPool.submit(() -> handleClient(clientSocket));
                } catch (RejectedExecutionException e) {
                        logger.error("Server is overloaded - cannot accept new client connection");
                        // Important: Clean up the socket to prevent resource leak
                        try {
                            responseSender.send(clientSocket, serviceUnavailableResponse);
                        } catch (IOException closeError) {
                            logger.error("Failed to close rejected client socket", closeError);
                        }
                    }
            }
        }catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
      try{
            requestHandler.handle(clientSocket);
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
        //First, finish all the connection (they still use the sockets)
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

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client socket on shutdown: " + e.getMessage());
        }
    }

}
