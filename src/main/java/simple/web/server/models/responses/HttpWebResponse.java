package simple.web.server.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import simple.web.server.enums.HttpStatusCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class HttpWebResponse {
    private HttpStatusCode statusCode;
    @Builder.Default
    private Map<String, String> headers = new HashMap<>();
    @Builder.Default
    private Object data = null;
}
