package simple.web.server;

public final class Const {
    private Const() {} // Prevent instantiation

    public static final class Server {

        public static final int PORT = 8080;
        public static final String HOST = "localhost";
        public static final int TIMEOUT = 5000;
    }

    public static final class Html {
        public static final String CONTENT_TYPE = "text/html";
        public static final String CHARSET = "UTF-8";
        public static final String DOCTYPE = "<!DOCTYPE html>";
        public static final String LINE_ENDING = "\r\n";

    }
}