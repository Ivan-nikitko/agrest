package io.agrest.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "e3")
public class E3 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_;

    @Column(name = "name")
    protected String name;

    @Column(name = "phone_number")
    protected String phoneNumber;

    @OneToOne
    protected E2 e2;

    @OneToOne
    protected E5 e5;

    public Integer getId_() {
        return id_;
    }

    public void setId_(Integer id) {
        this.id_ = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public E2 getE2() {
        return e2;
    }

    public void setE2(E2 e2) {
        this.e2 = e2;
    }

    public E5 getE5() {
        return e5;
    }

    public void setE5(E5 e5) {
        this.e5 = e5;
    }

    @Override
    public String toString() {
        return "E3{" +
                "id=" + id_ +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", e2=" + e2 +
                ", e5=" + e5 +
                '}';
    }
}