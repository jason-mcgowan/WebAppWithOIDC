package demo;

import java.util.UUID;

public class SessionData {

  private boolean isLoggedIn;
  private final String id;

  public SessionData() {
    id = UUID.randomUUID().toString().replaceAll("-", "");
    isLoggedIn = false;
  }

  public boolean isLoggedIn() {
    return isLoggedIn;
  }

  public String getId() {
    return id;
  }

  public void setLoggedIn(boolean loggedIn) {
    isLoggedIn = loggedIn;
  }
}
