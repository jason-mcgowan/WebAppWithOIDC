package demo;

/**
 * Singleton to hold various global services.
 *
 * @author Jason McGowan
 */
public final class Services {

  private Config config;
  private DbPool dbPool;

  public DbPool getDbPool() {
    return dbPool;
  }

  public void setDbPool(DbPool dbPool) {
    this.dbPool = dbPool;
  }

  public Config getConfig() {
    return config;
  }

  public void setConfig(Config config) {
    this.config = config;
  }

  private Services() {
  }

  public static synchronized Services getInstance() {
    return InstanceHolder.instance;
  }

  private static final class InstanceHolder {
    private static final Services instance = new Services();
  }
}
