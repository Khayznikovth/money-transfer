package ru.khayz.db.dao;

import com.sun.istack.NotNull;
import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.khayz.db.model.AccountSet;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class AccountDAO {
    private Session session;

    public AccountDAO(Session session) {
        this.session = session;
    }

    public AccountSet get(long id) {
        return session.get(AccountSet.class, id);
    }

    public long add(long clientId) {
        return (Long) session.save(new AccountSet(clientId));
    }

    public long add(long clientId, @NotNull String accountNumber) {
        return (Long) session.save(new AccountSet(clientId, accountNumber));
    }

    public boolean transfer(@NotNull AccountSet from, @NotNull AccountSet to, long amount) {
        from.setAmount(from.getAmount() - amount);
        to.setAmount(to.getAmount() + amount);
        session.update(from);
        session.update(to);
        return true;
    }

    public List<AccountSet> getAll() {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<AccountSet> cq = cb.createQuery(AccountSet.class);
        cq.select(cq.from(AccountSet.class));
        Query<AccountSet> query = session.createQuery(cq);
        return query.getResultList();
    }
}
