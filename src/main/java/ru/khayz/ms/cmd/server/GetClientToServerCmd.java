package ru.khayz.ms.cmd.server;

import ru.khayz.db.model.Client;
import ru.khayz.ms.Address;
import ru.khayz.ms.cmd.ToServerCmd;

import javax.servlet.AsyncContext;
import java.util.Objects;

public class GetClientToServerCmd extends ToServerCmd {
    private Client client;

    public GetClientToServerCmd(Address from, Address to, AsyncContext context, ResultCode result, Client client) {
        super(from, to, context, result);
        this.client = client;
    }

    public GetClientToServerCmd(Address from, Address to, AsyncContext context, ResultCode result, String message) {
        super(from, to, context, result, message);
    }

    public Client getClient() {
        return client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GetClientToServerCmd that = (GetClientToServerCmd) o;
        return Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), client);
    }
}
