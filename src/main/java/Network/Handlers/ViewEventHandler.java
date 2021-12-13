package Network.Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import common.Config;
import Db.DbStatements;
import Db.EventData;
import common.ExchangeTools;
import Network.SessionData;
import Network.SessionFilter;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import common.HttpTools;

public class ViewEventHandler implements HttpHandler {

  private final Config config;

  public ViewEventHandler(Config config) {
    this.config = config;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    int eventId = getEvent(exchange);
    if (eventId == 0) {
      String search;
      if (exchange.getRequestMethod().equals("POST")) {
        search = getSearchField(exchange);
      } else {
        search = "";
      }
      listEvents(exchange, search);
    } else {
      handleSubEvent(eventId, exchange);
    }
  }

  private String getSearchField(HttpExchange exchange) throws IOException {
    String payload = new String(exchange.getRequestBody().readAllBytes());
    Map<String, String> pairs;
    try {
      pairs = HttpTools.parseUrlEncodedPostPayload(payload);
    } catch (IllegalArgumentException e) {
      return "";
    }
    String term = pairs.get("search");
    if (term == null) {
      return "";
    }
    return term;
  }

  private void handleSubEvent(int eventId, HttpExchange exchange) throws IOException {
    EventData event;
    try {
      event = DbStatements.getEvent(eventId);
    } catch (SQLException e) {
      sendErrorMessage(exchange);
      return;
    }
    if (event == null) {
      sendErrorMessage(exchange);
      return;
    }
    if (exchange.getRequestMethod().equals("POST")) {
      doPostPurchase(event, exchange);
    } else {
      doShow(event, exchange);
    }

  }

  private void doShow(EventData event, HttpExchange exchange) throws IOException {
    Map<String, Object> model = new HashMap<>();
    String templatePath = "/events/show.html";
    model.put("mainUrl", "/");
    model.put("event", event);
    try {
      ExchangeTools.templateResponse(exchange, model, templatePath);
    } catch (TemplateException e) {
      e.printStackTrace();
    }
  }

  private void doPostPurchase(EventData eventData, HttpExchange exchange) throws IOException {
    String payload = new String(exchange.getRequestBody().readAllBytes());
    Map<String, String> pairs;
    try {
      pairs = HttpTools.parseUrlEncodedPostPayload(payload);
    } catch (IllegalArgumentException e) {
      sendErrorMessage(exchange);
      return;
    }
    String quantityString = pairs.get("quantity");
    int quantity;
    try {
      quantity = Integer.parseInt(quantityString);
    } catch (NumberFormatException e) {
      sendErrorMessage(exchange);
      return;
    }
    if (quantity < 0 || quantity > eventData.getQuantity()) {
      sendErrorMessage(exchange);
      return;
    }
    SessionData sd = (SessionData) exchange.getAttribute(SessionFilter.SESSION_DATA_ATT);
    try {
      DbStatements.purchaseTickets(quantity, eventData.getId(), sd.getLocalUserId());
    } catch (SQLException e) {
      e.printStackTrace();
      sendErrorMessage(exchange);
      return;
    }
    Map<String, Object> model = new HashMap<>();
    String templatePath = "/message.html";
    model.put("mainUrl", "/");
    model.put("message", "Purchase complete");
    try {
      ExchangeTools.templateResponse(exchange, model, templatePath);
    } catch (TemplateException e) {
      e.printStackTrace();
    }
  }

  private void sendErrorMessage(HttpExchange exchange) throws IOException {
    Map<String, Object> model = new HashMap<>();
    String templatePath = "/message.html";
    model.put("mainUrl", "/");
    model.put("message", "Error looking up event");
    try {
      ExchangeTools.templateResponse(exchange, model, templatePath);
    } catch (TemplateException e) {
      e.printStackTrace();
    }
  }

  private void listEvents(HttpExchange exchange, String search) throws IOException {
    Collection<EventData> events;
    try {
      events = DbStatements.getEvents(search);
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

  private int getEvent(HttpExchange exchange) {
    String path = exchange.getRequestURI().getPath();
    String subPath = path.substring(config.getEventsPath().length());
    String eventStr = subPath.replaceAll("/", "");
    int event;
    try {
      event = Integer.parseInt(eventStr);
    } catch (NumberFormatException e) {
      event = 0;
    }
    return event;
  }
}
