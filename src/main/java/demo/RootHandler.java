package demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
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
    Template temp = Services.getInstance().getFreemarkerCfg().getTemplate("main.html");
    StringWriter sw = new StringWriter();
    try {
      temp.process(model, sw);
    } catch (TemplateException e) {
      e.printStackTrace();
      return;
    }
    byte[] msgBytes = sw.toString().getBytes(StandardCharsets.US_ASCII);
    exchange.sendResponseHeaders(200, msgBytes.length);
    OutputStream os = exchange.getResponseBody();
    os.write(msgBytes);
    os.close();
  }
}
