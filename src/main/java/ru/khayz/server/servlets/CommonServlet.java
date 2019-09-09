package ru.khayz.server.servlets;

import ru.khayz.ms.Address;
import ru.khayz.ms.CmdSystem;
import ru.khayz.ms.Subscriber;
import ru.khayz.ms.cmd.ToServerCmd;

import javax.servlet.http.HttpServlet;


public abstract class CommonServlet extends HttpServlet implements Subscriber, Runnable {
    protected final CmdSystem cs;
    protected final Address address;
    protected final Address dbAddress;

    public CommonServlet(CmdSystem cs, Address dbAddress) {
        this.cs = cs;
        this.address = new Address();
        this.dbAddress = dbAddress;
    }

    @Override
    public final void run() {
        while (true) {
            cs.execForSubscriber(this);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public CmdSystem getCommandSystem() {
        return cs;
    }

    public abstract void processResponse(ToServerCmd cmd);

    public abstract String getUrl();
}
