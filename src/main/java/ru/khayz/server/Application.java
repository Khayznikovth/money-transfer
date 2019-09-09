package ru.khayz.server;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.khayz.db.DbService;
import ru.khayz.db.DbServiceImpl;
import ru.khayz.ms.CmdSystem;
import ru.khayz.server.servlets.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Application {
    private final Logger logger;
    @Parameter(names = {"-port", "-p"}, validateValueWith = PortValidator.class,
            description = "Port on that server will be init")
    private Integer port = 8080;

    public static class PortValidator implements IValueValidator<Integer> {
        @Override
        public void validate(String name, Integer value) throws ParameterException {
            int min = 0;
            int max = 65535;
            if (value < min || value > max) {
                throw new ParameterException(String.format("Port should be in range from %d to %d", min, max));
            }
        }
    }

    private static Properties serverProperties;

    Application() throws ParameterException{
        logger = LoggerFactory.getLogger(Application.class);
    }

    public static void main(String[] args) throws Exception {
        Application main = new Application();
        JCommander.newBuilder().addObject(main).build().parse(args);

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            serverProperties = properties;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        CmdSystem cs = new CmdSystem();
        DbService db = new DbServiceImpl(cs);

        MoneyTransferServer server = new MoneyTransferServer(main.port, cs, db);
        server.startThreads();

        server.start();
        server.join();
    }

    public static String getPropertyValue(String key) {
        return serverProperties.getProperty(key);
    }

    private static void initServlets(Server server) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        CmdSystem cs = new CmdSystem();
        DbService db = new DbServiceImpl(cs);
        cs.addQueue(db.getAddress());

        List<CommonServlet> servlets = new ArrayList<>();
        servlets.add(new AddAccountServlet(cs, db.getAddress()));
        servlets.add(new AddClientServlet(cs, db.getAddress()));
        servlets.add(new AddToAccountServlet(cs, db.getAddress()));
        servlets.add(new CheckAmountServlet(cs, db.getAddress()));
        servlets.add(new GetAccountServlet(cs, db.getAddress()));
        servlets.add(new GetClientAccountsServlet(cs, db.getAddress()));
        servlets.add(new GetClientServlet(cs, db.getAddress()));
        servlets.add(new SendAccountToAccountServlet(cs, db.getAddress()));
        servlets.add(new SendClientToClientServlet(cs, db.getAddress()));

        new Thread(db).start();
        for (CommonServlet servlet: servlets) {
            context.addServlet(new ServletHolder(servlet), servlet.getUrl());
            cs.addQueue(servlet.getAddress());
            new Thread(servlet).start();
        }

        server.setHandler(context);
    }
}
