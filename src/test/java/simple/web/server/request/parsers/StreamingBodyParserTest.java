package simple.web.server.request.parsers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import simple.web.server.enums.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StreamingBodyParserTest {


    @ParameterizedTest
    @CsvSource({
            "Hello, world!",
            "{\"name\": \"John Smith\"}",
            "{}",
            "{\n" +
                    "            \"name\": \"John Smith\",\n" +
                    "            \"age\": 30,\n" +
                    "            \"address\": {\n" +
                    "                \"street\": \"123 Main St\",\n" +
                    "                \"city\": \"Boston\"\n" +
                    "            }\n" +
                    "        }\n"
    })
    void testParseReturnsBodyWhenContentLengthIsProvided(String bodyContent) throws IOException {
        StreamingBodyParser parser = new StreamingBodyParser();
        BufferedReader in = new BufferedReader(new StringReader(bodyContent));
        Map<String, String> headers = Map.of(HttpHeader.ContentLength.getLabel(), String.valueOf(bodyContent.length()));

        String result = parser.parse(in, headers);

        assertEquals(bodyContent, result);
    }



    @Test
    void testParseReturnsEmptyBodyWhenContentLengthIsZero() throws IOException {
        StreamingBodyParser parser = new StreamingBodyParser();
        BufferedReader in = new BufferedReader(new StringReader(""));
        Map<String, String> headers = Map.of(HttpHeader.ContentLength.getLabel(), "0");

        String result = parser.parse(in, headers);

        assertEquals("", result);
    }

    @Test
    void testParseReturnsEmptyBodyWhenContentJSON() throws IOException {
        StreamingBodyParser parser = new StreamingBodyParser();
        String bodyContent = "{\"name\": \"John Smith\"}";

        BufferedReader in = new BufferedReader(new StringReader("{\"name\": \"John Smith\"}"));
        Map<String, String> headers = Map.of(HttpHeader.ContentLength.getLabel(), "0");

        String result = parser.parse(in, headers);

        assertEquals("", result);
    }

    @Test
    void testParseThrowsIOExceptionWhenInputStreamFails() {
        StreamingBodyParser parser = new StreamingBodyParser();
        BufferedReader in = new BufferedReader(new BufferedReader(new StringReader(""))) {
            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                throw new IOException("Forced IO Exception");
            }
        };
        Map<String, String> headers = Map.of(HttpHeader.ContentLength.getLabel(), "10");

        assertThrows(IOException.class, () -> parser.parse(in, headers));
    }
}