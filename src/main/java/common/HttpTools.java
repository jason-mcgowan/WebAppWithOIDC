package common;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for dealing with common HTTP needs
 *
 * @author Jason McGowan
 */
public final class HttpTools {

  private static final String IMPROPER_KVP_MESSAGE = "Key-value pairs not properly constructed";

  private HttpTools() {
  }

  public static Map<String, String> parseUrlEncodedPostPayload(String payload)
      throws IllegalArgumentException {
    Map<String, String> result = new HashMap<>();
    String[] pairs = payload.split("&");
    Charset cs = StandardCharsets.UTF_8;
    for (String s : pairs) {
      String[] kvp = s.split("=");
      if (kvp.length == 0) {
        throw new IllegalArgumentException(IMPROPER_KVP_MESSAGE);
      }
      String key = URLDecoder.decode(kvp[0], cs);
      String value;
      if (kvp.length == 1) {
        value = "";
      } else if (kvp.length == 2) {
        value = URLDecoder.decode(kvp[1], cs);
      } else {
        throw new IllegalArgumentException(IMPROPER_KVP_MESSAGE);
      }
      result.put(key, value);
    }
    return result;
  }

}
