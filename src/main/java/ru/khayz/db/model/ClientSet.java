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
public class ClientSet implements Serializable {
    private static final long serialVersionUID = -4203310066700861288L;

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
    private long prefferedAccount;

    public ClientSet() {}

    public ClientSet(String name) {
        this.id = -1;
        this.name = name;
    }

    public ClientSet(long id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
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
        ClientSet that = (ClientSet) o;
        return id == that.id &&
                name.equals(that.name) &&
                Objects.equals(birthDate, that.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, birthDate);
    }
}
