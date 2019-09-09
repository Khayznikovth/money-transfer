package ru.khayz.ms.cmd.server;

import ru.khayz.ms.Address;
import ru.khayz.ms.cmd.ToServerCmd;

import javax.servlet.AsyncContext;
import java.util.Objects;

public class AddClientToServerCmd extends ToServerCmd {
    private long id;
    public AddClientToServerCmd(Address from, Address to, AsyncContext context, ResultCode code, String message) {
        super(from, to, context, code, message);
    }

    public AddClientToServerCmd(Address from, Address to, AsyncContext context, ResultCode code, long id) {
        super(from, to, context, code);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddClientToServerCmd that = (AddClientToServerCmd) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
