package ru.khayz.ms.cmd.db;

import ru.khayz.db.DbException;
import ru.khayz.db.DbService;
import ru.khayz.db.model.Account;
import ru.khayz.ms.Address;
import ru.khayz.ms.Cmd;
import ru.khayz.ms.cmd.ToDbCmd;
import ru.khayz.ms.cmd.server.GetAccountToServerCmd;

import javax.servlet.AsyncContext;

import java.util.Objects;

import static ru.khayz.ms.cmd.ToServerCmd.ResultCode;

public class GetAccountToDbCmd extends ToDbCmd {
    private long accId;
    public GetAccountToDbCmd(Address from, Address to, AsyncContext context, long accId) {
        super(from, to, context);
        this.accId = accId;
    }

    @Override
    public void exec(DbService db) {
        Cmd response;
        try {
            Account account = db.getAccount(accId);
            response = new GetAccountToServerCmd(getTo(), getFrom(), getContext(), ResultCode.SUCCESS, account);
        } catch (DbException e) {
            response = new GetAccountToServerCmd(getTo(), getFrom(), getContext(), ResultCode.ERROR, e.getMessage());
        }
        db.getCommandSystem().sendCmd(response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetAccountToDbCmd that = (GetAccountToDbCmd) o;
        return accId == that.accId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accId);
    }

    public long getAccId() {
        return accId;
    }
}
