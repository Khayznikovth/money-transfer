package ru.khayz.ms.cmd.db;

import ru.khayz.db.DbException;
import ru.khayz.db.DbService;
import ru.khayz.ms.Address;
import ru.khayz.ms.Cmd;
import ru.khayz.ms.cmd.ToDbCmd;
import ru.khayz.ms.cmd.server.AddToAccountToServerCmd;

import javax.servlet.AsyncContext;

import java.util.Objects;

import static ru.khayz.ms.cmd.ToServerCmd.ResultCode;

public class AddToAccountToDbCmd extends ToDbCmd {
    private long accId;
    private long amount;

    public AddToAccountToDbCmd(Address from, Address to, AsyncContext context, long accountId, long amount) {
        super(from, to, context);
        this.accId = accountId;
        this.amount = amount;
    }

    @Override
    public void exec(DbService db) {
        Cmd response;
        try {
            db.addToAccount(accId, amount);
            response = new AddToAccountToServerCmd(getTo(), getFrom(), getContext(), ResultCode.SUCCESS, "Success");
        } catch (DbException e) {
            response = new AddToAccountToServerCmd(getTo(), getFrom(), getContext(), ResultCode.ERROR, e.getMessage());
        }
        db.getCommandSystem().sendCmd(response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddToAccountToDbCmd that = (AddToAccountToDbCmd) o;
        return accId == that.accId &&
                amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accId, amount);
    }

    public long getAccId() {
        return accId;
    }

    public long getAmount() {
        return amount;
    }
}
