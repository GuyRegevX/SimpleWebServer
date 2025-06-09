package simple.web.server.request.parsers;

import simple.web.server.enums.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class StreamingBodyParser {
    public String parse(BufferedReader in, Map<String, String> headers) throws IOException {
        String body = "";
        int contentLength = Optional.ofNullable(headers.get(HttpHeader.ContentLength.getLabel()))
                .map(Integer::parseInt)
                .orElse(0);
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);
        }
        return body;
    }
}
