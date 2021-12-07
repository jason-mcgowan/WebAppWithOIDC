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
    HttpContext context = server.createContext("/", new RootHandler(config));
    context.getFilters().add(sessionFilter);
    context.getFilters().add(loginCheckFilter);

    context = server.createContext(config.getAccountPath(), new AccountHandler(config));
    context.getFilters().add(sessionFilter);
    context.getFilters().add(loginCheckFilter);

    context = server.createContext(config.getCreateEventPath(), new CreateEventHandler(config));
    context.getFilters().add(sessionFilter);
    context.getFilters().add(loginCheckFilter);

    context = server.createContext(config.getLogoutPath(), new LogoutHandler(config));
    context.getFilters().add(sessionFilter);
    context.getFilters().add(loginCheckFilter);

    server.start();
  }
}
