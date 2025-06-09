package simple.web.server.hanlder;

import java.io.IOException;
import java.net.Socket;

public interface RequestHandler {
    void handle(Socket socket) throws IOException, IllegalArgumentException;
}
