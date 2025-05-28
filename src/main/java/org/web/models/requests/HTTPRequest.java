package org.web.models.requests;

import lombok.Builder;
import lombok.Data;
import org.web.enums.HttpMethod;

import java.util.Map;

@Data
@Builder
public abstract class HTTPRequest {
    private HttpMethod method;
    private Map<String, String> headers;
    private String path;
    private String query;
    private String body;
}
