package simple.web.server.request.parsers;

import simple.web.server.enums.HttpMethod;
import simple.web.server.request.models.StatusLineData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class StatusLineParser {

    public StatusLineData parse(String statusLine) throws IOException {
         if (statusLine == null || statusLine.isEmpty()) {
             throw new IllegalArgumentException("Invalid HTTP request: empty request line");
         }

         // 1. First line: GET /shop/items?category=electronics&page=1 HTTP/1.1
         String[] requestParts = statusLine.split(" ");
         if (requestParts.length != 3) {
             throw new IllegalArgumentException("Invalid HTTP request line: " + statusLine);
         }

         String method = requestParts[0];
         HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());

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

         return new StatusLineData(httpMethod, path, queryParams);

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
