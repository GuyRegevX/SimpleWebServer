package simple.web.parser;

import lombok.extern.slf4j.Slf4j;
import simple.web.models.requests.HTTPRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HTTPRequestParser {

    public HTTPRequest parse(BufferedReader in) throws IOException {
        // Parse the request line
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Invalid HTTP request: empty request line");
        }

        // 1. First line: GET /shop/items?category=electronics&page=1 HTTP/1.1
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length != 3) {
            throw new IOException("Invalid HTTP request line: " + requestLine);
        }

        String method = requestParts[0];
        String fullPath = requestParts[1];

        // Parse path and query parameters
        String path;
        Map<String, String> queryParams = new HashMap<>();

        int queryStart = fullPath.indexOf('?');
        if (queryStart != -1) {
            path = fullPath.substring(0, queryStart);
            String queryString = fullPath.substring(queryStart + 1);
            queryParams = parseQueryParameters(queryString);
        } else {
            path = fullPath;
        }

        // 3. Parse headers until empty line
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        int contentLength = 0;

        while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerParts = headerLine.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
                if (headerParts[0].equalsIgnoreCase("Content-Length")) {
                    contentLength = Integer.parseInt(headerParts[1]);
                }
            }
        }

        // Parse body if present
        String body = null;
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);
        }

        // Build and return the HTTPRequest object
        return HTTPRequest.builder()
                .method(method)
                .path(path)
                .queryParameters(queryParams)
                .headers(headers)
                .body(body)
                .build();
    }

    private Map<String, String> parseQueryParameters(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        String[] pairs = queryString.split("&");

        for (String pair : pairs) {
            int equalIndex = pair.indexOf('=');
            if (equalIndex > 0) {
                String key = pair.substring(0, equalIndex);
                String value = pair.substring(equalIndex + 1);
                queryParams.put(key, value);
            }
        }

        return queryParams;
    }
}