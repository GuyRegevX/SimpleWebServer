package simple.web.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import simple.web.server.hanlder.RequestHandlerImpl;
import simple.web.server.request.HTTPRequestParserImpl;
import simple.web.server.response.RestResponseSenderImpl;

public class Main {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        RestResponseSenderImpl restResponseSender = new RestResponseSenderImpl(objectMapper);
        HTTPRequestParserImpl requestParser = new HTTPRequestParserImpl();
        RequestHandlerImpl requestHandler = new RequestHandlerImpl
                (
                        requestParser,
                        restResponseSender
                );


        var webServer = new WebServer(restResponseSender, requestHandler);

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