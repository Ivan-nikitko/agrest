package io.agrest.encoder;

import io.agrest.encoder.converter.ISODateTimeConverter;
import io.agrest.encoder.converter.StringConverter;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ISODateTimeConverterTest {

    private StringConverter converter = ISODateTimeConverter.converter();

    @Test
    public void testISODateTimeConverter() {
        _testISODateTimeConverter(1458995247000L, "yyyy-MM-dd'T'HH:mm:ss");
    }

    @Test
    public void testISODateTimeConverter_FractionalPart1() {
        _testISODateTimeConverter(1458995247001L, "yyyy-MM-dd'T'HH:mm:ss.SSS");
    }

    @Test
    public void testISODateTimeConverter_FractionalPart2() {
        _testISODateTimeConverter(1458995247100L, "yyyy-MM-dd'T'HH:mm:ss.S");
    }

    /**
     * Prints java.util.Date in the server's local timezone
     */
    private void _testISODateTimeConverter(long millis, String expectedPattern) {
        String expected = DateTimeFormatter.ofPattern(expectedPattern).format(fromMillis(millis));
        assertEquals(expected, converter.asString(new Date(millis)));
    }

    private static LocalDateTime fromMillis(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }
}
