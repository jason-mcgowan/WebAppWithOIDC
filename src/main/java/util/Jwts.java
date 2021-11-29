package util;

import java.util.Base64;

public final class Jwts {

  private Jwts() {
  }

  public static String getPayload(String jwt) {
    String jwtPayload = jwt.split("\\.")[1];
    return new String(Base64.getDecoder().decode(jwtPayload));
  }

}
