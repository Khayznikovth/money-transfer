package ru.khayz.ms.cmd.db;

import ru.khayz.db.DbException;
import ru.khayz.db.DbService;
import ru.khayz.db.model.Account;
import ru.khayz.ms.Address;
import ru.khayz.ms.Cmd;
import ru.khayz.ms.cmd.ToDbCmd;
import ru.khayz.ms.cmd.server.GetClientAccountsToServerCmd;

import javax.servlet.AsyncContext;
import java.util.List;
import java.util.Objects;

import static ru.khayz.ms.cmd.ToServerCmd.ResultCode;

public class GetClientAccountsToDbCmd extends ToDbCmd {
    private long clientId;

    public GetClientAccountsToDbCmd(Address from, Address to, AsyncContext context, long clientId) {
        super(from, to, context);
        this.clientId = clientId;
    }

    @Override
    public void exec(DbService db) {
        Cmd response;
        try {
            List<Account> accounts = db.getClientAccounts(clientId);
            response = new GetClientAccountsToServerCmd(getTo(), getFrom(), getContext(), ResultCode.SUCCESS, accounts);
        } catch (DbException e) {
            response = new GetClientAccountsToServerCmd(getTo(), getFrom(), getContext(), ResultCode.ERROR, e.getMessage());
        }
        db.getCommandSystem().sendCmd(response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetClientAccountsToDbCmd that = (GetClientAccountsToDbCmd) o;
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
