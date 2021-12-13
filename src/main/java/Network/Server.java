package Network;

import Network.Handlers.AccountHandler;
import Network.Handlers.CreateEventHandler;
import Network.Handlers.LoginCheckFilter;
import Network.Handlers.LoginHandler;
import Network.Handlers.LogoutHandler;
import Network.Handlers.RootHandler;
import Network.Handlers.SlackOidcHandler;
import Network.Handlers.TransactionsHandler;
import Network.Handlers.TransferHandler;
import Network.Handlers.ViewEventHandler;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import common.Config;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {

  private HttpServer server;

  public void start(Config config) throws IOException {
    ConcurrentMap<String, SessionData> sessions = new ConcurrentHashMap<>();
    LoginCheckFilter loginCheckFilter = new LoginCheckFilter(
        config.getWebHost() + config.getLoginPath());
    SessionFilter sessionFilter = new SessionFilter(sessions);
    List<Filter> standardFilters = List.of(sessionFilter, loginCheckFilter);

    server = HttpServer.create(new InetSocketAddress(config.getServerPort()), -1);
    server.createContext(config.getLoginPath(), new LoginHandler(config)).getFilters()
        .add(sessionFilter);
    server.createContext(config.getSlackCallbackUrl(), new SlackOidcHandler()).getFilters()
        .add(sessionFilter);

    setupHandlerFilters(server, "/", new RootHandler(config), standardFilters);
    setupHandlerFilters(server, config.getAccountPath(), new AccountHandler(config),
        standardFilters);
    setupHandlerFilters(server, config.getCreateEventPath(), new CreateEventHandler(config),
        standardFilters);
    setupHandlerFilters(server, config.getLogoutPath(), new LogoutHandler(config),
        standardFilters);
    setupHandlerFilters(server, config.getEventsPath(), new ViewEventHandler(config),
        standardFilters);
    setupHandlerFilters(server, config.getTransactionsPath(), new TransactionsHandler(config),
        standardFilters);
    setupHandlerFilters(server, config.getTransferPath(), new TransferHandler(config),
        standardFilters);

    server.start();
  }

  public void stop() {
    server.stop(0);
  }

  private void setupHandlerFilters(HttpServer server, String path, HttpHandler handler,
      List<Filter> filters) {
    HttpContext context = server.createContext(path, handler);
    for (Filter filter : filters) {
      context.getFilters().add(filter);
    }
  }
}
