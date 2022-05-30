package io.agrest.jpa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "e36")
public class E36 {

    public static final String E35 = "e35";
    public static final String NAME = "name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    protected String name;

    @Column(name = "phone_number")
    protected String phoneNumber;

    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "e35_id")
    protected E35 e35;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public E35 getE35() {
        return e35;
    }

    public void set35(E35 e35) {
        this.e35 = e35;
    }


}
