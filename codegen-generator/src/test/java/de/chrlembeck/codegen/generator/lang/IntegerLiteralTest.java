package de.chrlembeck.codegen.generator.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Sammlung von Tests zu den ganzzahligen Integer-Zahlen
 *
 * @author Christoph Lembeck
 */
public class IntegerLiteralTest {

    /**
     * Einfache Zahlen ohne Vorzeichen.
     */
    @Test
    void testInteger() {
        testInt("0", 0);
        testInt("5", 5);
        testInt("10", 10);
    }

    /**
     * Negative Zahlen.
     */
    @Test
    void testNegativeInteger() {
        testInt("-1", -1);
        testInt("-123456789", -123456789);
    }

    /**
     * Integer.MAX_VALUE.
     */
    @Test
    void testMaxInteger() {
        testInt(Integer.toString(Integer.MAX_VALUE), Integer.MAX_VALUE);
    }

    /**
     * Integer.MIN_VALUE.
     */
    @Test
    void testMinInteger() {
        testInt(Integer.toString(Integer.MIN_VALUE), Integer.MIN_VALUE);
    }

    /**
     * Integer.MAX_VALUE als hexadezimaler String.
     */
    @Test
    void testMaxHexInteger() {
        testInt("0x7fffffff", Integer.MAX_VALUE);
    }

    /**
     * Integer.MIN_VALUE als Hexadezimaler Wert.
     */
    @Test
    void testMinHexInteger() {
        testInt("0x80000000", Integer.MIN_VALUE);
    }

    /**
     * Einige hexadezimal dargestellte Zahlen.
     */
    @Test
    void testHexInteger() {
        testInt("0x0", 0);
        testInt("0x10", 16);
        testInt("0xff", 255);
        testInt("0xff", 255);
        testInt("0xfff", 4095);
    }

    /**
     * Hilfklasse zum Testen des IntegerLiterals. Erstellt aus dem text ein IntegerLiteral und prüft das darin
     * enthaltene Integer-Objekt.
     * 
     * @param text
     *            Zu lesender Text.
     * @param expectedValue
     *            Erwartete Zahl.
     */
    void testInt(final String text, final int expectedValue) {
        final IntegerLiteral literal = new IntegerLiteral(null, text);
        final Number literalValue = literal.getValue();
        assertNotNull(literalValue);
        assertEquals(Integer.class, literalValue.getClass());
        assertEquals(expectedValue, literalValue.intValue());
    }
}
