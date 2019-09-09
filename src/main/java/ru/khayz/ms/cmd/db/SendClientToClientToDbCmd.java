package ru.khayz.ms.cmd.db;

import ru.khayz.db.DbException;
import ru.khayz.db.DbService;
import ru.khayz.ms.Address;
import ru.khayz.ms.Cmd;
import ru.khayz.ms.cmd.ToDbCmd;
import ru.khayz.ms.cmd.server.SendClientToClientToServerCmd;

import javax.servlet.AsyncContext;

import java.util.Objects;

import static ru.khayz.ms.cmd.ToServerCmd.ResultCode;

public class SendClientToClientToDbCmd extends ToDbCmd {
    private long clientFromId;
    private long clientToId;
    private long amount;
    public SendClientToClientToDbCmd(Address from, Address to, AsyncContext context, long clientFromId, long clientToId, long amount) {
        super(from, to, context);
        this.clientFromId = clientFromId;
        this.clientToId = clientToId;
        this.amount = amount;
    }

    @Override
    public void exec(DbService db) {
        Cmd response;
        try {
            db.sendMoneyByClients(clientFromId, clientToId, amount);
            response = new SendClientToClientToServerCmd(getTo(), getFrom(), getContext(), ResultCode.SUCCESS, "Success");
        } catch (DbException e) {
            response = new SendClientToClientToServerCmd(getTo(), getFrom(), getContext(), ResultCode.ERROR, e.getMessage());
        }
        db.getCommandSystem().sendCmd(response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SendClientToClientToDbCmd that = (SendClientToClientToDbCmd) o;
        return clientFromId == that.clientFromId &&
                clientToId == that.clientToId &&
                amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientFromId, clientToId, amount);
    }

    public long getClientFromId() {
        return clientFromId;
    }

    public long getClientToId() {
        return clientToId;
    }

    public long getAmount() {
        return amount;
    }
}
