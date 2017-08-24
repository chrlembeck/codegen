package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import lang.CodeGenParser.PrimaryThisReferenceContext;

/**
 * Stellt einen Verweis auf die this-Referenz dar.
 *
 * @author Christoph Lembeck
 */
public class ThisReference extends AbstractExpression<PrimaryThisReferenceContext> implements Primary {

    /**
     * Erstellt eine neue This-Referenz nach den Vorgaben des Parsers.
     * 
     * @param ctx
     *            Kontext des Parsers, wie er ihn für dieses Element erzeugt hat.
     */
    public ThisReference(final PrimaryThisReferenceContext ctx) {
        super(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "this";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        return new ObjectWithType(model, model.getClass());
    }
}