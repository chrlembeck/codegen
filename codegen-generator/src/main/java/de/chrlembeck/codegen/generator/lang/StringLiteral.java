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
        if (!input.startsWith("\"") || !input.endsWith("\"")) {
            throw new IllegalStateException("A string literal always has to begin and end withl '\"'.");
        } else {
            input = input.substring(1, input.length() - 1);
        }

        final StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < input.length(); i++) {
            final char ch = input.charAt(i);
            if (escaped) {
                // escaped
                if (ch >= '0' && ch <= '9') {
                    // octal escape
                    String num = Character.toString(ch);
                    if (i + 1 < input.length() && input.charAt(i + 1) >= '0' && input.charAt(i + 1) <= '9') {
                        num += input.charAt(i + 1);
                        i++;
                    }
                    if (num.charAt(0) <= '3' && i + 1 < input.length() && input.charAt(i + 1) >= '0'
                            && input.charAt(i + 1) <= '9') {
                        num += input.charAt(i + 1);
                        i++;
                    }
                    sb.append((char) Integer.parseInt(num, 8));
                } else {

                    switch (ch) {
                        case 'b':
                            sb.append('\b');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case '\'':
                            sb.append('\'');
                            break;
                        case '\"':
                            sb.append('\"');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case 'u':
                            final String next4 = input.substring(i + 1, i + 5);
                            sb.append((char) Integer.parseInt(next4, 16));
                            i += 4;
                            break;
                        default:
                            throw new IllegalArgumentException("unknonw escape sequence in string (" + input + ")");
                    }
                }
                escaped = false;
            } else {
                // not escaped
                if (ch == '\\') {
                    escaped = true;
                } else {
                    sb.append(ch);
                }
            }
        }
        if (escaped) {
            throw new IllegalArgumentException("string ends with open escape (" + input + ")");
        }
        value = sb.toString();
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