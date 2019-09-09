package ru.khayz.db.dao;

import org.hibernate.Session;
import ru.khayz.db.model.Client;

import java.sql.Date;

public class ClientDAO {
    private Session session;

    public ClientDAO(Session session) {
        this.session = session;
    }

    public Client get(long id) {
        return session.get(Client.class, id);
    }

    public long add(String name) {
        return (Long) session.save(new Client(name));
    }

    public long add(String name, String phone, Date birthDate) {
        Client client = new Client(name, phone, birthDate);
        return (Long) session.save(client);
    }

    public void setPrefferedAccount(long id, long accountId) {
        Client client = session.get(Client.class, id);
        client.setPrefferedAccount(accountId);
        session.save(client);
    }
}
