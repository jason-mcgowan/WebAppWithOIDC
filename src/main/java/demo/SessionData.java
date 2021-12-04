package demo;

import java.util.UUID;

public class SessionData {

  private final String id;
  private boolean isLoggedIn;
  private int localUserId;
  private String displayName;

  public SessionData() {
    id = UUID.randomUUID().toString().replaceAll("-", "");
    isLoggedIn = false;
  }

  @Override
  public String toString() {
    return "SessionData{" +
        "id='" + id + '\'' +
        ", isLoggedIn=" + isLoggedIn +
        ", localUserId=" + localUserId +
        ", displayName='" + displayName + '\'' +
        '}';
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
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
