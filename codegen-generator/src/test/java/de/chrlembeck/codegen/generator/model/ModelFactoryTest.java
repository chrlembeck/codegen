package de.chrlembeck.codegen.generator.model;

import java.lang.reflect.Array;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelFactoryTest {

    @Test
    public void testByMethodCallStatic() throws Exception {
        final Object model = ModelFactory.byMethodCall("java.lang.System", "getenv");
        Assertions.assertNotNull(model);
        Assertions.assertTrue(model instanceof Map);
    }

    @Test
    public void testByMethodCallInstance() throws Exception {
        final Object model = ModelFactory.byMethodCall("java.util.ArrayList", "toArray");
        Assertions.assertNotNull(model);
        Assertions.assertTrue(model.getClass().isArray());
        Assertions.assertEquals(0, Array.getLength(model));
    }
}