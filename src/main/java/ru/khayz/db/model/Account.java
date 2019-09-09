package ru.khayz.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Random;

/**
 *
 */
@Entity
@Table(name = "accounts")
public class Account implements Serializable {
    private static final long serialVersionUID = -6389183881984468625L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "client_id", nullable = false)
    private long clientId;

    @Column(name = "amount", nullable = false)
    private long amount = 0;

    @Column(name = "account_number", nullable = false, unique = true, precision = 20)
    private String accountNumber;

    public Account() {}

    public Account(long clientId, String account_number) {
        this.clientId = clientId;
        this.accountNumber = account_number;
    }

    public Account(long clientId) {
        this.id = -1;
        this.clientId = clientId;
        this.accountNumber = generateAccountNumber();
    }

    private String generateAccountNumber() {
        String zeros = "00000000000000000000";
        String value = String.valueOf(Math.abs(new Random().nextLong()));
        return zeros.concat(value).substring(value.length(), value.length()+20);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        String template = "<account>" +
                "<id>%d</id>\r\n" +
                "    <client_id>%d</client_id>\r\n" +
                "    <amount>%d</amount>\r\n" +
                "    <account_number>%s</account_number>\r\n" +
                "</account>\r\n";
        return String.format(template, id, clientId, amount, accountNumber);
    }
}
