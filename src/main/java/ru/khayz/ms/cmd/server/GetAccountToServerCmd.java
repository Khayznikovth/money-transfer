package ru.khayz.ms.cmd.server;

import ru.khayz.db.model.Account;
import ru.khayz.ms.Address;
import ru.khayz.ms.cmd.ToServerCmd;

import javax.servlet.AsyncContext;
import java.util.Objects;

public class GetAccountToServerCmd extends ToServerCmd {
    private Account account;
    public GetAccountToServerCmd(Address from, Address to, AsyncContext context, ResultCode result, Account account) {
        super(from, to, context, result);
        this.account = account;
    }

    public GetAccountToServerCmd(Address from, Address to, AsyncContext context, ResultCode result, String message) {
        super(from, to, context, result, message);
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GetAccountToServerCmd that = (GetAccountToServerCmd) o;
        return Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), account);
    }
}
