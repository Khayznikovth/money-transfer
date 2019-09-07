package ru.khayz.db.dao;

import org.hibernate.Session;
import ru.khayz.db.model.ClientSet;

public class ClientDAO {
    private Session session;

    public ClientDAO(Session session) {
        this.session = session;
    }

    public ClientSet get(long id) {
        return session.get(ClientSet.class, id);
    }


}
