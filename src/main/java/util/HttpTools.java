package util;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class HttpTools {

  private HttpTools() {

  }

  public static Map<String, String> parseUrlEncodedPostPayload(String payload) throws IllegalArgumentException {
    Map<String, String> result = new HashMap<>();
    String[] kvps = payload.split("&");
    Charset cs = StandardCharsets.UTF_8;
    for (String s : kvps) {
      String[] kvp = s.split("=");
      if (kvp.length == 0) {
        throw new IllegalArgumentException("Key-value pairs not properly constructed");
      }
      String key = URLDecoder.decode(kvp[0], cs);
      String value;
      if (kvp.length == 1) {
        value = "";
      } else if (kvp.length == 2) {
        value = URLDecoder.decode(kvp[1], cs);
      } else {
        throw new IllegalArgumentException("Key-value pairs not properly constructed");
      }
      result.put(key, value);
    }
    return result;
  }

}
