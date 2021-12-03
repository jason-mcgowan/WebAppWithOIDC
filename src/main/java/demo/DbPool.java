package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DbPool {

  private final String url;
  private final String user;
  private final String password;
  private final Queue<Connection> pool = new ConcurrentLinkedQueue<>();

  public DbPool(String url, String user, String password) {
    this.url = url;
    this.user = user;
    this.password = password;
  }

  public DbPool(Config config) {
    this.url = config.getDb_address();
    this.user = config.getDb_username();
    this.password = config.getDb_password();
  }

  public PreparedStatement executeQuery(String sql) throws SQLException {
    Connection conn = getConnection();
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.executeQuery();
    returnConnection(conn);
    return ps;
  }

  private Connection getConnection() throws SQLException {
    if (pool.isEmpty()) {
      return DriverManager.getConnection(url, user, password);
    } else {
      return pool.remove();
    }
  }

  private void returnConnection(Connection conn) {
    pool.add(conn);
  }
}
