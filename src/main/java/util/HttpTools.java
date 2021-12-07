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
      if (kvp.length != 2) {
        throw new IllegalArgumentException("Key-value pairs not properly constructed");
      }
      result.put(URLDecoder.decode(kvp[0], cs), URLDecoder.decode(kvp[1], cs));
    }
    return result;
  }

}
