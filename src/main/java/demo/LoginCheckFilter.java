package demo;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import util.HtmlBuilder;

public class LoginCheckFilter extends Filter {

  private final String redirectUri;

  public LoginCheckFilter(String redirectUri) {
    this.redirectUri = redirectUri;
  }

  @Override
  public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
    Object sdo = exchange.getAttribute(SessionFilter.SESSION_DATA_ATT);
    if (sdo == null) {
      redirect(exchange);
      return;
    }
    if (!(sdo instanceof SessionData sd)) {
      redirect(exchange);
      return;
    }
    if (!sd.isLoggedIn()) {
      redirect(exchange);
      return;
    }
    chain.doFilter(exchange);
  }

  private void redirect(HttpExchange exchange) throws IOException {
    String body = "Please log in: <a href=\"" + redirectUri + "\">Link</a>";
    String payload = HtmlBuilder.simplePage("", "Please Log In", body);
    byte[] bytes = payload.getBytes(StandardCharsets.US_ASCII);
    exchange.sendResponseHeaders(200, bytes.length);
    OutputStream os = exchange.getResponseBody();
    os.write(bytes);
  }


  @Override
  public String description() {
    return null;
  }
}
