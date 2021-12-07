package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AccountHandler implements HttpHandler {

  private final Config config;

  public AccountHandler(Config config) {
    this.config = config;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    SessionData sd = (SessionData) exchange.getAttribute(SessionFilter.SESSION_DATA_ATT);
    Map<String, Object> model = new HashMap<>();
    model.put("mainUrl", config.getWebHost());
    model.put("userId", sd.getLocalUserId());
    String templatePath;
    if (exchange.getRequestMethod().equals("POST")) {
      templatePath = "/message.html";
      String newName = getName(exchange);
      String message;
      if (newName.isBlank()) {
        message = "Error, invalid name";
      } else if (newName.length() >= 255) {
        message = "Error, name must be less than 255 characters long";
      } else {
        try {
          DbStatements.updateUserName(newName, sd.getLocalUserId());
          sd.setDisplayName(newName);
          message = "Name change complete";
        } catch (SQLException e) {
          message = "Error with name";
        }
      }
      model.put("message", message);
    } else {
      model.put("displayName", sd.getDisplayName());
      templatePath = "/account/GET.html";
    }
    try {
      ExchangeTools.templateResponse(exchange, model, templatePath);
    } catch (TemplateException e) {
      e.printStackTrace();
    }
  }

  private String getName(HttpExchange exchange) throws IOException {
    String body = new String(exchange.getRequestBody().readAllBytes());
    String key = "name=";
    if (!body.startsWith(key)){
      return "";
    }
    String newName = body.substring(key.length());
    return URLDecoder.decode(newName, StandardCharsets.UTF_8);
  }
}