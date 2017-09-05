package de.chrlembeck.codegen.generator.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Einige Tests zu den String-Literalen.
 *
 * @author Christoph Lembeck
 */
public class StringLiteralTest {

    /**
     * Unicode-Escaped String literals.
     */
    @Test
    public void testUnicode() {
        assertEquals("\u00ab", new StringLiteral(null, "\"\\u00ab\"").getValue());
        assertEquals("\\u00ab", new StringLiteral(null, "\"\\\\u00ab\"").getValue());
        assertEquals("u00ab", new StringLiteral(null, "\"u00ab\"").getValue());
    }

    @Test
    public void testUnknownEscape() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StringLiteral(null, "\"\\j\""));
    }

    @Test
    public void testMissingQuotes() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StringLiteral(null, "x"));
    }

    @Test
    public void testOpenEscape() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StringLiteral(null, "\"x\\\""));
    }

    @Test
    public void testToString() {
        assertEquals("\"\u00ab\"", new StringLiteral(null, "\"\\u00ab\"").toString());
        assertEquals("\"abc\"", new StringLiteral(null, "\"abc\"").toString());
    }
}