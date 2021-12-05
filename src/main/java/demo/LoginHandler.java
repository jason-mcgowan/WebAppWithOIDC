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
import util.SlackTools;

public class LoginHandler implements HttpHandler {

  private final Config config;
  private final String redirectUri;

  public LoginHandler(Config config) {
    this.config = config;
    this.redirectUri = config.getWebHost() + config.getSlackCallbackUrl();
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    SessionData sd = (SessionData) exchange.getAttribute(SessionFilter.SESSION_DATA_ATT);
    Template temp;
    Map<String, Object> model = new HashMap<>();
    if (sd.isLoggedIn()) {
      temp = Services.getInstance().getFreemarkerCfg().getTemplate("login/alreadyLoggedIn.html");
      model.put("mainUrl", config.getWebHost() + config.getMainPath());
      model.put("logoutUrl", config.getWebHost() + config.getLogoutPath());
    } else {
      temp = Services.getInstance().getFreemarkerCfg().getTemplate("login/loginPrompt.html");
      String slackButton = SlackTools.loginButton(redirectUri, config.getClient_id());
      model.put("slackButton", slackButton);
    }
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
