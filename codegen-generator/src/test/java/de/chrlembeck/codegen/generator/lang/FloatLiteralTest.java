package de.chrlembeck.codegen.generator.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Einige Tests zu den Fließkommaliteralen.
 *
 * @author Christoph Lembeck
 */
public class FloatLiteralTest {

    /**
     * Tests ohne Nachkommastellen.
     */
    @Test
    void testInteger() {
        testFloat("0f", 0f);
        testFloat("5f", 5f);
        testFloat("10f", 10f);
    }

    /**
     * Negative Zahlen oder Nachkommastellen.
     */
    @Test
    void testNegativeInteger() {
        testFloat("-1f", -1f);
        testFloat("-123456789f", -123456789f);
    }

    /**
     * Float.MAX_VALUE
     */
    @Test
    void testFloatMaxValue() {
        testFloat(Float.toString(Float.MAX_VALUE) + "f", Float.MAX_VALUE);
    }

    /**
     * Float.MIN_VALUE
     */
    @Test
    void testMinFloatMinValue() {
        testFloat(Float.toString(Float.MIN_VALUE) + "f", Float.MIN_VALUE);
    }

    /**
     * Float.MAX_VALUE als hexadezimaler String.
     */
    @Test
    void testMaxHexFloat() {
        testFloat(Float.toHexString(Float.MAX_VALUE) + "f", Float.MAX_VALUE);
    }

    /**
     * Float.MIN_Value als hexadezimaler String.
     */
    @Test
    void testMinHexFloat() {
        testFloat(Float.toHexString(Float.MIN_VALUE) + "f", Float.MIN_VALUE);
    }

    /**
     * Verschiedene hexadezimal dargestellte Zahlen mit und ohne Komma.
     */
    @Test
    void testHexFloat() {
        testFloat("0x0p1f", 0f);
        testFloat("0x10p1f", 0x10p1f);
        testFloat("0xffp1f", 0xffp1f);
        testFloat("0xffp-1f", 0xffp-1f);
        testFloat("0xfff.123p1f", 0xfff.123p1f);
    }

    /**
     * Hilfmethode für die Tests. Konvertiert den Text in ein FloatingPointLiteral und prüft das darin erkannte
     * Float-Objekt.
     * 
     * @param text
     *            Text, der eingelesen werden soll.
     * @param value
     *            Erwartete Fließkommazahl.
     */
    void testFloat(final String text, final float value) {
        final FloatingPointLiteral literal = new FloatingPointLiteral(null, text);
        final Number literalValue = literal.getValue();
        assertNotNull(literalValue);
        assertEquals(Float.class, literalValue.getClass());
        assertEquals(value, literalValue.floatValue());
    }
}
