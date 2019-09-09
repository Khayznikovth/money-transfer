package ru.khayz.db;

import ru.khayz.db.model.Account;
import ru.khayz.db.model.Client;
import ru.khayz.ms.Subscriber;

import java.sql.Date;
import java.util.List;

/**
 * @author Cinimex
 * @version 1.0
 * @since 9/7/2019 2:03 PM
 */
public interface DbService extends Subscriber, Runnable {
    /**
     *
     * @param id
     * @return
     */
    Account getAccount(long id) throws DbException;

    /**
     *
     * @param name
     * @param birthDate
     * @param phone
     * @return
     * @throws DbException
     */
    long addClient(String name, Date birthDate, String phone) throws DbException;

    /**
     *
     * @param clientId
     * @return
     */
    long addAccount(long clientId) throws DbException;

    /**
     *
     * @param accFromId
     * @param accToId
     * @param amount
     */
    void sendMoney(long accFromId, long accToId, long amount) throws DbException;

    /**
     * Check if account has at least given in {@code amount} money
     * @param accountId - Account identifier into DB
     * @param amount    - Amount on account
     * @return          - {@code true} if into Account table into DB there are at least {@code amount} money
     *                    {@code false} otherwise
     */
    boolean checkAmount(long accountId, long amount) throws DbException;


    /**
     *
     * @param clientId
     * @return
     */
    Client getClient(long clientId) throws DbException;

    /**
     *
     * @param clientId
     * @param id
     */
    void setClientPrefferedAccount(long clientId, long id) throws DbException;

    void addToAccount(long accountId, long amount) throws DbException;

    List<Account> getClientAccounts(long id) throws DbException;

    void sendMoneyByClients(long clientFromId, long clientToId, long amount) throws DbException;
}
