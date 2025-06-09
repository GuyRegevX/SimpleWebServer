package simple.web.server.request.models;

import lombok.Builder;
import lombok.Getter;
import simple.web.server.enums.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public record StatusLineData(
        HttpMethod method,
        String path,
        Map<String, String> queryParams
) {
    public StatusLineData(HttpMethod method, String path) {
        this(method, path, new HashMap<>());
    }
}

