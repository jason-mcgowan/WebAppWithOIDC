package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ViewEventHandler implements HttpHandler {

  private final Config config;

  public ViewEventHandler(Config config) {
    this.config = config;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    Collection<EventData> events;
    try {
      events = DbStatements.getEvents();
    } catch (SQLException e) {
      e.printStackTrace();
      events = new LinkedList<>();
    }
    Map<String, Object> model = new HashMap<>();
    model.put("mainUrl", config.getWebHost());
    model.put("eventsPath", config.getEventsPath());
    model.put("events", events);
    try {
      ExchangeTools.templateResponse(exchange, model, "/events/main.html");
    } catch (TemplateException e) {
      e.printStackTrace();
    }
  }
}
