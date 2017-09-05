package de.chrlembeck.codegen.generator.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CharacterLiteralTest {

    @Test
    public void testUnicode() {
        testCharacter("\\u0058", 'X');
        testCharacter("\\u0064", '\u0064');
    }

    @Test
    public void testSpecial() {
        testCharacter("\\n", '\n');
        testCharacter("\\t", '\t');
        testCharacter("\\b", '\b');
        testCharacter("\\f", '\f');
        testCharacter("\\r", '\r');
        testCharacter("\\\"", '\"');
        testCharacter("\\'", '\'');
        testCharacter("\\\\", '\\');
    }

    @Test
    public void testOctal() {
        testCharacter("\\040", '\040');
    }

    @Test
    public void testIllegalOctal() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> testCharacter("\\777", '\0'));
        Assertions.assertThrows(IllegalArgumentException.class, () -> testCharacter("\\888", '\0'));
    }

    @Test
    public void testIllegalUnicode() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> testCharacter("\\u12345", '\0'));
        Assertions.assertThrows(IllegalArgumentException.class, () -> testCharacter("\\u123", '\0'));
    }

    @Test
    public void testLiteralTooLong() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> testCharacter("ab", '\0'));
    }

    @Test
    public void testToString() {
        assertEquals("'x'", new CharacterLiteral(null, "x").toString());
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
    void testCharacter(final String text, final char expectedValue) {
        final CharacterLiteral literal = new CharacterLiteral(null, text);
        final char literalValue = literal.getCharacter();
        assertNotNull(literalValue);
        assertEquals(expectedValue, literalValue);
    }
}