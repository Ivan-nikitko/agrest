package io.agrest.runtime.encoder;

import io.agrest.encoder.Encoder;
import io.agrest.runtime.jackson.IJacksonService;
import io.agrest.runtime.jackson.JacksonService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Encoders {

    private static final IJacksonService JACKSON = new JacksonService();

    public static String toJson(Encoder encoder, Object value) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            JACKSON.outputJson(g -> encoder.encode(null, value, g), out);
        } catch (IOException e) {
            throw new RuntimeException("Encoding error: " + e.getMessage());
        }

        return out.toString();
    }
}
