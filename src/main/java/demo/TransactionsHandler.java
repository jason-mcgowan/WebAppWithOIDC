package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import util.HttpTools;

public class TransactionsHandler implements HttpHandler {

  private final Config config;

  public TransactionsHandler(Config config) {
    this.config = config;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    SessionData sd = (SessionData) exchange.getAttribute(SessionFilter.SESSION_DATA_ATT);
    Collection<EventData> events;
    try {
      events = DbStatements.getUserTransactions(sd.getLocalUserId());
    } catch (SQLException e) {
      try {
        ExchangeTools.homeMessageResponse(exchange, "Database error");
      } catch (TemplateException ex) {
        ex.printStackTrace();
      }
      return;
    }
    Map<String, Object> model = new HashMap<>();
    model.put("mainUrl", "/");
    model.put("eventsPath", config.getEventsPath());
    model.put("events", events);
    String templatePath = "/transactions/main.html";
    try {
      ExchangeTools.templateResponse(exchange, model, templatePath);
    } catch (TemplateException e) {
      e.printStackTrace();
      return;
    }
  }
}
