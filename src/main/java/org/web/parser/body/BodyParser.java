package org.web.parser.body;

import org.web.enums.HttpHeader;

import java.io.BufferedReader;
import java.util.Map;

public class BodyParser {

    public String parse(BufferedReader in, Map<String, String> headers){
        var contentLength = Integer.parseInt(headers.get(HttpHeader.ContentLength.getLabel()));
        // Parse body if present
        String body = null;
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);
        }
    }
}
