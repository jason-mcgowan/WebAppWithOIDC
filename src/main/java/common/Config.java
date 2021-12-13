package common;

/**
 * @author Jason McGowan
 */
public class Config {

  private int serverPort;
  private String webHost;
  private String templatePath;
  private String slackCallbackUrl;
  private String client_id;
  private String client_secret;
  private String db_address;
  private String db_username;
  private String db_password;
  private String mainPath;
  private String logoutPath;
  private String eventsPath;
  private String createEventPath;
  private String transactionsPath;
  private String accountPath;
  private String loginPath;
  private String transferPath;

  public int getServerPort() {
    return serverPort;
  }

  public String getWebHost() {
    return webHost;
  }

  public String getTemplatePath() {
    return templatePath;
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

  public String getDb_address() {
    return db_address;
  }

  public String getDb_username() {
    return db_username;
  }

  public String getDb_password() {
    return db_password;
  }

  public String getMainPath() {
    return mainPath;
  }

  public String getLogoutPath() {
    return logoutPath;
  }

  public String getEventsPath() {
    return eventsPath;
  }

  public String getCreateEventPath() {
    return createEventPath;
  }

  public String getTransactionsPath() {
    return transactionsPath;
  }

  public String getAccountPath() {
    return accountPath;
  }

  public String getLoginPath() {
    return loginPath;
  }

  public String getTransferPath() {
    return transferPath;
  }
}
