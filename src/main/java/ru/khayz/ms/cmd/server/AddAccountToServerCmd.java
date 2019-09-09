package ru.khayz.ms.cmd.server;

import ru.khayz.ms.Address;
import ru.khayz.ms.cmd.ToServerCmd;

import javax.servlet.AsyncContext;
import java.util.Objects;

public class AddAccountToServerCmd extends ToServerCmd {
    private long accId;

    public AddAccountToServerCmd(Address from, Address to, AsyncContext context, ResultCode code, String message) {
        super(from, to, context, code, message);
    }

    public AddAccountToServerCmd(Address from, Address to, AsyncContext context, ResultCode code, long accId) {
        super(from, to, context, code);
        this.accId = accId;
    }

    public long getAccId() {
        return accId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddAccountToServerCmd that = (AddAccountToServerCmd) o;
        return accId == that.accId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accId);
    }
}
