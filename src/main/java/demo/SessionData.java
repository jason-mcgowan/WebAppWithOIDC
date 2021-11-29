package demo;

import java.util.UUID;

public class SessionData {

  private final String id;
  private boolean isLoggedIn;
  private int localUserId;

  public SessionData() {
    id = UUID.randomUUID().toString().replaceAll("-", "");
    isLoggedIn = false;
  }

  public int getLocalUserId() {
    return localUserId;
  }

  public void setLocalUserId(int localUserId) {
    this.localUserId = localUserId;
  }

  public boolean isLoggedIn() {
    return isLoggedIn;
  }

  public void setLoggedIn(boolean loggedIn) {
    isLoggedIn = loggedIn;
  }

  public String getId() {
    return id;
  }
}
