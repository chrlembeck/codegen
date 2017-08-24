package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import lang.CodeGenParser.PrimaryVoidClassContext;

/**
 * Repräsentiert den Typen void.
 *
 * @author Christoph Lembeck
 */
public class VoidRef extends AbstractExpression<PrimaryVoidClassContext> implements Primary {

    /**
     * Erstellt eine neue VoidRef mit dem Context des Parsers.
     * 
     * @param ctx
     *            Kontext, wie der Parser ihn gelesen hat.
     */
    public VoidRef(final PrimaryVoidClassContext ctx) {
        super(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public ObjectWithType<Class> evaluate(final Object model, final Environment environment) {
        return new ObjectWithType<Class>(void.class, Class.class);
    }
}