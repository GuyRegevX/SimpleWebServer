package simple.web.server.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import simple.web.server.Const;
import simple.web.server.enums.HttpHeader;
import simple.web.server.models.responses.HttpWebResponse;
import simple.web.server.utils.HTTPBodyUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class RestResponseSenderImpl implements ResponseSender{

    private final ObjectMapper  objectMapper;

    public RestResponseSenderImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void send(Socket clientSocket, HttpWebResponse httpWebResponse) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            String body = getBody(httpWebResponse);
            int contentLength = HTTPBodyUtil.getBodyLength(body);
            var headers = getHeaders(httpWebResponse, contentLength);

            String response = getStatusLine(httpWebResponse) +
                    Const.Html.LINE_ENDING +
                    headers;
            if (contentLength > 0) {
                // Add body to response if present.
                // Add a line separator before the body(Response format)
                response +=  Const.Html.LINE_ENDING +
                        body;
            }
            writer.write(response);
            writer.flush();
        }finally {
            if (clientSocket != null) {
                clientSocket.close(); // then close socket
            }
        }
    }

    private String getStatusLine(HttpWebResponse httpWebResponse){
        var statusCode = httpWebResponse.getStatusCode();
        return String.format("HTTP/1.1 %d %s", statusCode.getCode(), statusCode.getStatusMessage());
    }

    private String getHeaders(HttpWebResponse httpWebResponse, int contentLength){
        StringBuilder stringBuilder = new StringBuilder();
        httpWebResponse.getHeaders().put(HttpHeader.ContentLength.getLabel(), String.valueOf(contentLength));
        httpWebResponse.getHeaders().putIfAbsent(HttpHeader.ContentType.getLabel(), "application/json");
        httpWebResponse.getHeaders().forEach((key, value) -> {
            stringBuilder.append(key).append(": ").append(value).append(Const.Html.LINE_ENDING);
        });
        return stringBuilder.toString();
    }

    private String getBody(HttpWebResponse httpWebResponse) throws JsonProcessingException {
        var data = httpWebResponse.getData();
        if (httpWebResponse.getData() == null) {
            return "";
        }
        if(data instanceof String){
            return data.toString();
        }
        return objectMapper.writeValueAsString(httpWebResponse.getData());
    }


}
