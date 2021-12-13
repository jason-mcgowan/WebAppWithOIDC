package Network;

import java.time.Instant;
import java.util.UUID;

/**
 * Contains information for the ongoing session. Linked by a browser cookie.
 *
 * @author Jason McGowan
 */
public class SessionData {

  private final String id;
  private boolean isLoggedIn;
  private int localUserId;
  private String displayName;
  private Instant timeLastActive;
  private boolean logoutRequested;

  public SessionData() {
    id = UUID.randomUUID().toString().replaceAll("-", "");
    isLoggedIn = false;
    timeLastActive = Instant.now();
    logoutRequested = false;
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

  public Instant getTimeLastActive() {
    return timeLastActive;
  }

  public void setTimeLastActive(Instant timeLastActive) {
    this.timeLastActive = timeLastActive;
  }

  public boolean isLogoutRequested() {
    return logoutRequested;
  }

  public void setLogoutRequested(boolean logoutRequested) {
    this.logoutRequested = logoutRequested;
  }
}
