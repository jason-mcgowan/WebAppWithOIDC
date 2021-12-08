package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import util.HttpTools;

public class CreateEventHandler implements HttpHandler {

  private final Config config;

  public CreateEventHandler(Config config) {
    this.config = config;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    SessionData sd = (SessionData) exchange.getAttribute(SessionFilter.SESSION_DATA_ATT);
    Map<String, Object> model = new HashMap<>();
    model.put("mainUrl", config.getWebHost());
    model.put("displayName", sd.getDisplayName());
    String templatePath;
    if (exchange.getRequestMethod().equals("POST")) {
      templatePath = "/message.html";
      String message = createEvent(exchange, sd.getLocalUserId());
      model.put("message", message);
    } else {
      model.put("displayName", sd.getDisplayName());
      templatePath = "/event/create/GET.html";
    }
    try {
      ExchangeTools.templateResponse(exchange, model, templatePath);
    } catch (TemplateException e) {
      e.printStackTrace();
    }
  }

  private String createEvent(HttpExchange exchange, int creatorId) throws IOException {
    String payload = new String(exchange.getRequestBody().readAllBytes());
    Map<String, String> pairs;
    try {
      pairs = HttpTools.parseUrlEncodedPostPayload(payload);
    } catch (IllegalArgumentException e) {
      return e.getLocalizedMessage();
    }
    EventData event;
    try {
      event = new EventData(pairs);
    } catch (IllegalArgumentException e) {
      return e.getLocalizedMessage();
    }
    try {
      DbStatements.createEvent(event, creatorId);
      return "Event created";
    } catch (SQLException e) {
      e.printStackTrace();
      return "Error creating event";
    }
  }
}
