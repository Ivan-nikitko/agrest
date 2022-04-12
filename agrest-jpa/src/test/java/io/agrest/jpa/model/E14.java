package io.agrest.jpa.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table
public  class E14 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long long_id;

    protected String name;

    @OneToOne
    protected E15 e15;

    public Long getLong_id() {
        return long_id;
    }

    public void setLong_id(Long long_id) {
        this.long_id = long_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public E15 getE15() {
        return e15;
    }

    public void setE15(E15 e15) {
        this.e15 = e15;
    }
}
