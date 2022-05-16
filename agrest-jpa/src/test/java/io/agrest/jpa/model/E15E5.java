package io.agrest.jpa.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// This entity is used only for table e15_e5 cleanup
@Entity
@Table(name = "e15_e5")
public class E15E5 implements Serializable {

    @Id
    private Long e5_id;

    @Id
    private Long e15_id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        E15E5 e15E5 = (E15E5) o;

        if (e5_id != null ? !e5_id.equals(e15E5.e5_id) : e15E5.e5_id != null) return false;
        return e15_id != null ? e15_id.equals(e15E5.e15_id) : e15E5.e15_id == null;
    }

    @Override
    public int hashCode() {
        int result = e5_id != null ? e5_id.hashCode() : 0;
        result = 31 * result + (e15_id != null ? e15_id.hashCode() : 0);
        return result;
    }
}
