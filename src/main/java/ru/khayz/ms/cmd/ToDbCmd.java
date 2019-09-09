package ru.khayz.ms.cmd;

import ru.khayz.db.DbService;
import ru.khayz.ms.Address;
import ru.khayz.ms.Cmd;
import ru.khayz.ms.Subscriber;

import javax.servlet.AsyncContext;

public abstract class ToDbCmd extends Cmd {
    public ToDbCmd(Address from, Address to, AsyncContext context) {
        super(from, to, context);
    }

    @Override
    public void exec(Subscriber subscr) {
        if (subscr instanceof DbService) {
            exec((DbService) subscr);
        }
    }

    public abstract void exec(DbService db);
}
