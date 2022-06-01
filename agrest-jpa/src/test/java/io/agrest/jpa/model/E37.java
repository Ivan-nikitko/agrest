package io.agrest.jpa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "e37")
public class E37 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    protected String name;

    @Embedded
    protected E37EmbeddedClass address;

    public E37EmbeddedClass getAddress() {
        return address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
