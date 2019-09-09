package ru.khayz.ms.cmd.server;

import ru.khayz.db.model.Account;
import ru.khayz.ms.Address;
import ru.khayz.ms.cmd.ToServerCmd;

import javax.servlet.AsyncContext;
import java.util.List;
import java.util.Objects;

public class GetClientAccountsToServerCmd extends ToServerCmd {
    private List<Account> accounts;
    public GetClientAccountsToServerCmd(Address from, Address to, AsyncContext context, ResultCode result, List<Account> accounts) {
        super(from, to, context, result);
        this.accounts = accounts;
    }

    public GetClientAccountsToServerCmd(Address from, Address to, AsyncContext context, ResultCode result, String message) {
        super(from, to, context, result, message);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GetClientAccountsToServerCmd that = (GetClientAccountsToServerCmd) o;
        return Objects.equals(accounts, that.accounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accounts);
    }
}
