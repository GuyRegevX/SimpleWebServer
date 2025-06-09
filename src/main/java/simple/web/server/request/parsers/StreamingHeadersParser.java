package simple.web.server.request.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StreamingHeadersParser{

    public Map<String, String> parse(BufferedReader in) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String headerLine;

        while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerParts = headerLine.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }
        return headers;
    }

}
