package ru.khayz.ms.cmd;

import ru.khayz.ms.Address;
import ru.khayz.ms.Cmd;
import ru.khayz.ms.Subscriber;
import ru.khayz.server.servlets.CommonServlet;

import javax.servlet.AsyncContext;
import java.util.Objects;

public abstract class ToServerCmd extends Cmd {
    private String message;
    private final ResultCode result;

    public ResultCode getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToServerCmd that = (ToServerCmd) o;
        return Objects.equals(message, that.message) &&
                result == that.result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, result);
    }

    public enum ResultCode {
        SUCCESS,
        ERROR
    }

    public ToServerCmd(Address from, Address to, AsyncContext context, ResultCode result) {
        super(from, to, context);
        this.result = result;
    }

    public ToServerCmd(Address from, Address to, AsyncContext context, ResultCode result, String message) {
        super(from, to, context);
        this.result = result;
        this.message = message;
    }

    @Override
    public void exec(Subscriber subscr) {
        if (subscr instanceof CommonServlet) {
            ((CommonServlet) subscr).processResponse(this);
        }
    }

    public String getMessage() {
        return message;
    }
}
