package Network;

/**
 * @author Jason McGowan
 */
public class OpenIdResponse {

  private boolean ok;
  private String access_token;
  private String token_type;
  private String id_token;

  public boolean getOk() {
    return ok;
  }

  public String getAccess_token() {
    return access_token;
  }

  public String getToken_type() {
    return token_type;
  }

  public String getId_token() {
    return id_token;
  }
}
