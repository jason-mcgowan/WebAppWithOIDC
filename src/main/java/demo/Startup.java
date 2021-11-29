package demo;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Startup {


  public static void main(String[] args) {
    try {
      Config config = loadConfig(args);
      Server server = new Server(config);
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static Config loadConfig(String[] args) throws IOException {
    if (args.length < 1) {
      throw new IllegalArgumentException(
          "Must include single command line argument for config file location");
    }
    String file = Files.readString(Paths.get(args[0]));
    Gson gson = new Gson();
    return gson.fromJson(file, Config.class);
  }
}
