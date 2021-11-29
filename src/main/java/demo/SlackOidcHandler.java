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
import util.Jwts;

public class SlackOidcHandler implements HttpHandler {

  private final Gson gson = new Gson();

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String code = exchange.getRequestURI().toString().split("code=")[1];
    URI url = getOpenIdConnectUrl(code);
    HttpRequest request = HttpRequest.newBuilder(url).build();
    HttpClient client = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build();
    try {
      HttpResponse<String> response = client.send(request,
          BodyHandlers.ofString(StandardCharsets.US_ASCII));
      OpenIdResponse oidr = gson.fromJson(response.body(), OpenIdResponse.class);
      String tokenJson = Jwts.getPayload(oidr.getId_token());
      IdToken idt = gson.fromJson(tokenJson, IdToken.class);
      String sub = idt.getSub();
      // todo
      // look up sub in DB
      // if no sub, add and create new user in local DB with name
      // append local user to session data and set logged in
      // display successful page with name and link to main page
      byte[] bodyBytes = "Ok".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, bodyBytes.length);
      exchange.getResponseBody().write(bodyBytes);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
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
