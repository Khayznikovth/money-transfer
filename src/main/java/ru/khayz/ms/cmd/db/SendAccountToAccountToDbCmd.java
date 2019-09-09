package ru.khayz.ms.cmd.db;

import ru.khayz.db.DbException;
import ru.khayz.db.DbService;
import ru.khayz.ms.Address;
import ru.khayz.ms.Cmd;
import ru.khayz.ms.cmd.ToDbCmd;
import ru.khayz.ms.cmd.server.SendAccountToAccountToServerCmd;

import javax.servlet.AsyncContext;

import java.util.Objects;

import static ru.khayz.ms.cmd.ToServerCmd.ResultCode;

public class SendAccountToAccountToDbCmd extends ToDbCmd {
    private long accFromId;
    private long accToId;
    private long amount;

    public SendAccountToAccountToDbCmd(Address from, Address to, AsyncContext context, long accFromId, long accToId, long amount) {
        super(from, to, context);
        this.accFromId = accFromId;
        this.accToId   = accToId;
        this.amount    = amount;
    }

    @Override
    public void exec(DbService db) {
        Cmd response;
        try {
            db.sendMoney(accFromId, accToId, amount);
            response = new SendAccountToAccountToServerCmd(getTo(), getFrom(), getContext(), ResultCode.SUCCESS, "Success");
        } catch (DbException e) {
            response = new SendAccountToAccountToServerCmd(getTo(), getFrom(), getContext(), ResultCode.ERROR, e.getMessage());
        }
        db.getCommandSystem().sendCmd(response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SendAccountToAccountToDbCmd that = (SendAccountToAccountToDbCmd) o;
        return accFromId == that.accFromId &&
                accToId == that.accToId &&
                amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accFromId, accToId, amount);
    }

    public long getAccFromId() {
        return accFromId;
    }

    public long getAccToId() {
        return accToId;
    }

    public long getAmount() {
        return amount;
    }
}
