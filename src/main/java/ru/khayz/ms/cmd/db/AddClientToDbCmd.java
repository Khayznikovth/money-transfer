package ru.khayz.ms.cmd.db;

import ru.khayz.db.DbException;
import ru.khayz.db.DbService;
import ru.khayz.ms.Address;
import ru.khayz.ms.Cmd;
import ru.khayz.ms.cmd.ToDbCmd;
import ru.khayz.ms.cmd.server.AddClientToServerCmd;

import javax.servlet.AsyncContext;
import java.sql.Date;
import java.util.Objects;

import static ru.khayz.ms.cmd.ToServerCmd.ResultCode;

public class AddClientToDbCmd extends ToDbCmd {
    private final String name;

    private final String phone;
    private final Date   birthDate;
    public AddClientToDbCmd(Address from, Address to, AsyncContext context, String name, String phone, Date birthDate) {
        super(from, to, context);
        this.name      = name;
        this.phone     = phone;
        this.birthDate = birthDate;
    }

    @Override
    public void exec(DbService db) {
        Cmd response;
        try {
            long id = db.addClient(name, birthDate, phone);
            response = new AddClientToServerCmd(getTo(), getFrom(), getContext(), ResultCode.SUCCESS, id);
        } catch (DbException e) {
            response = new AddClientToServerCmd(getTo(), getFrom(), getContext(), ResultCode.ERROR, e.getMessage());
        }
        db.getCommandSystem().sendCmd(response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddClientToDbCmd that = (AddClientToDbCmd) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(birthDate, that.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, birthDate);
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Date getBirthDate() {
        return birthDate;
    }
}
