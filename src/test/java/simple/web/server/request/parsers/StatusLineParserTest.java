package simple.web.server.request.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import simple.web.server.enums.HttpMethod;
import simple.web.server.request.models.StatusLineData;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StatusLineParserTest {
    private StatusLineParser parser;

    @BeforeEach
    void setUp() {
        parser = new StatusLineParser();
    }

    @ParameterizedTest
    @MethodSource("validStatusLineDataProvider")
    void parse_ValidStatusLine_ReturnsStatusCodeAndReason(
            String statusLine,
            StatusLineData inputStatusLine) throws IOException {
        var statusLineData = parser.parse(statusLine);
        assertEquals(inputStatusLine, statusLineData, "Status line data does not match expected value");
    }

    static Stream<Arguments> validStatusLineDataProvider() {
        return Stream.of(
                Arguments.of("GET /index.html?GA=3 HTTP/1.1\\r\\n",
                        new StatusLineData(HttpMethod.GET, "/index.html", Map.of("GA", "3"))),
                Arguments.of("GET /api/test?Pace=rr&Car=Ford HTTP/1.1\\r\\n",
                        new StatusLineData(HttpMethod.GET, "/api/test", Map.of("Pace", "rr","Car", "Ford"))),
                Arguments.of("POST /api/login HTTP/1.1\\r\\n",
                        new StatusLineData(HttpMethod.POST, "/api/login")),
                Arguments.of("PUT /api/items/42 HTTP/1.1\\r\\n",
                        new StatusLineData(HttpMethod.PUT, "/api/items/42")),
                Arguments.of("DELETE /api/items/424 HTTP/1.1\\r\\n",
                        new StatusLineData(HttpMethod.DELETE, "/api/items/424"))
                );
    }

    @ParameterizedTest
    @CsvSource({
            "GET HTTP/1.1\\r\\n",
    })
    void parse_InvalidStatusLine_ThrowsException(String invalidStatusLine) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(invalidStatusLine)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void parse_NullOrEmptyStatusLine_ThrowsException(String input) {
        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(input)
        );
    }

    @Test
    void parse_WhitespaceStatusLine_ThrowsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse("   ")
        );
    }

    @Test
    void valueOf_ThrowsIllegalArgumentException_WhenMethodDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpMethod.valueOf("INVALID_METHOD"));
    }
}