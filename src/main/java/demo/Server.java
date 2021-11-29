package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {

  private static final String LOGIN_PATH = "/login/";

  private final Config config;

  public Server(Config config) {
    this.config = config;
  }

  private static void printExchangeRequestInfo(HttpExchange exchange) throws IOException {
    System.out.println("Request received");
    System.out.println(exchange.getRequestMethod() + exchange.getRequestURI());
    System.out.println("Headers");
    exchange.getRequestHeaders().forEach((k, v) -> System.out.println(k + " " + v));
    System.out.println("Body");
    byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
    System.out.println(new String(bodyBytes, StandardCharsets.US_ASCII));
  }

  public void start() throws IOException {
    ConcurrentMap<String, SessionData> sessions = new ConcurrentHashMap<>();
    LoginCheckFilter loginCheckFilter = new LoginCheckFilter(config.getWebHost() + LOGIN_PATH);
    SessionFilter sessionFilter = new SessionFilter(sessions);

    HttpServer server = HttpServer.create(new InetSocketAddress(8080), -1);
    server.createContext(LOGIN_PATH, new LoginHandler(config)).getFilters()
        .add(sessionFilter);
    server.createContext(config.getSlackCallbackUrl(), new SlackOidcHandler(config)).getFilters()
        .add(sessionFilter);

    server.start();
  }
}
