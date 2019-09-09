package ru.khayz.ms.cmd.server;

import ru.khayz.ms.Address;
import ru.khayz.ms.cmd.ToServerCmd;

import javax.servlet.AsyncContext;
import java.util.Objects;

public class CheckAmountToServerCmd extends ToServerCmd {
    private boolean enough;
    public CheckAmountToServerCmd(Address from, Address to, AsyncContext context, ResultCode result, boolean enough) {
        super(from, to, context, result);
        this.enough = enough;
    }

    public CheckAmountToServerCmd(Address from, Address to, AsyncContext context, ResultCode result, String message) {
        super(from, to, context, result, message);
    }

    public boolean isEnough() {
        return enough;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CheckAmountToServerCmd that = (CheckAmountToServerCmd) o;
        return enough == that.enough;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), enough);
    }
}
