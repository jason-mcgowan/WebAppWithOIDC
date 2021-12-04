package demo;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Startup {

  public static void main(String[] args) {
    try {
      Config config = readConfigFile(args);
      initServices(config);
      Server server = new Server();
      server.start(config.getServerPort());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void initServices(Config config) {
    Services.getInstance().setConfig(config);
    Services.getInstance().setDbPool(new DbPool(config));
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
}
