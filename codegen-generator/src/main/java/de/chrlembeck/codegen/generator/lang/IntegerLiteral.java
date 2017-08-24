package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import lang.CodeGenParser.LiteralContext;

/**
 * Stellt eine ganzzahlige Zahl vom Typ int oder long des Java-ähnlichen Teils der Template-Sprache dar.
 * 
 * @author Christoph Lembeck
 */
public class IntegerLiteral extends AbstractExpression<LiteralContext> implements Literal {

    /**
     * Die zu repräsentierende Zahl.
     */
    private Number value;

    /**
     * Erstellt ein neues IntegerLiteral aus den Daten des Parsers.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen des Dokumentes erzeugt hat.
     * @param literal
     *            Repräsentation der Zahl.
     */
    public IntegerLiteral(final LiteralContext ctx, String literal) {
        super(ctx);
        boolean isLong = false;
        if (literal.endsWith("l") || literal.endsWith("L")) {
            literal = literal.substring(0, literal.length() - 1);
            isLong = true;
        }
        if (literal.length() == 0) {
            throw new NumberFormatException("illegal integer literal (" + literal + ")");
        }
        if ("0".equals(literal)) {
            value = isLong ? (Number) Long.valueOf(0) : (Number) Integer.valueOf(0);
        } else if (literal.startsWith("0x") || literal.startsWith("0X")) {
            // hex
            literal = literal.substring(2);
            if (literal.startsWith("_") || literal.endsWith("_")) {
                throw new NumberFormatException("illegal integer literal (" + literal + ")");
            }
            value = isLong ? (Number) Long.parseUnsignedLong(literal.replaceAll("_", ""), 16)
                    : (Number) Integer.parseUnsignedInt(literal.replaceAll("_", ""), 16);
        } else if (literal.startsWith("0b") || literal.startsWith("0B")) {
            // binary
            value = parse(literal.substring(2), 2, isLong);
        } else if (literal.startsWith("0") || literal.startsWith("0")) {
            // octal
            value = parse(literal.substring(1), 8, isLong);
        } else {
            // decimal
            value = parse(literal, 10, isLong);
        }
    }

    /**
     * Überführt eine alz Zeichenkette vorliegende Zahl in die Zahl als int oder long unter Berücksichtigung des
     * verwendeten Zahlensystems.
     * 
     * @param literal
     *            Zeichenkette, die überführt werden soll.
     * @param radix
     *            Zu verwendender Radix der Operation
     * @param isLong
     *            true entspricht long, false entspricht int.
     * @return Zahl als Integer oder Long.
     * @see Long#valueOf(String, int)
     * @see Integer#valueOf(String, int)
     */
    private Number parse(final String literal, final int radix, final boolean isLong) {
        if (literal.startsWith("_") || literal.endsWith("_")) {
            throw new NumberFormatException("illegal integer literal (" + literal + ")");
        }
        return isLong ? (Number) Long.valueOf(literal.replaceAll("_", ""), radix)
                : (Number) Integer.valueOf(literal.replaceAll("_", ""), radix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * Gibt die enthaltene Zahl zurück.
     * 
     * @return Repräsentierte Zahl.
     */
    public Number getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        return value instanceof Integer ? new ObjectWithType<Integer>((Integer) value, int.class)
                : new ObjectWithType<Long>((Long) value, long.class);
    }
}