package Network.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import common.Config;
import Db.DbStatements;
import common.ExchangeTools;
import Network.IdToken;
import Network.OpenIdResponse;
import common.Services;
import Network.SessionData;
import Network.SessionFilter;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import common.Jwts;

/**
 * Follows API from: https://api.slack.com/authentication/sign-in-with-slack
 *
 * @author Jason McGowan
 */
public class SlackOidcHandler implements HttpHandler {

  private final Gson gson = new Gson();
  private final Config config;

  public SlackOidcHandler() {
    this.config = Services.getInstance().getConfig();
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String code = exchange.getRequestURI().toString().split("code=")[1];
    URI url = getOpenIdConnectUrl(code);
    HttpRequest request = HttpRequest.newBuilder(url).build();
    HttpClient client = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build();
    HttpResponse<String> response;
    try {
      response = client.send(request, BodyHandlers.ofString(StandardCharsets.US_ASCII));
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    String message = completeLogin(exchange, response);
    Map<String, Object> model = new HashMap<>();
    model.put("message", message);
    model.put("mainUrl", config.getWebHost());
    try {
      ExchangeTools.templateResponse(exchange, model, "/message.html");
    } catch (TemplateException e) {
      e.printStackTrace();
    }
  }

  private String completeLogin(HttpExchange exchange, HttpResponse<String> response) {
    if (response.statusCode() != 200) {
      return "Internal error";
    }
    OpenIdResponse oidr = gson.fromJson(response.body(), OpenIdResponse.class);
    if (!oidr.getOk()) {
      return "Error retrieving data from Slack";
    }
    String tokenJson = Jwts.getPayload(oidr.getId_token());
    IdToken idt = gson.fromJson(tokenJson, IdToken.class);
    if (idt.getSub() == null || idt.getSub().isBlank()) {
      return "Error, Slack user data empty";
    }
    SessionData sessionData = (SessionData) exchange.getAttribute(SessionFilter.SESSION_DATA_ATT);
    try {
      addInfo(idt, sessionData);
      return "Welcome, " + sessionData.getDisplayName();
    } catch (SQLException e) {
      e.printStackTrace();
      return "Internal database error";
    }
  }

  private void addInfo(IdToken idt, SessionData sessionData) throws SQLException {
    String sub = idt.getSub();
    Map<Integer, String> users = DbStatements.slackSubQuery(sub);
    int localId = 0;
    String displayName = "";
    if (users.size() == 1) {
      for (Map.Entry<Integer, String> entry : users.entrySet()) {
        localId = entry.getKey();
        displayName = entry.getValue();
      }
    } else if (users.isEmpty()) {
      displayName = idt.getName();
      localId = DbStatements.insertNewLocalUser(displayName);
      DbStatements.insertNewSlackUser(sub, localId);
    } else {
      // todo throw some exception?
      return;
    }
    sessionData.setLocalUserId(localId);
    sessionData.setDisplayName(displayName);
    sessionData.setLoggedIn(true);
  }

  private URI getOpenIdConnectUrl(String code) {
    Config config = Services.getInstance().getConfig();
    return URI.create(
        "https://slack.com/api/openid.connect.token/?"
            + "client_id=" + config.getClient_id()
            + "&client_secret=" + config.getClient_secret()
            + "&code=" + code
            + "&redirect_uri=" + config.getWebHost() + config.getSlackCallbackUrl()
    );
  }
}
