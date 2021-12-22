package io.agrest.encoder;

import io.agrest.encoder.converter.ISODateConverter;
import io.agrest.encoder.converter.StringConverter;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ISODateConverterTest {

    private StringConverter converter = ISODateConverter.converter();

    @Test
    public void testISODateConverter() {
        assertEquals("2016-03-26", converter.asString(new Date(1458995247000L)));
    }
}
