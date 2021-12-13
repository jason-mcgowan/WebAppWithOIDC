package Db;

import common.Services;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Utility class for performing safe queries and updates for project4
 *
 * @author Jason McGowan
 */
public final class DbStatements {

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
  private static final String EVENTS_QUERY =
      "SELECT * FROM event "
          + "WHERE CONCAT(name, description) LIKE ?";
  private static final String EVENT_ID_QUERY =
      "SELECT * FROM event WHERE id=?";
  private static final String LOWER_EVENT_TICKETS =
      "UPDATE event SET quantity_remaining=quantity_remaining-? WHERE id=?";
  private static final String INSERT_PURCHASE =
      "INSERT INTO purchase VALUES (?, ?, ?, ?)"
          + "ON DUPLICATE KEY UPDATE quantity=quantity+?";
  private static final String USER_PURCHASES_QUERY =
      "SELECT event.id, event.name, event.start_date, event.end_date, purchase.quantity "
          + "FROM purchase INNER JOIN event ON purchase.event_id=event.id "
          + "WHERE purchase.local_user_id=?";
  private static final String LOWER_USER_EVENT_TICKET =
      "UPDATE purchase SET quantity=quantity-? WHERE user_event_ids=?";

  private DbStatements() {
  }

  /**
   * Returns a map of local user ids to their display names with the provided slack unique
   * identifier (sub)
   */
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

  /**
   * Inserts a new slack user
   */
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

  /**
   * Updates the user's display name
   */
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

  /**
   * Inserts a new event
   */
  public static void createEvent(EventData event, int creatorLocalId) throws SQLException {
    Connection conn = pool.getConnection();
    try (PreparedStatement ps = conn.prepareStatement(INSERT_EVENT_STATEMENT)) {
      ps.setString(1, event.getName());
      ps.setString(2, event.getDescription());
      ps.setDate(3, event.getStartDate());
      ps.setDate(4, event.getEndDate());
      ps.setInt(5, creatorLocalId);
      ps.setInt(6, event.getQuantity());
      ps.setBigDecimal(7, event.getPrice());
      ps.executeUpdate();
    } finally {
      pool.returnConnection(conn);
    }
  }

  /**
   * Gets all events whose name or description contains the provided query
   */
  public static Collection<EventData> getEvents(String query) throws SQLException {
    Connection conn = pool.getConnection();
    Collection<EventData> events = new LinkedList<>();
    try (PreparedStatement ps = conn.prepareStatement(EVENTS_QUERY,
        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
      ps.setString(1, "%" + query + "%");
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        events.add(getEventData(rs));
      }
      return events;
    } finally {
      pool.returnConnection(conn);
    }
  }

  private static EventData getEventData(ResultSet rs) throws SQLException {
    EventData event = new EventData();
    event.setId(rs.getInt(1));
    event.setName(rs.getString(2));
    event.setDescription(rs.getString(3));
    event.setStartDate(rs.getDate(4));
    event.setEndDate(rs.getDate(5));
    event.setQuantity(rs.getInt(7));
    event.setPrice(rs.getBigDecimal(8));
    return event;
  }

  /**
   * Returns the event data for the provided event id
   */
  public static EventData getEvent(int event) throws SQLException {
    Connection conn = pool.getConnection();
    try (PreparedStatement ps = conn.prepareStatement(EVENT_ID_QUERY,
        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
      ps.setInt(1, event);
      ResultSet rs = ps.executeQuery();
      rs.next();
      if (rs.isAfterLast()) {
        return null;
      }
      return getEventData(rs);
    } finally {
      pool.returnConnection(conn);
    }
  }

  /**
   * Performs a transaction which locks concurrent table writes while it checks sufficient event
   * tickets then raises/decrements the user/event ticket counts accordingly.
   */
  public static void purchaseTickets(int quantity, int eventId, int userId) throws SQLException {
    Connection conn = pool.getConnection();
    conn.setAutoCommit(false);
    try (
        PreparedStatement lowerTicket = conn.prepareStatement(LOWER_EVENT_TICKETS,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        PreparedStatement insertPurchase = conn.prepareStatement(INSERT_PURCHASE,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
      lowerTicket.setInt(1, quantity);
      lowerTicket.setInt(2, eventId);
      lowerTicket.executeUpdate();
      String userEventIds = userId + "," + eventId;
      insertPurchase.setString(1, userEventIds);
      insertPurchase.setInt(2, userId);
      insertPurchase.setInt(3, eventId);
      insertPurchase.setInt(4, quantity);
      insertPurchase.setInt(5, quantity);
      insertPurchase.executeUpdate();
    } catch (SQLException e) {
      conn.rollback();
      throw e;
    } finally {
      conn.setAutoCommit(true);
      pool.returnConnection(conn);
    }
  }

  /**
   * Returns all purchases belonging to the provided user
   */
  public static Collection<EventData> getUserTransactions(int localUserId) throws SQLException {
    Connection conn = pool.getConnection();
    Collection<EventData> events = new LinkedList<>();
    try (PreparedStatement ps = conn.prepareStatement(USER_PURCHASES_QUERY,
        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
      ps.setInt(1, localUserId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        events.add(purchasesToEvent(rs));
      }
      return events;
    } finally {
      pool.returnConnection(conn);
    }
  }

  private static EventData purchasesToEvent(ResultSet rs) throws SQLException {
    EventData event = new EventData();
    event.setId(rs.getInt(1));
    event.setName(rs.getString(2));
    event.setStartDate(rs.getDate(3));
    event.setEndDate(rs.getDate(4));
    event.setQuantity(rs.getInt(5));
    return event;
  }

  /**
   * Performs a transaction with concurrent table writes disabled while method checks for sufficient
   * from-user ticket count, then increments/decrements the to-user and from-user ticket counts for
   * the event
   */
  public static void transferTickets(int eventId, int fromUser, int toUser, int quantity)
      throws SQLException {
    Connection conn = pool.getConnection();
    conn.setAutoCommit(false);
    try (
        PreparedStatement lowerTicket = conn.prepareStatement(LOWER_USER_EVENT_TICKET,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        PreparedStatement insertPurchase = conn.prepareStatement(INSERT_PURCHASE,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
      lowerTicket.setInt(1, quantity);
      String fromUserEventIds = fromUser + "," + eventId;
      lowerTicket.setString(2, fromUserEventIds);
      int rows = lowerTicket.executeUpdate();
      if (rows == 0) {
        throw new SQLException("No tickets held for event");
      }
      String toUserEventIds = toUser + "," + eventId;
      insertPurchase.setString(1, toUserEventIds);
      insertPurchase.setInt(2, toUser);
      insertPurchase.setInt(3, eventId);
      insertPurchase.setInt(4, quantity);
      insertPurchase.setInt(5, quantity);
      insertPurchase.executeUpdate();
    } catch (SQLException e) {
      conn.rollback();
      throw e;
    } finally {
      conn.setAutoCommit(true);
      pool.returnConnection(conn);
    }
  }

}
