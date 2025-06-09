package simple.web.server.request;

import simple.web.server.models.requests.HTTPRequest;

import java.io.BufferedReader;
import java.io.IOException;

public interface HTTPRequestParser {
    HTTPRequest parse(BufferedReader in) throws IOException;
}
