package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import lang.CodeGenParser.LiteralContext;

/**
 * Stellt eine Fließkommazahl vom Typ float oder double des Java-ähnlichen Teils der Template-Sprache dar.
 * 
 * @author Christoph Lembeck
 */
public class StringLiteral extends AbstractExpression<LiteralContext> implements Literal {

    /**
     * Die zu repräsentierende Zeichenkette ohne die umschließenden Hochkommata.
     */
    private String value;

    /**
     * Erstellt ein neues StringLiteral aus den Daten des Parsers.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen des Dokumentes erzeugt hat.
     * @param input
     *            Repräsentation der Zeichenkette inclusive der ihn umschließenden Hochkommata.
     */
    public StringLiteral(final LiteralContext ctx, String input) {
        super(ctx);
        if (input.startsWith("\"") && input.endsWith("\"")) {
            input = input.substring(1, input.length() - 1);
        } else {
            throw new IllegalStateException("A string literal always has to begin and end withl '\"'.");
        }

        final StringBuilder builder = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < input.length(); i++) {
            final char character = input.charAt(i);
            if (escaped) {
                // escaped
                if (character >= '0' && character <= '9') {
                    // octal escape
                    String num = Character.toString(character);
                    if (i + 1 < input.length() && input.charAt(i + 1) >= '0' && input.charAt(i + 1) <= '9') {
                        num += input.charAt(i + 1);
                        i++;
                    }
                    if (num.charAt(0) <= '3' && i + 1 < input.length() && input.charAt(i + 1) >= '0'
                            && input.charAt(i + 1) <= '9') {
                        num += input.charAt(i + 1);
                        i++;
                    }
                    builder.append((char) Integer.parseInt(num, 8));
                } else {

                    switch (character) {
                        case 'b':
                            builder.append('\b');
                            break;
                        case 't':
                            builder.append('\t');
                            break;
                        case 'n':
                            builder.append('\n');
                            break;
                        case 'f':
                            builder.append('\f');
                            break;
                        case 'r':
                            builder.append('\r');
                            break;
                        case '\'':
                            builder.append('\'');
                            break;
                        case '\"':
                            builder.append('\"');
                            break;
                        case '\\':
                            builder.append('\\');
                            break;
                        case 'u':
                            final String next4 = input.substring(i + 1, i + 5);
                            builder.append((char) Integer.parseInt(next4, 16));
                            i += 4;
                            break;
                        default:
                            throw new IllegalArgumentException("unknonw escape sequence in string (" + input + ")");
                    }
                }
                escaped = false;
            } else {
                // not escaped
                if (character == '\\') {
                    escaped = true;
                } else {
                    builder.append(character);
                }
            }
        }
        if (escaped) {
            throw new IllegalArgumentException("string ends with open escape (" + input + ")");
        }
        value = builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

    /**
     * Gibt die zu repräsentierende Zeichenkette ohne die umschließenden Hochkommata zurück.
     * 
     * @return Repräsentierte Zeichenkette.
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<String> evaluate(final Object model, final Environment environment) {
        return new ObjectWithType<String>(value, String.class);
    }
}