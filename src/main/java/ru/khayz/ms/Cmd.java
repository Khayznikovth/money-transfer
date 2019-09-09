package ru.khayz.ms;

import javax.servlet.AsyncContext;
import java.util.Objects;

public abstract class Cmd {
    final private Address from;
    final private Address to;
    final private AsyncContext context;

    public Cmd(Address from, Address to, AsyncContext context) {
        this.from = from;
        this.to = to;
        this.context = context;
    }

    protected Address getFrom() {
        return from;
    }

    protected Address getTo() {
        return to;
    }

    public AsyncContext getContext() {
        return context;
    }

    public abstract void exec(Subscriber subscr);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cmd cmd = (Cmd) o;
        return Objects.equals(from, cmd.from) &&
                Objects.equals(to, cmd.to) &&
                Objects.equals(context, cmd.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, context);
    }
}
