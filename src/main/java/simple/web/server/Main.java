package simple.web.server;

import org.example.controller.AssetVulnerabilityController;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        var webServer = new WebServer();
        webServer.startServer();
    }
}