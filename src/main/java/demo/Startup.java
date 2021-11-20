package demo;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Startup {

  public static void main(String[] args) {
    try {
      runUntilShutdown(args[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void runUntilShutdown(String clientSecret)
      throws IOException, InterruptedException {
    ConcurrentMap<String, SessionData> sessions = new ConcurrentHashMap<>();
    SessionFilter sessionFilter = new SessionFilter(sessions);

    HttpServer server = HttpServer.create(new InetSocketAddress(8080), -1);
    server.createContext("/", Startup::landingPageRequest).getFilters().add(sessionFilter);
    server.createContext("/openid/callback/", Startup::openIdCallback).getFilters()
        .add(sessionFilter);
    server.start();
  }

  private static void openIdCallback(HttpExchange exchange) throws IOException {
    System.out.println("/openid/callback/ request received");
    printExchangeRequestInfo(exchange);
    String code = exchange.getRequestURI().toString().split("code=")[1];
    System.out.println("code: " + code);
    URI url = getOpenIdConnectUrl(code);
    System.out.println("request URL is: " + url);
    HttpRequest request = HttpRequest.newBuilder(url)
        .build();
    System.out.println("Request URI is: " + request.uri());
    HttpClient client = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS)
        .build();
    try {
      System.out.println("Sending request for token");
      HttpResponse<String> response = client.send(request,
          BodyHandlers.ofString(StandardCharsets.US_ASCII));
      Gson gson = new Gson();
      System.out.println("Status code: " + response.statusCode());
      System.out.println("URI: " + response.uri());
      System.out.println("Headers: " + response.headers().toString());
      System.out.println("Body: " + response.body());
      OpenIdResponse oidr = gson.fromJson(response.body(), OpenIdResponse.class);
      System.out.println("OIDR object: " + oidr);
      System.out.println("OIDR token: " + oidr.getId_token());
      String tokenJson = getJwtPayload(oidr.getId_token());
      System.out.println("tokenJson: " + tokenJson);
      IdToken idt = gson.fromJson(tokenJson, IdToken.class);
      System.out.println("Name is: " + idt.getName());
      System.out.println("Email is: " + idt.getEmail());
      System.out.println("Returning something");
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

  private static String getJwtPayload(String id_token) {
    String jwtPayload = id_token.split("\\.")[1];
    return new String(Base64.getDecoder().decode(jwtPayload));
  }

  private static URI getOpenIdConnectUrl(String code) {
    return URI.create(
        "https://slack.com/api/openid.connect.token/?client_id=2464212157.2680821785088"
            + "&client_secret=96e6d39590a94ecf1eb3f7f300d622c5"
            + "&code=" + code
            + "&redirect_uri=https://3c04-2600-1700-2320-ab30-acc3-c067-4009-5d2b.ngrok.io/openid/callback/"
    );
  }

  private static void landingPageRequest(HttpExchange exchange) throws IOException {
    System.out.println("Root request received");
    printExchangeRequestInfo(exchange);
    String body = slackButtonHtml();
    byte[] bodyBytes = body.getBytes(StandardCharsets.US_ASCII);
    exchange.sendResponseHeaders(200, bodyBytes.length);
    OutputStream os = exchange.getResponseBody();
    os.write(bodyBytes);
  }

  private static void printExchangeRequestInfo(HttpExchange exchange) throws IOException {
    System.out.println("Request received");
    System.out.println(exchange.getRequestMethod() + exchange.getRequestURI());
    System.out.println("Headers");
    exchange.getRequestHeaders().forEach((k, v) -> System.out.println(k + " " + v));
    System.out.println("Body");
    byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
    System.out.println(new String(bodyBytes, StandardCharsets.US_ASCII));
  }

  private static String slackButtonHtml() {
    return "<a href=\"https://slack.com/openid/connect/authorize?scope=openid%20email%20profile&amp;response_type=code&amp;redirect_uri=https%3A%2F%2F3c04-2600-1700-2320-ab30-acc3-c067-4009-5d2b.ngrok.io%2Fopenid%2Fcallback%2F&amp;client_id=2464212157.2680821785088\" style=\"align-items:center;color:#000;background-color:#fff;border:1px solid #ddd;border-radius:4px;display:inline-flex;font-family:Lato, sans-serif;font-size:16px;font-weight:600;height:48px;justify-content:center;text-decoration:none;width:256px\"><svg xmlns=\"http://www.w3.org/2000/svg\" style=\"height:20px;width:20px;margin-right:12px\" viewBox=\"0 0 122.8 122.8\"><path d=\"M25.8 77.6c0 7.1-5.8 12.9-12.9 12.9S0 84.7 0 77.6s5.8-12.9 12.9-12.9h12.9v12.9zm6.5 0c0-7.1 5.8-12.9 12.9-12.9s12.9 5.8 12.9 12.9v32.3c0 7.1-5.8 12.9-12.9 12.9s-12.9-5.8-12.9-12.9V77.6z\" fill=\"#e01e5a\"></path><path d=\"M45.2 25.8c-7.1 0-12.9-5.8-12.9-12.9S38.1 0 45.2 0s12.9 5.8 12.9 12.9v12.9H45.2zm0 6.5c7.1 0 12.9 5.8 12.9 12.9s-5.8 12.9-12.9 12.9H12.9C5.8 58.1 0 52.3 0 45.2s5.8-12.9 12.9-12.9h32.3z\" fill=\"#36c5f0\"></path><path d=\"M97 45.2c0-7.1 5.8-12.9 12.9-12.9s12.9 5.8 12.9 12.9-5.8 12.9-12.9 12.9H97V45.2zm-6.5 0c0 7.1-5.8 12.9-12.9 12.9s-12.9-5.8-12.9-12.9V12.9C64.7 5.8 70.5 0 77.6 0s12.9 5.8 12.9 12.9v32.3z\" fill=\"#2eb67d\"></path><path d=\"M77.6 97c7.1 0 12.9 5.8 12.9 12.9s-5.8 12.9-12.9 12.9-12.9-5.8-12.9-12.9V97h12.9zm0-6.5c-7.1 0-12.9-5.8-12.9-12.9s5.8-12.9 12.9-12.9h32.3c7.1 0 12.9 5.8 12.9 12.9s-5.8 12.9-12.9 12.9H77.6z\" fill=\"#ecb22e\"></path></svg>Sign in with Slack</a>";
  }
}
