package demo;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import util.Jwts;

public class SlackOidcHandler implements HttpHandler {

  private final Gson gson = new Gson();

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
    if (response.statusCode() != 200) {
      // todo send error page
      return;
    }
    OpenIdResponse oidr = gson.fromJson(response.body(), OpenIdResponse.class);
    if (!oidr.getOk()) {
      // todo send error page
      return;
    }
    String tokenJson = Jwts.getPayload(oidr.getId_token());
    IdToken idt = gson.fromJson(tokenJson, IdToken.class);
    if (idt.getSub() == null || idt.getSub().isBlank()) {
      // todo send error page
      return;
    }
    SessionData sessionData = (SessionData) exchange.getAttribute(SessionFilter.SESSION_DATA_ATT);
    try {
      addInfo(idt, sessionData);
    } catch (SQLException e) {
      // todo log, show error page
      e.printStackTrace();
      return;
    }
    // todo display successful page with name and link to main page
    String body = "Welcome, " + sessionData.getDisplayName();
    byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(200, bodyBytes.length);
    exchange.getResponseBody().write(bodyBytes);
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
