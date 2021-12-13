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
 * Logs user out. Session Filter will remove their session from its memory.
 *
 * @author Jason McGowan
 */
public class LogoutHandler implements HttpHandler {

  private final Config config;

  public LogoutHandler(Config config) {
    this.config = config;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    SessionData sd = (SessionData) exchange.getAttribute(SessionFilter.SESSION_DATA_ATT);
    sd.setLogoutRequested(true);
    Map<String, Object> model = new HashMap<>();
    model.put("mainUrl", config.getWebHost());
    model.put("message", "Logged out");
    String templatePath = "/message.html";
    try {
      ExchangeTools.templateResponse(exchange, model, templatePath);
    } catch (TemplateException e) {
      e.printStackTrace();
    }
  }
}
