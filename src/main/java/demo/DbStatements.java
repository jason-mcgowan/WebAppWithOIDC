package demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DbStatements {

  private static final DbPool pool = Services.getInstance().getDbPool();
  private static final String SLACK_SUB_QUERY =
      "SELECT local_user.id, local_user.display_name FROM slack_user "
          + "INNER JOIN local_user ON slack_user.local_user_id=local_user.id "
          + "WHERE slack_user.open_id_sub=?";
  private static final String INSERT_LOCAL_USER_STATEMENT =
      "INSERT INTO local_user VALUES (0, ?)";
  private static final String INSERT_SLACK_USER_STATEMENT =
      "INSERT INTO slack_user VALUES (?, ?)";
  private static final String UPDATE_USER_NAME_STATEMENT =
      "UPDATE local_user SET display_name = ? WHERE id = ?";
  private static final String INSERT_EVENT_STATEMENT =
      "INSERT INTO event VALUES (0, ?, ?, ?, ?, ?, ?, ?)";

  public static Map<Integer, String> slackSubQuery(String sub) throws SQLException {
    Map<Integer, String> result = new HashMap<>();
    Connection conn = pool.getConnection();
    try (PreparedStatement ps = conn.prepareStatement(SLACK_SUB_QUERY,
        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
      ps.setString(1, sub);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        result.put(rs.getInt(1), rs.getString(2));
      }
      return result;
    } finally {
      pool.returnConnection(conn);
    }
  }

  /**
   * Inserts a local user with the supplied display name.
   *
   * @return User's auto-generated id
   */
  public static int insertNewLocalUser(String displayName) throws SQLException {
    Connection conn = pool.getConnection();
    try (PreparedStatement ps = conn.prepareStatement(INSERT_LOCAL_USER_STATEMENT,
        Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, displayName);
      ps.executeUpdate();
      ResultSet rs = ps.getGeneratedKeys();
      rs.next();
      return rs.getInt(1);
    } finally {
      pool.returnConnection(conn);
    }
  }

  public static void insertNewSlackUser(String sub, int localId) throws SQLException {
    Connection conn = pool.getConnection();
    try (PreparedStatement ps = conn.prepareStatement(INSERT_SLACK_USER_STATEMENT)) {
      ps.setString(1, sub);
      ps.setInt(2, localId);
      ps.executeUpdate();
    } finally {
      pool.returnConnection(conn);
    }
  }

  public static void updateUserName(String newName, int localId) throws SQLException {
    Connection conn = pool.getConnection();
    try (PreparedStatement ps = conn.prepareStatement(UPDATE_USER_NAME_STATEMENT)) {
      ps.setString(1, newName);
      ps.setInt(2, localId);
      ps.executeUpdate();
    } finally {
      pool.returnConnection(conn);
    }
  }

  public static void createEvent(CreateEventData event) throws SQLException {
    Connection conn = pool.getConnection();
    try (PreparedStatement ps = conn.prepareStatement(INSERT_EVENT_STATEMENT)) {
      ps.setString(1, event.getName());
      ps.setString(2, event.getDescription());
      ps.setDate(3, event.getStartDate());
      ps.setDate(4, event.getEndDate());
      ps.setInt(5, event.getCreatorId());
      ps.setInt(6, event.getQuantity());
      ps.setBigDecimal(7, event.getPrice());
      ps.executeUpdate();
    } finally {
      pool.returnConnection(conn);
    }
  }
}
