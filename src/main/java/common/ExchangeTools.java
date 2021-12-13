package common;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for dealing with common HttpExchange needs in project4
 *
 * @author Jason McGowan
 */
public final class ExchangeTools {

  private ExchangeTools() {
  }

  public static void printExchangeRequestInfo(HttpExchange exchange) throws IOException {
    System.out.println("Request received");
    System.out.println(exchange.getRequestMethod() + exchange.getRequestURI());
    System.out.println("Headers");
    exchange.getRequestHeaders().forEach((k, v) -> System.out.println(k + " " + v));
    System.out.println("Body");
    byte[] bodyBytes = exchange.getRequestBody().readAllBytes();
    System.out.println(new String(bodyBytes, StandardCharsets.US_ASCII));
  }

  public static void templateResponse(HttpExchange exchange, Map<String, Object> model,
      String templatePath)
      throws IOException, TemplateException {
    Template temp = Services.getInstance().getFreemarkerCfg().getTemplate(templatePath);
    StringWriter sw = new StringWriter();
    temp.process(model, sw);
    byte[] msgBytes = sw.toString().getBytes(StandardCharsets.US_ASCII);
    exchange.sendResponseHeaders(200, msgBytes.length);
    OutputStream os = exchange.getResponseBody();
    os.write(msgBytes);
    os.close();
  }

  public static void homeMessageResponse(HttpExchange exchange, String message)
      throws TemplateException, IOException {
    Map<String, Object> model = new HashMap<>();
    String templatePath = "/message.html";
    model.put("mainUrl", "/");
    model.put("message", message);
    ExchangeTools.templateResponse(exchange, model, templatePath);
  }
}
