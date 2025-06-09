package simple.web.server.response;

import simple.web.server.models.responses.HttpWebResponse;

import java.io.IOException;
import java.net.Socket;

public interface ResponseSender {
    void send(Socket clientSocket, HttpWebResponse response) throws IOException;
}
