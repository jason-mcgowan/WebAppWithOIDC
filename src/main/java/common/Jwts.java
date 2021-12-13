package common;

import java.util.Base64;

/**
 * Utility class for handling JWTs (Java Web Tokens)
 * @author Jason McGowan
 */
public final class Jwts {

  private Jwts() {
  }

  /**
   * Argument must be in a valid JWT format with the '.' character separating elements
   */
  public static String getPayload(String jwt) {
    String jwtPayload = jwt.split("\\.")[1];
    return new String(Base64.getDecoder().decode(jwtPayload));
  }

}
