package Db;

import common.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Contains a pool of JDBC connection objects for use by the DbStatements utility class.
 *
 * @author Jason McGowan
 */
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

  public Connection getConnection() throws SQLException {
    if (pool.isEmpty()) {
      return DriverManager.getConnection(url, user, password);
    } else {
      return pool.remove();
    }
  }

  public void returnConnection(Connection conn) {
    pool.add(conn);
  }
}
