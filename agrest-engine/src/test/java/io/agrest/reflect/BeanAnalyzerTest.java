package io.agrest.reflect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BeanAnalyzerTest {

    @Test
    public void testPropertyNameFromGetter() {

        assertFalse(BeanAnalyzer.propertyNameFromGetter("get").isPresent());
        assertFalse(BeanAnalyzer.propertyNameFromGetter("xyz").isPresent());
        assertFalse(BeanAnalyzer.propertyNameFromGetter("setXyz").isPresent());
        assertFalse(BeanAnalyzer.propertyNameFromGetter("getxyz").isPresent());


        assertEquals("x", BeanAnalyzer.propertyNameFromGetter("getX").get());
        assertEquals("xyz", BeanAnalyzer.propertyNameFromGetter("getXyz").get());
        assertEquals("xyzAbc", BeanAnalyzer.propertyNameFromGetter("getXyzAbc").get());
        assertEquals("xyz", BeanAnalyzer.propertyNameFromGetter("isXyz").get());
    }
}
