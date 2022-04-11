package io.agrest.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table (name = "e23")
public class E23  {

    @Id
    @Column(name = "id")
    private Long exposedId;

    protected String name;

    @OneToMany
    protected List<E26> e26s = new java.util.ArrayList<>();

    public void setE26s(List<E26> e26s) {
        this.e26s = e26s;
    }

    public List<E26> getE26s() {
        return e26s;
    }

    public Long getExposedId() {
        return exposedId;
    }

    public void setExposedId(Long exposedId) {
        this.exposedId = exposedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}