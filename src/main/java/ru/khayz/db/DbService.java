package ru.khayz.db;

import org.hibernate.HibernateException;
import ru.khayz.db.model.AccountSet;

/**
 * @author Cinimex
 * @version 1.0
 * @since 9/7/2019 2:03 PM
 */
public interface DbService {
    /**
     *
     * @param id
     * @return
     */
    AccountSet getAccount(long id) throws HibernateException, DbException;

    /**
     *
     * @param clientId
     * @return
     */
    long addAccount(long clientId) throws HibernateException, DbException;

    /**
     *
     * @param clientId
     * @param accountNumber
     * @return
     * @throws DbException
     */
    long addAccount(long clientId, String accountNumber) throws DbException;

    /**
     *
     * @param accFromId
     * @param accToId
     * @param amount
     */
    void sendMoney(long accFromId, long accToId, long amount) throws HibernateException, DbException;

    /**
     * Check if account has at least given in {@code amount} money
     * @param accountId - Account identifier into DB
     * @param amount    - Amount on account
     * @return          - {@code true} if into Account table into DB there are at least {@code amount} money
     *                    {@code false} otherwise
     */
    boolean checkAmount(long accountId, long amount) throws HibernateException, DbException;



}
