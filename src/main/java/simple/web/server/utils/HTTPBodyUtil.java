package simple.web.server.utils;

import java.nio.charset.StandardCharsets;

public final class HTTPBodyUtil {
    public static int getBodyLength(String body) {
        return (body != null) ? body.getBytes(StandardCharsets.UTF_8).length : 0;
    }
}
