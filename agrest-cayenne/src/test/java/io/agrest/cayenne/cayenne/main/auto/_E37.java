package io.agrest.cayenne.cayenne.main.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.property.EmbeddableProperty;
import org.apache.cayenne.exp.property.PropertyFactory;
import org.apache.cayenne.exp.property.StringProperty;

import io.agrest.cayenne.cayenne.main.E37EmbeddedClass;

/**
 * Class _E37 was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _E37 extends BaseDataObject {

    private static final long serialVersionUID = 1L;

    public static final String ID_PK_COLUMN = "id";

    public static final EmbeddableProperty<E37EmbeddedClass> ADDRESS = PropertyFactory.createEmbeddable("address", E37EmbeddedClass.class);
    public static final StringProperty<String> NAME = PropertyFactory.createString("name", String.class);

    protected E37EmbeddedClass address;
    protected String name;


    public void setAddress(E37EmbeddedClass address) {
        beforePropertyWrite("address", this.address, address);
        this.address = address;
    }

    public E37EmbeddedClass getAddress() {
        beforePropertyRead("address");
        return this.address;
    }

    public void setName(String name) {
        beforePropertyWrite("name", this.name, name);
        this.name = name;
    }

    public String getName() {
        beforePropertyRead("name");
        return this.name;
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "address":
                return this.address;
            case "name":
                return this.name;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "address":
                this.address = (E37EmbeddedClass)val;
                break;
            case "name":
                this.name = (String)val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.address);
        out.writeObject(this.name);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.address = (E37EmbeddedClass)in.readObject();
        this.name = (String)in.readObject();
    }

}