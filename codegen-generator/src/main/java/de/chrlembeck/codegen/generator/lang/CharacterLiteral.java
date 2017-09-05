package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.grammar.CodeGenParser.LiteralContext;

/**
 * Stellt ein einfaches Zeichen vom Typ char des Java-ähnlichen Teils der Template-Sprache dar.
 * 
 * @author Christoph Lembeck
 */
public class CharacterLiteral extends AbstractExpression<LiteralContext> implements Literal {

    /**
     * Zeichen, dass durch dieses Element dargestellt wird.
     */
    private char character;

    /**
     * Erstellt einen neuen Repräsentanten für das Zeichen anhand der Werte des Parsers.
     * 
     * @param ctx
     *            Kontext, wie der Parser ihn ermittelt hat.
     * @param literal
     *            Literal, das der Parser als Character erkannt hat. Bei dem Literal kann es sich auch um ein
     *            Unicode-Escaped Character oder eine Oktalzahl handeln.
     */
    public CharacterLiteral(final LiteralContext ctx, final String literal) {
        super(ctx);
        if (literal.length() == 0 || literal.length() > 6) {
            throw new IllegalArgumentException("illegal character (" + literal + ")");
        }

        if (literal.startsWith("\\")) {
            if (literal.charAt(1) >= '0' && literal.charAt(1) <= '9') {
                final int code = Integer.parseInt(literal.substring(1), 8);
                if (code > 255) {
                    throw new IllegalArgumentException("illegal octal character (" + literal + ")");
                }
                character = (char) code;
            } else if (literal.charAt(1) == 'u') {
                if (literal.length() != 6) {
                    throw new IllegalArgumentException("illegal unicode character (" + literal + ")");
                }
                character = (char) Integer.parseInt(literal.substring(2, 6), 16);
            } else if (literal.length() == 2 && literal.charAt(1) == 'b') {
                character = '\b';
            } else if (literal.length() == 2 && literal.charAt(1) == 't') {
                character = '\t';
            } else if (literal.length() == 2 && literal.charAt(1) == 'n') {
                character = '\n';
            } else if (literal.length() == 2 && literal.charAt(1) == 'f') {
                character = '\f';
            } else if (literal.length() == 2 && literal.charAt(1) == 'r') {
                character = '\r';
            } else if (literal.length() == 2 && literal.charAt(1) == '\"') {
                character = '\"';
            } else if (literal.length() == 2 && literal.charAt(1) == '\'') {
                character = '\'';
            } else if (literal.length() == 2 && literal.charAt(1) == '\\') {
                character = '\\';
            } else {
                throw new IllegalArgumentException("illegal character (" + literal + ")");
            }
        } else {
            if (literal.length() != 1) {
                throw new IllegalArgumentException("illegal character (" + literal + ")");
            }
            character = literal.charAt(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "'" + character + "'";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<Character> evaluate(final Object model, final Environment environment) {
        return new ObjectWithType<Character>(Character.valueOf(character), char.class);
    }
}