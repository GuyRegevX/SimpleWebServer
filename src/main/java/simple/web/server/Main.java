package simple.web.server;

public class Main {
    public static void main(String[] args) {
        var webServer = new WebServer();

        // Add shutdown hook as early as possible
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown initiated...");
            try {
                // Your shutdown logic here
                webServer.shutdown();
                System.out.println("Shutdown completed");
            } catch (Exception e) {
                System.err.println("Error during shutdown: " + e.getMessage());
            }
        }));

        // Start Server
        webServer.startup();

    }
}