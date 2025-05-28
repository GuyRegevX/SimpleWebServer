package simple.web.enums;

import lombok.Getter;

@Getter
public enum HttpHeader {
    ContentType("Content-Type" ),
    ContentLength("Content-Length" );


    private final String label;

    HttpHeader(String label) {
        this.label = label;
    }
}
