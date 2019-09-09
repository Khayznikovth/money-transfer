package ru.khayz.db.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

/**
 *
 */
@Entity
@Table(name = "clients")
public class Client implements Serializable {
    private static final long serialVersionUID = -4203310066700861288L;
    public static final long NO_PREFFERED_ACCOUNT = -1;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", updatable = false)
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "default_account")
    private long prefferedAccount = NO_PREFFERED_ACCOUNT;

    public Client() {}

    public Client(String name) throws IllegalArgumentException {
        this.id = -1;
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Client name is empty");
        }
        this.name = name;
    }

    public Client(String name, String phone, Date birthDate) throws IllegalArgumentException {
        this.id = -1;
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Client name is empty");
        }
        this.name = name;
        this.phone = phone;
        if (birthDate != null) {
            if (birthDate.compareTo(new Date(System.currentTimeMillis())) >= 0) {
                throw new IllegalArgumentException("Client birth date is incorrect");
            }
            this.birthDate = birthDate;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getPrefferedAccount() {
        return prefferedAccount;
    }

    public void setPrefferedAccount(long prefferedAccount) {
        this.prefferedAccount = prefferedAccount;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client that = (Client) o;
        return id == that.id &&
                name.equals(that.name) &&
                Objects.equals(birthDate, that.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, birthDate);
    }
}
