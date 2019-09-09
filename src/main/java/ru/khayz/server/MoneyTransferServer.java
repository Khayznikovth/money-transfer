package ru.khayz.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.khayz.db.DbService;
import ru.khayz.ms.CmdSystem;
import ru.khayz.server.servlets.*;

import java.util.ArrayList;
import java.util.List;

public class MoneyTransferServer extends Server {
    private final DbService db;
    private final CmdSystem cs;
    private List<CommonServlet> servlets;

    public MoneyTransferServer(Integer port, CmdSystem cs, DbService db) {
        super(port);
        this.cs = cs;
        this.db = db;
        servlets = new ArrayList<>();
        initServlets();
    }

    private void initServlets() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        cs.addQueue(db.getAddress());
        servlets.add(new AddAccountServlet(cs, db.getAddress()));
        servlets.add(new AddClientServlet(cs, db.getAddress()));
        servlets.add(new AddToAccountServlet(cs, db.getAddress()));
        servlets.add(new CheckAmountServlet(cs, db.getAddress()));
        servlets.add(new GetAccountServlet(cs, db.getAddress()));
        servlets.add(new GetClientAccountsServlet(cs, db.getAddress()));
        servlets.add(new GetClientServlet(cs, db.getAddress()));
        servlets.add(new SendAccountToAccountServlet(cs, db.getAddress()));
        servlets.add(new SendClientToClientServlet(cs, db.getAddress()));


        for (CommonServlet servlet: servlets) {
            context.addServlet(new ServletHolder(servlet), servlet.getUrl());
            cs.addQueue(servlet.getAddress());
            new Thread(servlet).start();
        }

        this.setHandler(context);
    }

    public void startThreads() {
        new Thread(db).start();
        for (CommonServlet servlet: servlets) {
            new Thread(servlet).start();
        }
    }
}
