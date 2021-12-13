package demo;

import Db.DbPool;
import Network.Server;
import com.google.gson.Gson;
import common.Config;
import common.Services;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Example server
 * @author Jason McGowan
 */
public class Startup {

  public static void main(String[] args) {
    try {
      Config config = readConfigFile(args);
      initServices(config);
      Server server = new Server();
      server.start(config);
      System.out.println("Server started, press enter to shutdown");
      int b = System.in.read();
      server.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void initServices(Config config) throws IOException {
    Services.getInstance().setConfig(config);
    Services.getInstance().setDbPool(new DbPool(config));
    Services.getInstance().setFreemarkerCfg(initFreemarkerConfig(config.getTemplatePath()));
  }

  public static Config readConfigFile(String[] args) throws IOException {
    if (args.length < 1) {
      throw new IllegalArgumentException(
          "Must include single command line argument for config file location");
    }
    String file = Files.readString(Paths.get(args[0]));
    Gson gson = new Gson();
    return gson.fromJson(file, Config.class);
  }

  private static Configuration initFreemarkerConfig(String templateDir) throws IOException {

    // Recommended settings from the manual
    // https://freemarker.apache.org/docs/pgui_quickstart_createconfiguration.html

    Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
    cfg.setDirectoryForTemplateLoading(new File(templateDir));
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setFallbackOnNullLoopVariable(false);
    return cfg;
  }
}
