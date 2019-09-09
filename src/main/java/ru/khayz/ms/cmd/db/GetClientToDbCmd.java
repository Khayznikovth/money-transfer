package ru.khayz.ms.cmd.db;

import ru.khayz.db.DbException;
import ru.khayz.db.DbService;
import ru.khayz.db.model.Client;
import ru.khayz.ms.Address;
import ru.khayz.ms.Cmd;
import ru.khayz.ms.cmd.ToDbCmd;
import ru.khayz.ms.cmd.server.GetClientToServerCmd;

import javax.servlet.AsyncContext;

import java.util.Objects;

import static ru.khayz.ms.cmd.ToServerCmd.ResultCode;

public class GetClientToDbCmd extends ToDbCmd {
    private long clientId;

    public GetClientToDbCmd(Address from, Address to, AsyncContext context, long clientId) {
        super(from, to, context);
        this.clientId = clientId;
    }

    @Override
    public void exec(DbService db) {
        Cmd response;
        try {
            Client client = db.getClient(clientId);
            response = new GetClientToServerCmd(getTo(), getFrom(), getContext(), ResultCode.SUCCESS, client);
        } catch (DbException e) {
            response = new GetClientToServerCmd(getTo(), getFrom(), getContext(), ResultCode.ERROR, e.getMessage());
        }
        db.getCommandSystem().sendCmd(response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetClientToDbCmd that = (GetClientToDbCmd) o;
        return clientId == that.clientId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }

    public long getClientId() {
        return clientId;
    }
}
