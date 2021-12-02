package demo;

public class Config {

  private String webHost;
  private String slackCallbackUrl;
  private String client_id;
  private String client_secret;
  private String db_address;
  private String db_username;
  private String db_password;

  public String getWebHost() {
    return webHost;
  }

  public String getSlackCallbackUrl() {
    return slackCallbackUrl;
  }

  public String getClient_id() {
    return client_id;
  }

  public String getClient_secret() {
    return client_secret;
  }
}
