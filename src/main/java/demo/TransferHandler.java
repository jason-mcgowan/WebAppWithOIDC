package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import util.HttpTools;

public class TransferHandler implements HttpHandler {

  private final Config config;

  public TransferHandler(Config config) {
    this.config = config;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    SessionData sd = (SessionData) exchange.getAttribute(SessionFilter.SESSION_DATA_ATT);
    Map<String, Object> model = new HashMap<>();
    model.put("mainUrl", "/");
    model.put("displayName", sd.getDisplayName());
    String templatePath;
    if (exchange.getRequestMethod().equals("POST")) {
      templatePath = doPost(exchange, sd, model);
    } else {
      templatePath = "/transfer/GET.html";
    }
    try {
      ExchangeTools.templateResponse(exchange, model, templatePath);
    } catch (TemplateException e) {
      e.printStackTrace();
    }
  }

  private String doPost(HttpExchange exchange, SessionData sd, Map<String, Object> model)
      throws IOException {
    String payload = new String(exchange.getRequestBody().readAllBytes());
    Map<String, String> pairs = HttpTools.parseUrlEncodedPostPayload(payload);
    String eventIdStr = pairs.get("eventId");
    String quantityStr = pairs.get("quantity");
    String toUserIdStr = pairs.get("toUserId");
    if (eventIdStr == null || quantityStr == null || toUserIdStr == null) {
      model.put("message", "Invalid request");
      return "/message.html";
    }
    int eventId;
    int quantity;
    int toUserId;
    try {
      eventId = Integer.parseInt(eventIdStr);
      quantity = Integer.parseInt(quantityStr);
      toUserId = Integer.parseInt(toUserIdStr);
    } catch (NumberFormatException e) {
      model.put("message", "Invalid request");
      return "/message.html";
    }
    try {
      DbStatements.transferTickets(eventId, sd.getLocalUserId(), toUserId, quantity);
    } catch (SQLException e) {
      model.put("message", "Invalid request");
      return "/message.html";
    }
    model.put("message", "Transfer complete");
    return "/message.html";
  }

}
