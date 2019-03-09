package io.agrest.meta.compiler;

import io.agrest.AgException;

import javax.ws.rs.core.Response;
import java.lang.reflect.Method;

/**
 * @since 2.10
 */
public class PropertyGetter {

    private String name;
    private Class<?> type;
    private Method method;

    public PropertyGetter(String name, Class<?> type, Method method) {
        this.name = name;
        this.type = type;
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public Method getMethod() {
        return method;
    }

    public Object getValue(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return method.invoke(object, (Object[]) null);
        } catch (Throwable th) {
            throw new AgException(Response.Status.INTERNAL_SERVER_ERROR, "Error reading property: " + name, th);
        }
    }
}
