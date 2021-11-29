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

  private Config config;

  public SlackOidcHandler(Config config) {
    this.config = config;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String code = exchange.getRequestURI().toString().split("code=")[1];
    URI url = getOpenIdConnectUrl(code);
    HttpRequest request = HttpRequest.newBuilder(url).build();
    HttpClient client = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build();
    try {
      HttpResponse<String> response = client.send(request,
          BodyHandlers.ofString(StandardCharsets.US_ASCII));
      Gson gson = new Gson();
      OpenIdResponse oidr = gson.fromJson(response.body(), OpenIdResponse.class);
      String tokenJson = Jwts.getPayload(oidr.getId_token());
      IdToken idt = gson.fromJson(tokenJson, IdToken.class);
      byte[] bodyBytes = "Ok".getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(200, bodyBytes.length);
      exchange.getResponseBody().write(bodyBytes);
      // check if token came back ok
      // get email and name
      // update session info
      // send user to logged in page
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // todo get id_token and decode
    // todo send user response
  }

  private URI getOpenIdConnectUrl(String code) {
    return URI.create(
        "https://slack.com/api/openid.connect.token/?"
            + "client_id=" + config.getClient_id()
            + "&client_secret=" + config.getClient_secret()
            + "&code=" + code
            + "&redirect_uri=" + config.getWebHost() + config.getSlackCallbackUrl()
    );
  }
}
