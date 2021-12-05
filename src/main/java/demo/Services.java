package demo;

import freemarker.template.Configuration;

/**
 * Singleton to hold various global services.
 *
 * @author Jason McGowan
 */
public final class Services {

  private Config config;
  private DbPool dbPool;
  private Configuration freemarkerCfg;

  public Configuration getFreemarkerCfg() {
    return freemarkerCfg;
  }

  public void setFreemarkerCfg(Configuration freemarkerCfg) {
    this.freemarkerCfg = freemarkerCfg;
  }

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
