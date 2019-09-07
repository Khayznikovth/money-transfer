package ru.khayz.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Currency;

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
    private long amount;

    @Column(name = "currency", nullable = false)
    private Currency currency;

    public AccountSet() {}

    public AccountSet(long id, long clientId, long amount, String currCode) {
        this.id = id;
        this.clientId = clientId;
        this.amount = amount;
        this.currency = Currency.getInstance(currCode);
    }

    public AccountSet(long clientId, String currCode) {
        this.id = -1;
        this.clientId = clientId;
        this.amount = 0;
        this.currency = Currency.getInstance(currCode);
    }
}
