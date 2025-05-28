package simple.web.server.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum HttpStatusCode {
    OK(200, "OK", "The request has succeeded"),
    CREATED(201, "Created", "The request has succeeded and a new resource has been created"),
    ACCEPTED(202, "Accepted", "The request has been received but not yet completed"),
    BAD_REQUEST(400, "Bad Request", "The server cannot process the request due to client error"),
    NOT_FOUND(404, "Not Found", "The server cannot find the requested resource"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "The server encountered an internal error");

    private final int code;
    private final String statusMessage;
    private final String description;

    HttpStatusCode(int code, String statusMessage, String description) {
        this.code = code;
        this.statusMessage = statusMessage;
        this.description = description;
    }
}