package ru.khayz.ms.cmd.db;

import ru.khayz.db.DbException;
import ru.khayz.db.DbService;
import ru.khayz.ms.Address;
import ru.khayz.ms.Cmd;
import ru.khayz.ms.cmd.ToDbCmd;
import ru.khayz.ms.cmd.server.CheckAmountToServerCmd;

import javax.servlet.AsyncContext;

import java.util.Objects;

import static ru.khayz.ms.cmd.ToServerCmd.ResultCode;

public class CheckAmountToDbCmd extends ToDbCmd {
    private long accId;
    private long amount;

    public CheckAmountToDbCmd(Address from, Address to, AsyncContext context, long accId, long amount) {
        super(from, to, context);
        this.accId = accId;
        this.amount = amount;
    }

    @Override
    public void exec(DbService db) {
        Cmd response;
        try {
            boolean enough = db.checkAmount(accId, amount);
            response = new CheckAmountToServerCmd(getTo(), getFrom(), getContext(), ResultCode.SUCCESS, enough);
        } catch (DbException e) {
            response = new CheckAmountToServerCmd(getTo(), getFrom(), getContext(), ResultCode.ERROR, e.getMessage());
        }
        db.getCommandSystem().sendCmd(response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckAmountToDbCmd that = (CheckAmountToDbCmd) o;
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
