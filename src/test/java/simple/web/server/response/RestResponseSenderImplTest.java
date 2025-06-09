package simple.web.server.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simple.web.server.Const;
import simple.web.server.enums.HttpStatusCode;
import simple.web.server.models.responses.HttpWebResponse;
import simple.web.server.utils.HTTPBodyUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;


class RestResponseSenderImplTest {

    private ObjectMapper objectMapper;
    private RestResponseSenderImpl restResponseSender;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        restResponseSender = new RestResponseSenderImpl(objectMapper);
    }

    @Test
    public void Send_ServerUnavailable_NoBodyResponse() throws IOException {

        // Set up the mocks
        Socket mockSocket = mock(Socket.class);
        OutputStream mockOutputStream = mock(OutputStream.class);

        // Set up the mock behavior
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        var response = HttpWebResponse.builder().statusCode(HttpStatusCode.SERVER_UNAVAILABLE_ERROR).build();

        restResponseSender.send(mockSocket, response);
        verify(mockOutputStream).write(argThat(bytes -> {
            // Convert actual and expected to strings for debugging
            String actual = new String(bytes, 0, 85, StandardCharsets.UTF_8);
            String expected = String.join(Const.Html.LINE_ENDING,
                    "HTTP/1.1 503 Service Unavailable",
                    "Content-Length: 0",
                    "Content-Type: application/json",
                    "");


            return actual.equals(expected);
        }), eq(0), eq(85));

        verify(mockOutputStream, atLeastOnce()).flush();
        verify(mockSocket).close();
     }

    @Test
    public void Send_OKWithBodyAsPlainString_BodyResponseAsString() throws IOException {

        // Set up the mocks
        Socket mockSocket = mock(Socket.class);
        OutputStream mockOutputStream = mock(OutputStream.class);

        // Set up the mock behavior
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        var body = "Hello World!";
        int contentLength = HTTPBodyUtil.getBodyLength(body);
        var response = HttpWebResponse.builder()
                .statusCode(HttpStatusCode.OK)
                .data(body)
                .build();

        restResponseSender.send(mockSocket, response);
        verify(mockOutputStream).write(argThat(bytes -> {
            // Convert actual and expected to strings for debugging
            String actual = new String(bytes, 0, 83, StandardCharsets.UTF_8);
            String expected = String.join(Const.Html.LINE_ENDING,
                    "HTTP/1.1 200 OK",
                    "Content-Length: "+contentLength,
                    "Content-Type: application/json",
                    Const.Html.LINE_ENDING+body
                    );

            return actual.equals(expected);
        }), eq(0), eq(83));

        verify(mockOutputStream, atLeastOnce()).flush();
        verify(mockSocket).close();
    }

    @Test
    public void Send_CreatedWithBodyAsObject_BodyResponseAsJson() throws IOException {

        record TestResponse(String message, int id) {}

        TestResponse testResponse = new TestResponse("Some Message", 123);
        var body = objectMapper.writeValueAsString(testResponse);

        // Set up the mocks
        Socket mockSocket = mock(Socket.class);
        OutputStream mockOutputStream = mock(OutputStream.class);

        // Set up the mock behavior
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        int contentLength = HTTPBodyUtil.getBodyLength(body);
        var response = HttpWebResponse.builder()
                .statusCode(HttpStatusCode.CREATED)
                .data(testResponse)
                .build();

        restResponseSender.send(mockSocket, response);
        verify(mockOutputStream).write(argThat(bytes -> {
            // Convert actual and expected to strings for debugging
            String actual = new String(bytes, 0, 111, StandardCharsets.UTF_8);
            String expected = String.join(Const.Html.LINE_ENDING,
                    "HTTP/1.1 201 Created",
                    "Content-Length: "+contentLength,
                    "Content-Type: application/json",
                    Const.Html.LINE_ENDING+body
            );

            return actual.equals(expected);
        }), eq(0), eq(111));

        verify(mockOutputStream, atLeastOnce()).flush();
        verify(mockSocket).close();
    }

}