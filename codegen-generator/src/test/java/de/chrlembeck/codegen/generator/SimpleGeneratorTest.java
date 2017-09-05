package de.chrlembeck.codegen.generator;

import java.awt.Point;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.chrlembeck.codegen.generator.lang.TemplateFile;
import de.chrlembeck.codegen.generator.output.BasicOutputPreferences;
import de.chrlembeck.codegen.generator.output.BufferedOutput;
import de.chrlembeck.codegen.generator.visitor.TemplateFileVisitor;
import lang.CodeGenLexer;
import lang.CodeGenParser;

/**
 * Sammlung von Tests zu einfachen Templates ohne Imports und Aufrufe von anderen Templates.
 *
 * @author Christoph Lembeck
 */
public class SimpleGeneratorTest {

    /**
     * Statischer Text.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testStaticOutput() throws Exception {
        checkOut1ForRoot("abc", "«TEMPLATE root FOR java.lang.Object»" +
                "«OUTPUT \"out1\"»abc«ENDOUTPUT»" +
                "«ENDTEMPLATE»", new Object());
    }

    /**
     * Inhalt eines String-Modells.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testDynamicExpression() throws Exception {
        checkOut1ForRoot("hello", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«this»«ENDOUTPUT»" +
                "«ENDTEMPLATE»", "hello");
    }

    /**
     * Instanz-Methodenaufruf.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testMethodCall() throws Exception {
        checkOut1ForRoot("5", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«this.length()»«ENDOUTPUT»" +
                "«ENDTEMPLATE»", "hello");
    }

    /**
     * Statischer Methodenaufruf.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testStaticMethod() throws Exception {
        checkOut1ForRoot(LocalDate.now().toString(), "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«java.time.LocalDate.now()»«ENDOUTPUT»" +
                "«ENDTEMPLATE»", "");
    }

    /**
     * FOREACH mit Trennzeichen.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testForEachWithSeparator() throws Exception {
        checkOut1ForRoot("a,b,c", "«TEMPLATE root FOR java.util.List»" +
                "«OUTPUT \"out1\"»«FOREACH i FROM this SEPARATOR \",\"»«i»«ENDFOREACH»«ENDOUTPUT»" +
                "«ENDTEMPLATE»", Arrays.asList("a", "b", "c"));
    }

    /**
     * FOREACH ohne Trennzeichen.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testForEach() throws Exception {
        checkOut1ForRoot("abc", "«TEMPLATE root FOR java.util.List»" +
                "«OUTPUT \"out1\"»«FOREACH i FROM this»«i»«ENDFOREACH»«ENDOUTPUT»" +
                "«ENDTEMPLATE»", Arrays.asList("a", "b", "c"));
    }

    /**
     * FOREACH mit Zählvariable.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testForEachWithCounter() throws Exception {
        checkOut1ForRoot("0a1b2c", "«TEMPLATE root FOR java.util.List»" +
                "«OUTPUT \"out1\"»«FOREACH i FROM this COUNTER c»«c.getIndex()»«i»«ENDFOREACH»«ENDOUTPUT»" +
                "«ENDTEMPLATE»", Arrays.asList("a", "b", "c"));
    }

    /**
     * FOREACH mit Trennzeichen und Zähler.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testForEachWithCounterAndSeparator() throws Exception {
        checkOut1ForRoot("0a, 1b, 2c", "«TEMPLATE root FOR java.util.List»" +
                "«OUTPUT \"out1\"»«FOREACH i FROM this COUNTER c SEPARATOR \", \"»«c.getIndex()»«i»" +
                "«ENDFOREACH»«ENDOUTPUT»«ENDTEMPLATE»", Arrays.asList("a", "b", "c"));
    }

    /**
     * Einige Primary-Literale.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionPrimary() throws Exception {
        checkOut1ForRoot("42;xyz;3.25;1.5;17;true;null;void;3", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«42»;«\"xyz\"»;«3.25f»;«1.5d»;«17l»;«true»;«null»;«void.class»;«(1+2)»«ENDOUTPUT»«ENDTEMPLATE»",
                "");
    }

    /**
     * Einfache Tests für Template-Definitionen mit Kommentaren.
     * 
     * @throws Exception
     */
    @Test
    public void testComment() throws Exception {
        checkOut1ForRoot("abc", "«COMMENT»hfhdsj«ENDCOMMENT»"
                + "«TEMPLATE root FOR java.lang.String»" + "«COMMENT»hfhdsj«ENDCOMMENT»" +
                "«OUTPUT \"out1\"»«this»«ENDOUTPUT»«ENDTEMPLATE»",
                "abc");
    }

    /**
     * Auswertung eines Instanz-Attributs.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionAttribute() throws Exception {
        checkOut1ForRoot("17", "«TEMPLATE root FOR java.awt.Point»" +
                "«OUTPUT \"out1\"»«this.x»«ENDOUTPUT»«ENDTEMPLATE»", new Point(17, 42));
    }

    /**
     * Zugriff auf ein int-Array.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionArrayAccess() throws Exception {
        checkOut1ForRoot("33", "«TEMPLATE root FOR [I»" +
                "«OUTPUT \"out1\"»«this[2]»«ENDOUTPUT»«ENDTEMPLATE»", new int[] { 11, 22, 33 });
    }

    /**
     * Methodenaufruf auf dem Modell.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionMethodCall() throws Exception {
        checkOut1ForRoot("5", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«this.length()»«ENDOUTPUT»«ENDTEMPLATE»", "Hello");
    }

    /**
     * Instanzmethodenaufruf mit einem Argument.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionMethodCallOneArgument() throws Exception {
        checkOut1ForRoot("llo", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«this.substring(2)»«ENDOUTPUT»«ENDTEMPLATE»", "Hello");
    }

    /**
     * Instanzmethodenaufruf mit zwei Argumenten.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionMethodCallTwoArguments() throws Exception {
        checkOut1ForRoot("cd", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«this.substring(2,4)»«ENDOUTPUT»«ENDTEMPLATE»", "abcde");
    }

    /**
     * Casts primitiver Datentypen.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionCast() throws Exception {
        checkOut1ForRoot("4;3;abc", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«(int)4l»;«(int)3.4f»;«(java.io.Serializable)\"abc\"»«ENDOUTPUT»«ENDTEMPLATE»", "");
    }

    /**
     * Zahlen mit Vorzeichen.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionSign() throws Exception {
        checkOut1ForRoot("-5;9;-2.5;9.25", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«-5»;«+9»;«-2.5f»;«+9.25d»«ENDOUTPUT»«ENDTEMPLATE»", "");
    }

    /**
     * Boolsche und bitweise Negation.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionNeg() throws Exception {
        checkOut1ForRoot("false;true;-6", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«!true»;«!java.lang.Boolean.FALSE»;«~5»«ENDOUTPUT»«ENDTEMPLATE»", "");
    }

    /**
     * Multiplikation, Division und Modulo.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionMultDivMod() throws Exception {
        checkOut1ForRoot("6;9;1;6.75", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«2*3»;«19/2»;«19%2»;«3f*2.25d»«ENDOUTPUT»«ENDTEMPLATE»", "");
    }

    /**
     * Plus und Minus.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionPlusMinus() throws Exception {
        checkOut1ForRoot("5;3;19;3.25", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«2+3»;«8-5»;«20-1»;«2l+1.25f»«ENDOUTPUT»«ENDTEMPLATE»", "");
    }

    /**
     * Shift-Operationen.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionShift() throws Exception {
        checkOut1ForRoot("36;4;6", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«9<<2»;«9>>1»;«13l>>>1»«ENDOUTPUT»«ENDTEMPLATE»", "");
    }

    /**
     * Vergleichoperationen
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionCompare() throws Exception {
        checkOut1ForRoot("true;true;true;false;false", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«9>8»;«1f<2l»;«8>=8d»;«8>=9»;«8<=7»«ENDOUTPUT»«ENDTEMPLATE»", "");
    }

    /**
     * Dei Instanceof-Operation.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionInstanceof() throws Exception {
        checkOut1ForRoot("true;true;false", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«\"a\" instanceof java.lang.String»;«java.lang.Integer.valueOf(42) instanceof java.lang.Number»;«\"xyz\" instanceof java.lang.Thread»«ENDOUTPUT»«ENDTEMPLATE»",
                "");
    }

    /**
     * Gleichheit und Ungleichheit.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionEquals() throws Exception {
        checkOut1ForRoot("true;true;true;false;false;false", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«8==8»;«true==true»;«false==false»;«8==9»;«8!=8»;«\"a\"==\"a\"»«ENDOUTPUT»«ENDTEMPLATE»",
                "");
    }

    /**
     * Logisches Und.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionAnd() throws Exception {
        checkOut1ForRoot("false;false;false;true;false;true;4", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«true&false»;«false&true»;«false&false»;«true&true»;«1>2&2>1»;«1<2&2>1»;«12&5»«ENDOUTPUT»«ENDTEMPLATE»",
                "");
    }

    /**
     * Logisches Xor.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionXor() throws Exception {
        checkOut1ForRoot("true;true;false;false;true;9", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«true^false»;«false^true»;«false^false»;«true^true»;«1>2^2>1»;«12^5»«ENDOUTPUT»«ENDTEMPLATE»",
                "");
    }

    /**
     * Logisches Oder.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionOr() throws Exception {
        checkOut1ForRoot("true;true;false;true;true;13", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«true|false»;«false|true»;«false|false»;«true|true»;«1>2|2>1»;«12|5»«ENDOUTPUT»«ENDTEMPLATE»",
                "");
    }

    /**
     * Bedingte Und-Operation.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionConditionalAnd() throws Exception {
        checkOut1ForRoot("false;false;false;true;false;true", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«true&&false»;«false&&true»;«false&&false»;«true&&true»;«1>2&&2>1»;«1<2&&2>1»«ENDOUTPUT»«ENDTEMPLATE»",
                "");
    }

    /**
     * Bedingtes Oder.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionConditionalOr() throws Exception {
        checkOut1ForRoot("true;true;false;true;true", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«true||false»;«false||true»;«false||false»;«true||true»;«1>2||2>1»«ENDOUTPUT»«ENDTEMPLATE»",
                "");
    }

    /**
     * Der Conditional-Operator.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testExpressionConditional() throws Exception {
        checkOut1ForRoot("a;42", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«true?\"a\":\"b\"»;«false?17:42»«ENDOUTPUT»«ENDTEMPLATE»", "");
    }

    /**
     * Typen von primitiven Typen.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testPrimitiveClass() throws Exception {
        checkOut1ForRoot("int;class [I;class [[I;[[I", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«int.class»;«int[].class»;«int[][].class»;«int[][].class.getName()»«ENDOUTPUT»«ENDTEMPLATE»",
                "");
    }

    /**
     * Tests für die Erkennung der primitiven Datentypen.
     * 
     * @throws Exception
     */
    @Test
    public void testPrimitiveTypes() throws Exception {
        checkOut1ForRoot("boolean;byte;short;char;int;float;long;double", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«boolean.class»;«byte.class»;«short.class»;«char.class»;«int.class»;«float.class»;«long.class»;«double.class»«ENDOUTPUT»«ENDTEMPLATE»",
                "");
    }

    /**
     * Typen von Klassen.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testClassRef() throws Exception {
        checkOut1ForRoot("java.lang.Object:[[Ljava.lang.Object;", "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«java.lang.Object.class.getName()»:«java.lang.Object[][].class.getName()»«ENDOUTPUT»«ENDTEMPLATE»",
                "");
    }

    /**
     * Statische Attribute.
     * 
     * @throws Exception
     *             Bei einem Laufzeitproblem.
     */
    @Test
    public void testStaticAttribute() throws Exception {
        checkOut1ForRoot(System.out.toString(), "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«java.lang.System.out»«ENDOUTPUT»" +
                "«ENDTEMPLATE»", "");
    }

    /**
     * Test für ein Import-Statement.
     * 
     * @throws Exception
     */
    @Test
    public void testImport() throws Exception {
        checkOut1ForRoot("abc", "«IMPORT a AS import.codegen»" +
                "«TEMPLATE root FOR java.lang.String»" +
                "«OUTPUT \"out1\"»«this»«ENDOUTPUT»" +
                "«ENDTEMPLATE»", "abc");
    }

    /**
     * Prüft die Ausgabe eines Generatorlaufs. Das verwendete Template muss die Ausgabe dabei in den Channel
     * {@code out1} schreiben.
     * 
     * @param expected
     *            Erwartete Ausgabe des Generatorlaufs.
     * @param input
     *            Textuelle Beschreibung des Templates.
     * @param model
     *            Zur Generierung zu verwendendes Modell.
     * @throws IOException
     *             Bei einem Laufzeitproblem.
     */
    public static void checkOut1ForRoot(final String expected, final String input, final Object model)
            throws IOException {
        final BufferedOutput out = createOutput(input, "root", model);
        final String content = out.getContent("out1");
        Assertions.assertEquals(expected, content);
    }

    /**
     * Startet den Generator mit der textuellen Template-Datei und dem Modell und führt das gewünschte Template aus.
     * 
     * @param input
     *            Inhalt der Template-Datei-Beschreibung.
     * @param templateName
     *            Name des auszuführenden Templates.
     * @param model
     *            Modell für die Ausführung
     * @return BufferedOutput-Objekt, welches für die Ausgabe verwendet wurde.
     * @throws IOException
     *             Bei einem Problem bei der Ausführung.
     */
    public static BufferedOutput createOutput(final String input, final String templateName, final Object model)
            throws IOException {
        final BufferedOutput out = new BufferedOutput();
        final TemplateFile templateFile = parse(input);
        final Generator generator = new Generator(new SimpleTemplateResolver(templateFile), out,
                new BasicOutputPreferences());
        generator.generate(templateFile.getResourceIdentifier(), templateName, model);
        return out;
    }

    /**
     * Konvertiert den Inhalt einer Template-Datei in ein TemplateFile-Objekt.
     * 
     * @param input
     *            Textueller Inhalt einer Template-Datei.
     * @return Übersetztes TemplateFile
     */
    public static TemplateFile parse(final String input) {
        final CodeGenLexer lexer = new CodeGenLexer(CharStreams.fromString(input));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final CodeGenParser parser = new CodeGenParser(tokens);
        try {
            return parser.templateFile().accept(new TemplateFileVisitor(new URI(".")));
        } catch (RecognitionException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}