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
    SessionData sd = (SessionData) exchange.getAttribute(SessionFilter.SESSION_DATA_ATT);
    String bodyText;
    if (sd.isLoggedIn()) {
      bodyText = "You are already logged in, please log out if you wish to switch accounts";
    } else {
      bodyText = SlackTools.loginButton(redirectUri, config.getClient_id());
    }
    String bodyHtml = HtmlBuilder.simplePage("Login", "Login", bodyText);
    byte[] msgBytes = bodyHtml.getBytes(StandardCharsets.US_ASCII);
    exchange.sendResponseHeaders(200, msgBytes.length);
    OutputStream os = exchange.getResponseBody();
    os.write(msgBytes);
    os.close();
  }
}
