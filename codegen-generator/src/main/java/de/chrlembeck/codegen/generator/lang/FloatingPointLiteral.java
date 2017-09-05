package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.grammar.CodeGenParser.LiteralContext;

/**
 * Stellt eine Fließkommazahl vom Typ float oder double des Java-ähnlichen Teils der Template-Sprache dar.
 * 
 * @author Christoph Lembeck
 */
public class FloatingPointLiteral extends AbstractExpression<LiteralContext> implements Literal {

    /**
     * Die zu repräsentierende Zahl.
     */
    private Number value;

    /**
     * Erstellt ein neues IntegerLiteral aus den Daten des Parsers.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen des Dokumentes erzeugt hat.
     * @param text
     *            Repräsentation der Zahl.
     */
    public FloatingPointLiteral(final LiteralContext ctx, final String text) {
        super(ctx);
        if (text.endsWith("f") || text.endsWith("F")) {
            value = Float.valueOf(text);
        } else {
            value = Double.valueOf(text);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        return value instanceof Float ? new ObjectWithType<Float>((Float) value, float.class)
                : new ObjectWithType<Double>((Double) value, double.class);
    }

    /**
     * Gibt die enthaltene Zahl zurück.
     * 
     * @return Repräsentierte Zahl.
     */
    public Number getValue() {
        return value;
    }
}