package demo;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;

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
    exchange.getResponseHeaders().put("Location", List.of(redirectUri));
    exchange.sendResponseHeaders(302, 0);
  }


  @Override
  public String description() {
    return null;
  }
}
