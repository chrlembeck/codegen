package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import lang.CodeGenParser.LiteralContext;

/**
 * Stellt das Literal {@code null} dar.
 *
 * @author Christoph Lembeck
 */
public class NullLiteral extends AbstractExpression<LiteralContext> implements Literal {

    /**
     * Erstellt ein neues NullLiteral mit dem Kontext des Parsers.
     * 
     * @param ctx
     *            Kontext des Parsers, so wie er ihn beim Lesen der Datei erzeugt hat.
     */
    public NullLiteral(final LiteralContext ctx) {
        super(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "NullLiteral";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<Object> evaluate(final Object model, final Environment environment) {
        return new ObjectWithType<Object>(null, Object.class);
    }
}