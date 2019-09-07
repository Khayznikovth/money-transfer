package ru.khayz.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Random;

/**
 *
 */
@Entity
@Table(name = "accounts")
public class AccountSet implements Serializable {
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

    public AccountSet() {}

    public AccountSet(long clientId, String account_number) {
        this.clientId = clientId;
        this.accountNumber = account_number;
    }

    public AccountSet(long clientId) {
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
}
