package demo;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
}
