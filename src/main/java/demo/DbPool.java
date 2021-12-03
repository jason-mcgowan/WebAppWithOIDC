package demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    this(config.getDb_address(), config.getDb_username(), config.getDb_password());
  }

  public ResultSet executeQuery(String sql) throws SQLException {
    Connection conn = getConnection();
    PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_UPDATABLE);
    ResultSet rs =ps.executeQuery();
    returnConnection(conn);
    return rs;
  }

  public ResultSet executeUpdate(String sql) throws SQLException {
    Connection conn = getConnection();
    PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    ps.executeUpdate();
    returnConnection(conn);
    return ps.getGeneratedKeys();
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
