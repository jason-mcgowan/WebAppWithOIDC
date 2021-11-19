package demo;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CookieFilter extends Filter {

  public static final String COOKIE_MAP_ATT = "cookies";

  @Override
  public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
    List<String> cookies = exchange.getRequestHeaders().get("Cookie");
    Map<String, String> cookieMap = new HashMap<>();
    for (String cookie : cookies) {
      int i = cookie.indexOf("=");
      String key = cookie.substring(0, i);
      String value = cookie.substring(i + 1);
      cookieMap.put(key, value);
    }
    exchange.setAttribute(COOKIE_MAP_ATT, cookieMap);
    chain.doFilter(exchange);
  }

  @Override
  public String description() {
    return
        "Pulls key-value pairs from Cookie: request header, adds them to a Map<String, String> and attaches them to the exchange as "
            + COOKIE_MAP_ATT;
  }
}
