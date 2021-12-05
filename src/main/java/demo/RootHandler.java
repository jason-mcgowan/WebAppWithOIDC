package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RootHandler implements HttpHandler {

  private final Config config;

  public RootHandler(Config config) {
    this.config = config;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    SessionData sd = (SessionData) exchange.getAttribute(SessionFilter.SESSION_DATA_ATT);
    Map<String, Object> model = new HashMap<>();
    model.put("displayName", sd.getDisplayName());
    model.put("viewEventsUrl", config.getWebHost() + config.getEventsPath());
    model.put("createEventUrl", config.getWebHost() + config.getCreateEventPath());
    model.put("transactionsUrl", config.getWebHost() + config.getTransactionsPath());
    model.put("accountUrl", config.getWebHost() + config.getAccountPath());
    model.put("logoutUrl", config.getWebHost() + config.getLogoutPath());
    String templatePath = "main.html";
    try {
      ExchangeTools.templateResponse(exchange, model, templatePath);
    } catch (TemplateException e) {
      e.printStackTrace();
    }
  }
}
