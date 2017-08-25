package de.chrlembeck.codegen.generator;

import static de.chrlembeck.codegen.generator.SimpleGeneratorTest.checkOut1ForRoot;

import org.junit.jupiter.api.Test;

/**
 * Testfälle zur Erkennung von Literalen im Generator.
 *
 * @author Christoph Lembeck
 */
public class SimpleLiteralTest {

    /**
     * Testes die Erkennung einiger String-Literale.
     * 
     * @throws Exception
     *             Falls ein Laufzeitfehler auftritt.
     */
    @Test
    public void testStringLiteralOutput() throws Exception {
        checkOut1ForRoot("abc;\u00ab;\n\b\t\f\r'\"\\;z;\7;*", "«TEMPLATE root FOR java.lang.Object»" +
                "«OUTPUT \"out1\"»«\"abc\"»;«\"\\u00ab\"»;«\"\\n\\b\\t\\f\\r\\'\\\"\\\\\"»;«\"\\172\"»;«\"\\7\"»;«\"\\52\"»«ENDOUTPUT»"
                + "«ENDTEMPLATE»", new Object());
    }

    /**
     * Teste die Erkennung von Character-Literalen.
     * 
     * @throws Exception
     *             Falls ein Laufzeitfehler auftritt.
     */
    @Test
    public void testCharacterLiteralOutput() throws Exception {
        checkOut1ForRoot("m;\n;\u00ab", "«TEMPLATE root FOR java.lang.Object»" +
                "«OUTPUT \"out1\"»«'m'»;«'\n'»;«'\\u00ab'»«ENDOUTPUT»" +
                "«ENDTEMPLATE»", new Object());
    }
}