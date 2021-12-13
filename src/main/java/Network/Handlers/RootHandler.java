package Network.Handlers;

import Network.SessionData;
import Network.SessionFilter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import common.Config;
import common.ExchangeTools;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Displays links
 *
 * @author Jason McGowan
 */
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
    model.put("viewEventsUrl", config.getEventsPath());
    model.put("createEventUrl", config.getCreateEventPath());
    model.put("transactionsUrl", config.getTransactionsPath());
    model.put("transferUrl", config.getTransferPath());
    model.put("accountUrl", config.getAccountPath());
    model.put("logoutUrl", config.getLogoutPath());
    String templatePath = "main.html";
    try {
      ExchangeTools.templateResponse(exchange, model, templatePath);
    } catch (TemplateException e) {
      e.printStackTrace();
    }
  }
}
