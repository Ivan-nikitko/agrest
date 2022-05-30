package io.agrest.jpa.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "e35")
public  class E35 {

    public static final String E2 = "e2";
    public static final String NAME = "name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    protected String address;

    protected String name;

    @OneToOne
    @JoinColumn(name = "e2_id")
    protected E2 e2;

    @OneToMany(mappedBy = "e35", cascade = CascadeType.ALL)
    protected List<E36> e36 = new java.util.ArrayList<>();

    public E2 getE2() {
        return e2;
    }

    public Integer getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<E36> getE36s() {
        return e36;
    }

    public void setE3s(List<E36> e36s) {
        this.e36 = e36;
    }


}
