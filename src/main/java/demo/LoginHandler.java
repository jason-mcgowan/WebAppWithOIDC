package demo;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import util.HtmlBuilder;
import util.SlackTools;

public class LoginHandler implements HttpHandler {

  private final Config config;
  private final String redirectUri;

  public LoginHandler(Config config) {
    this.config = config;
    this.redirectUri = config.getWebHost() + config.getSlackCallbackUrl();
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String button = SlackTools.loginButton(redirectUri, config.getClient_id());
    String body = HtmlBuilder.simplePage("Login", "Login", button);
    byte[] bodyBytes = body.getBytes(StandardCharsets.US_ASCII);
    exchange.sendResponseHeaders(200, bodyBytes.length);
    OutputStream os = exchange.getResponseBody();
    os.write(bodyBytes);
  }
}
