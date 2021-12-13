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
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import common.Config;
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

    context = server.createContext(config.getEventsPath(), new ViewEventHandler(config));
    context.getFilters().add(sessionFilter);
    context.getFilters().add(loginCheckFilter);

    context = server.createContext(config.getTransactionsPath(), new TransactionsHandler(config));
    context.getFilters().add(sessionFilter);
    context.getFilters().add(loginCheckFilter);

    context = server.createContext(config.getTransferPath(), new TransferHandler(config));
    context.getFilters().add(sessionFilter);
    context.getFilters().add(loginCheckFilter);

    server.start();
  }
}
