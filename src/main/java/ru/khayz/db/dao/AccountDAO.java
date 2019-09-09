package ru.khayz.db.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.khayz.db.DbException;
import ru.khayz.db.model.Account;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class AccountDAO {
    private Session session;

    public AccountDAO(Session session) {
        this.session = session;
    }

    public Account get(long id) {
        return session.get(Account.class, id);
    }

    public long add(long clientId) {
        return (Long) session.save(new Account(clientId));
    }

    public boolean transfer(Account from, Account to, long amount) {
        from.setAmount(from.getAmount() - amount);
        to.setAmount(to.getAmount() + amount);
        session.update(from);
        session.update(to);
        return true;
    }

    public List<Account> getClientAccounts(long clientId) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Account> cq = cb.createQuery(Account.class);
        Root<Account> root = cq.from(Account.class);
        cq.select(root).where(cb.equal(root.get("clientId"), clientId));
        Query<Account> query = session.createQuery(cq);
        return query.getResultList();
    }

    public void addAmount(long id, long amount) throws DbException {
        Account account = session.get(Account.class, id);
        if (account == null) {
            throw new DbException("Given account doesn't exist");
        }
        account.setAmount(account.getAmount() + amount);
        session.update(account);
    }
}
