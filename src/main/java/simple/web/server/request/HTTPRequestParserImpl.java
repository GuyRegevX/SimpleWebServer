package simple.web.server.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simple.web.server.models.requests.HTTPRequest;
import simple.web.server.request.parsers.StreamingBodyParser;
import simple.web.server.request.parsers.StreamingHeadersParser;
import simple.web.server.request.parsers.StatusLineParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HTTPRequestParserImpl implements HTTPRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HTTPRequestParserImpl.class);
    private final StatusLineParser statusLineParser;
    private final StreamingHeadersParser streamingHeadersParser;
    private final StreamingBodyParser streamingBodyParser;

    public HTTPRequestParserImpl(){
        this.statusLineParser = new StatusLineParser();
        this.streamingHeadersParser = new StreamingHeadersParser();
        this.streamingBodyParser = new StreamingBodyParser();
    }

    public HTTPRequest parse(BufferedReader in) throws IOException {
        // Parse the request line
        String requestLine = in.readLine();
        var statusLineData = statusLineParser.parse(requestLine);

        Map<String, String> headers  = streamingHeadersParser.parse(in);        // 3. Parse headers until empty line

        // Parse body if present
        String body = streamingBodyParser.parse(in, headers);

        // Build and return the HTTPRequest object
        return HTTPRequest.builder()
                .method(statusLineData.method())
                .path(statusLineData.path())
                .query(statusLineData.queryParams())
                .headers(headers)
                .body(body)
                .build();
    }
}