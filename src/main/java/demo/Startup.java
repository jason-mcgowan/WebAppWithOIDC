package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Startup {

  public static void main(String[] args) {
    try {
      runUntilShutdown(args[0]);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void runUntilShutdown(String clientSecret) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 64);
    server.createContext("/", Startup::landingPageRequest);
    server.start();
  }

  private static void landingPageRequest(HttpExchange exchange) throws IOException {
    String body = "Login link goes here: ";
    byte[] bodyBytes = body.getBytes(StandardCharsets.US_ASCII);
    exchange.sendResponseHeaders(200, bodyBytes.length);
    OutputStream os = exchange.getResponseBody();
    os.write(bodyBytes);
  }
}
