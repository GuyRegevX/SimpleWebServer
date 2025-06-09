package simple.web.server.hanlder;

import simple.web.server.enums.HttpStatusCode;
import simple.web.server.models.responses.HttpWebResponse;
import simple.web.server.request.HTTPRequestParser;
import simple.web.server.response.ResponseSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestHandlerImpl implements RequestHandler {

    private final HTTPRequestParser httpRequestParser;
    private final ResponseSender responseSender;

    public RequestHandlerImpl(HTTPRequestParser httpRequestParser, ResponseSender responseSender) {
        this.httpRequestParser = httpRequestParser;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(Socket socket) {
        try (socket; BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            try {

                var httpRequest = httpRequestParser.parse(in);
                var response = HttpWebResponse.builder().statusCode(HttpStatusCode.ACCEPTED).build();
                responseSender.send(socket ,response);


            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
                throw e;
            }
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }


    }
}
