package ru.khayz.db;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.khayz.db.dao.AccountDAO;
import ru.khayz.db.model.AccountSet;
import ru.khayz.db.model.ClientSet;
import ru.khayz.server.Application;

public class DbServiceImpl implements DbService {
    private static final String hibernate_show_sql = "true";
    private static final String hibernate_hbm2ddl_auto = "update";

    private final SessionFactory sessionFactory;

    public DbServiceImpl() {
        Configuration configuration = getConfiguration();
        sessionFactory = createSessionFactory(configuration);
    }

    private SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    private Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(AccountSet.class);
        configuration.addAnnotatedClass(ClientSet.class);

        configuration.setProperty("hibernate.dialect", Application.getPropertyValue("hibernate.dialect"));
        configuration.setProperty("hibernate.connection.driver_class", Application.getPropertyValue("hibernate.connection.driver_class"));
        configuration.setProperty("hibernate.connection.url", Application.getPropertyValue("hibernate.connection.url"));
        configuration.setProperty("hibernate.connection.username", Application.getPropertyValue("hibernate.connection.username"));
        configuration.setProperty("hibernate.connection.password", Application.getPropertyValue("hibernate.connection.password"));
        configuration.setProperty("hibernate.show_sql", hibernate_show_sql);
        configuration.setProperty("hibernate.hbm2ddl.auto", hibernate_hbm2ddl_auto);
        return configuration;
    }

    @Override
    public AccountSet getAccount(long accountId) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            AccountDAO dao = new AccountDAO(session);
            AccountSet data = dao.get(accountId);
            return data;
        } catch (HibernateException e) {
            throw new DbException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long addAccount(long clientId) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            AccountDAO dao = new AccountDAO(session);
            long data = dao.add(clientId);
            transaction.commit();
            return data;
        } catch (HibernateException e) {
            throw new DbException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public long addAccount(long clientId, String accountNumber) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            AccountDAO dao = new AccountDAO(session);
            long data = dao.add(clientId, accountNumber);
            transaction.commit();
            return data;
        } catch (HibernateException e) {
            throw new DbException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void sendMoney(long accFromId, long accToId, long amount) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            AccountDAO dao = new AccountDAO(session);
            AccountSet from = dao.get(accFromId);
            AccountSet to   = dao.get(accToId);

            if (from == null || to == null) {
                return;
            } else if (from.getAmount() < amount) {
                return;
            }

            Transaction transaction = session.beginTransaction();
            dao.transfer(from, to, amount);
            transaction.commit();
            return;
        } catch (HibernateException e) {
            throw new DbException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public boolean checkAmount(long accountId, long amount) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            AccountDAO dao = new AccountDAO(session);
            AccountSet data = dao.get(accountId);
            if (data == null) {
                return false;
            }
            return data.getAmount() >= amount;
        } catch (HibernateException e) {
            throw new DbException(e);
        } finally {
            closeSession(session);
        }
    }

    private void closeSession(Session session) {
        if (session != null) {
            session.close();
        }
    }
}
