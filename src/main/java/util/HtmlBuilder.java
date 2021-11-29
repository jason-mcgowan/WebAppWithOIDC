package util;

/**
 * Utility class for providing common HTML documents.
 *
 * @author Jason McGowan
 */
public final class HtmlBuilder {

  private static final String START_TO_NAMESPACE = "<!DOCTYPE HTML>\n<html xmlns=\"";
  private static final String NAMESPACE_TO_TITLE = "\">\n<head>\n<title>";
  private static final String TITLE_TO_BODY = "</title>\n</head>\n<body>\n";
  private static final String BODY_TO_END = "\n</body>\n</html>";

  private HtmlBuilder() {
  }

  /**
   * Ensure the bodyXml is fully formed XML.
   */
  public static String simplePage(String namespace, String title, String bodyXml) {
    return START_TO_NAMESPACE + namespace
        + NAMESPACE_TO_TITLE + title
        + TITLE_TO_BODY + bodyXml
        + BODY_TO_END;
  }

  public static String inputTextForPost(String action, String inputLabel, String inputId,
      String buttonLabel) {
    return "<form action=\"" + action + "\" method=\"POST\">\n"
        + "<label for=\"" + inputId + "\">" + inputLabel + "</label><br/>\n"
        + "<input type=\"text\" id=\"" + inputId + "\" name=\"" + inputId + "\"/><br/>\n"
        + "<input type=\"submit\" value=\"" + buttonLabel + "\"/>\n"
        + "</form>\n";
  }

}
