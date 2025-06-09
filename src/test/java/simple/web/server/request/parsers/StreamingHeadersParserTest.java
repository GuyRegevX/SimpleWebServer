package simple.web.server.request.parsers;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamingHeadersParserTest {

    @Test
    void testParseWithValidHeaders() throws IOException {
        // Arrange
        String headersInput = "Host: localhost\r\nContent-Type: text/plain\r\nContent-Length: 123\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(headersInput));
        StreamingHeadersParser parser = new StreamingHeadersParser();

        // Act
        Map<String, String> headers = parser.parse(bufferedReader);

        // Assert
        assertEquals(3, headers.size());
        assertEquals("localhost", headers.get("Host"));
        assertEquals("text/plain", headers.get("Content-Type"));
        assertEquals("123", headers.get("Content-Length"));
    }

    @Test
    void testParseWithEmptyInput() throws IOException {
        // Arrange
        String headersInput = "";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(headersInput));
        StreamingHeadersParser parser = new StreamingHeadersParser();

        // Act
        Map<String, String> headers = parser.parse(bufferedReader);

        // Assert
        assertTrue(headers.isEmpty());
    }

    @Test
    void testParseWithInvalidHeaderFormat() throws IOException {
        // Arrange
        String headersInput = "InvalidHeaderLine\r\nContent-Type: text/html\r\nX-Custom-Header: CustomValue\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(headersInput));
        StreamingHeadersParser parser = new StreamingHeadersParser();

        // Act
        Map<String, String> headers = parser.parse(bufferedReader);

        // Assert
        assertEquals(2, headers.size());
        assertEquals("text/html", headers.get("Content-Type"));
        assertEquals("CustomValue", headers.get("X-Custom-Header"));
    }

    @Test
    void testParseWithEmptyHeaderLine() throws IOException {
        // Arrange
        String headersInput = "Host: localhost\r\n\r\nUser-Agent: Mozilla/5.0\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(headersInput));
        StreamingHeadersParser parser = new StreamingHeadersParser();

        // Act
        Map<String, String> headers = parser.parse(bufferedReader);

        // Assert
        assertEquals(1, headers.size());
        assertEquals("localhost", headers.get("Host"));
    }

    @Test
    void testParseWithWhitespaceHeaderLine() throws IOException {
        // Arrange
        String headersInput = "Accept: */*\r\n \r\nConnection: keep-alive\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(headersInput));
        StreamingHeadersParser parser = new StreamingHeadersParser();

        // Act
        Map<String, String> headers = parser.parse(bufferedReader);

        // Assert
        assertEquals(2, headers.size());
        assertEquals("*/*", headers.get("Accept"));
        assertEquals("keep-alive", headers.get("Connection"));
    }
}