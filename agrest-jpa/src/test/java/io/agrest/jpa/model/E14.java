package io.agrest.jpa.model;

import io.agrest.annotation.AgAttribute;
import io.agrest.annotation.AgRelationship;
import io.agrest.jaxrs2.pojo.model.P7;
import jakarta.persistence.*;
import io.agrest.jaxrs2.pojo.model.P7;

@Entity
@Table
public  class E14 {

    public static final String NAME = "name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long long_id;

    protected String name;

    @OneToOne(cascade = {jakarta.persistence.CascadeType.REMOVE})
    @JoinColumn (name="e14_id")
    protected E15 e15;

    @Transient
    private P7 p7;

    @AgAttribute
    public String getPrettyName() {
        return getName() + "_pretty";
    }

    @AgRelationship
    public P7 getP7() {
        return p7;
    }

    public void setP7(P7 p7) {
        this.p7 = p7;
    }


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
