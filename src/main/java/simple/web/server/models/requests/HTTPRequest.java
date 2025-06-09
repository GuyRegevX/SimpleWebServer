package simple.web.server.models.requests;

import lombok.Builder;
import lombok.Data;
import simple.web.server.enums.HttpMethod;

import java.util.Map;

@Data
@Builder
public class HTTPRequest {
    private HttpMethod method;
    private Map<String, String> headers;
    private String path;
    private Map<String, String> query;
    private String body;
}
