package io.agrest.base.protocol.exp;

import io.agrest.base.protocol.CayenneExp;

import java.util.Objects;

/**
 * @since 3.8
 */
public class SimpleExp implements CayenneExp {

    private final String template;

    public SimpleExp(String template) {
        this.template = Objects.requireNonNull(template);
    }

    @Override
    public void visit(ExpVisitor visitor) {
        visitor.visitSimpleExp(this);
    }

    public String getTemplate() {
        return template;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleExp simpleExp = (SimpleExp) o;
        return template.equals(simpleExp.template);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template);
    }

    @Override
    public String toString() {
        return template;
    }
}
