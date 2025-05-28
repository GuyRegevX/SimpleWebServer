package org.web.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class HttpResponse<T> {
    private int statusCode;
    private final Map<String, List<String>> headers;
    private T data;
}
