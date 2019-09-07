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
import ru.khayz.server.servlets.AddToAccountServlet;
import ru.khayz.server.servlets.CheckAmountServlet;
import ru.khayz.server.servlets.SendToClientServlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static DbService dbService;

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

        dbService = new DbServiceImpl();

        initiateDatabase();

        Server server = new Server(main.port);
        initServlets(server);

        server.start();
        server.join();
    }

    public static String getPropertyValue(String key) {
        return serverProperties.getProperty(key);
    }

    public static DbService getDbService() {
        return dbService;
    }
    /**
     * Made only for testing purposes. Not applicable for use on Production
     */
    private static void initiateDatabase() {
    }

    /**
     *
     * @param server
     */
    private static void initServlets(Server server) {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new AddToAccountServlet()), "/addToAccount");
        context.addServlet(new ServletHolder(new CheckAmountServlet()),  "/checkAmount");
        context.addServlet(new ServletHolder(new SendToClientServlet()), "/sendToClient");
        server.setHandler(context);
    }
}
