package demo;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {

  public void start(Config config) throws IOException {
    ConcurrentMap<String, SessionData> sessions = new ConcurrentHashMap<>();
    LoginCheckFilter loginCheckFilter = new LoginCheckFilter(
        config.getWebHost() + config.getLoginPath());
    SessionFilter sessionFilter = new SessionFilter(sessions);

    HttpServer server = HttpServer.create(new InetSocketAddress(config.getServerPort()), -1);
    server.createContext(config.getLoginPath(), new LoginHandler(config)).getFilters()
        .add(sessionFilter);
    server.createContext(config.getSlackCallbackUrl(), new SlackOidcHandler()).getFilters()
        .add(sessionFilter);
    HttpContext main = server.createContext("/", new RootHandler(config));
    main.getFilters().add(sessionFilter);
    main.getFilters().add(loginCheckFilter);

    server.start();
  }
}
