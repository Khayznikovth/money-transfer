package ru.khayz.db;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.khayz.db.dao.AccountDAO;
import ru.khayz.db.dao.ClientDAO;
import ru.khayz.db.model.Account;
import ru.khayz.db.model.Client;
import ru.khayz.ms.Address;
import ru.khayz.ms.CmdSystem;
import ru.khayz.server.Application;

import java.sql.Date;
import java.util.List;

public class DbServiceImpl implements DbService {
    private static final String hibernate_show_sql = "true";
    private static final String hibernate_hbm2ddl_auto = "create";

    private final SessionFactory sessionFactory;
    private final CmdSystem cs;
    private final Address address;

    public DbServiceImpl(CmdSystem commandSystem) {
        Configuration configuration = getConfiguration();
        sessionFactory = createSessionFactory(configuration);
        this.cs = commandSystem;
        address = new Address();
    }

    private SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Account.class);
        configuration.addAnnotatedClass(Client.class);

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
    public Account getAccount(long accountId) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            AccountDAO dao = new AccountDAO(session);
            Account data = dao.get(accountId);
            transaction.commit();
            if (data == null) {
                throw new DbException("Account with given id doesn't exist");
            }
            return data;
        } catch (HibernateException e) {
            throw new DbException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public synchronized long addClient(String name, Date birthDate, String phone) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            ClientDAO dao = new ClientDAO(session);
            long data = dao.add(name, phone, birthDate);
            transaction.commit();
            return data;
        } catch (HibernateException | IllegalArgumentException e) {
            throw new DbException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public synchronized long addAccount(long clientId) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            AccountDAO dao = new AccountDAO(session);
            ClientDAO cDao = new ClientDAO(session);

            // Check if client exist
            Client client = cDao.get(clientId);
            if (client == null) {
                throw new DbException("An attempt to create account for non-existing Client");
            }

            // Create account
            long accId = dao.add(clientId);

            // If Client has no preffered account then update client
            if (client.getPrefferedAccount() == Client.NO_PREFFERED_ACCOUNT) {
                cDao.setPrefferedAccount(clientId, accId);
            }
            transaction.commit();
            return accId;
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
            Transaction transaction = session.beginTransaction();
            AccountDAO dao = new AccountDAO(session);
            Account from = dao.get(accFromId);
            Account to   = dao.get(accToId);

            if (from == null || to == null) {
                return;
            } else if (from.getAmount() < amount) {
                return;
            }

            dao.transfer(from, to, amount);
            transaction.commit();
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
            Transaction transaction = session.beginTransaction();
            AccountDAO dao = new AccountDAO(session);
            Account data = dao.get(accountId);
            transaction.commit();
            if (data == null) {
                throw new DbException(String.format("Account with given id: %d doesn't exist", accountId));
            }
            return data.getAmount() >= amount;
        } catch (HibernateException e) {
            throw new DbException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public Client getClient(long clientId) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            ClientDAO dao = new ClientDAO(session);
            Client data = dao.get(clientId);
            transaction.commit();
            if (data == null) {
                throw new DbException("Client with given id doesn't exist");
            }
            return data;
        } catch (HibernateException e) {
            throw new DbException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void setClientPrefferedAccount(long clientId, long accountId) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();

            AccountDAO aDao = new AccountDAO(session);
            Account acc = aDao.get(accountId);
            if (clientId != acc.getClientId()) {
                throw new DbException("Given account doesn't own by given client");
            }

            ClientDAO cDao = new ClientDAO(session);
            cDao.setPrefferedAccount(clientId, accountId);

            transaction.commit();
        } catch (HibernateException e) {
            throw new DbException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void addToAccount(long accountId, long amount) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            AccountDAO dao = new AccountDAO(session);
            Account account = dao.get(accountId);
            if (account == null) {
                throw new DbException(String.format("There is no account with given id: %d", accountId));
            }
            if (account.getAmount() + amount < 0) {
                throw new DbException("Account with given id has not enough money for operation");
            }
            dao.addAmount(accountId, amount);
            transaction.commit();
        } catch (HibernateException e) {
            throw new DbException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public List<Account> getClientAccounts(long clientId) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            AccountDAO dao = new AccountDAO(session);
            List<Account> data = dao.getClientAccounts(clientId);
            transaction.commit();
            return data;
        } catch (HibernateException e) {
            throw new DbException(e);
        } finally {
            closeSession(session);
        }
    }

    @Override
    public void sendMoneyByClients(long clientFromId, long clientToId, long amount) throws DbException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            ClientDAO cDao = new ClientDAO(session);
            Client clientFrom = cDao.get(clientFromId);
            if (clientFrom == null) {
                throw new DbException(String.format("Client with given id doesn't exist", clientFromId));
            }
            Client clientTo = cDao.get(clientToId);
            if (clientTo == null) {
                throw new DbException(String.format("Client with given id %d doesn't exist", clientToId));
            }
            long accFromId = clientFrom.getPrefferedAccount();
            long accToId   = clientTo.getPrefferedAccount();
            if (accToId == Client.NO_PREFFERED_ACCOUNT) {
                throw new DbException(String.format("Client %d doesn't has a default account for money transfer", clientToId));
            }
            if (accFromId == Client.NO_PREFFERED_ACCOUNT) {
                throw new DbException(String.format("Client doesn't has a default account for money transfer", clientFromId));
            }

            AccountDAO dao = new AccountDAO(session);
            // That accounts by application logic should exist
            Account from = dao.get(accFromId);
            Account to   = dao.get(accToId);

            if (from == null) {
                throw new DbException(String.format("Account marked as default for client %d doesn't exist", clientFromId));
            }
            if (to == null) {
                throw new DbException(String.format("Account marked as default for client %d doesn't exist", clientToId));
            }

            //check if there is enough money for transfer
            if (from.getAmount() < amount) {
                return;
            }
            dao.transfer(from, to, amount);
            transaction.commit();
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

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public CmdSystem getCommandSystem() {
        return cs;
    }

    @Override
    public void run() {
        while (true) {
            cs.execForSubscriber(this);
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
