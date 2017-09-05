package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.grammar.CodeGenParser.PrimarySuperReferenceContext;

/**
 * Stellt einen Verweis auf die super-Referenz dar.
 *
 * @author Christoph Lembeck
 */
public class SuperReference extends AbstractExpression<PrimarySuperReferenceContext> implements Primary {

    /**
     * Erstellt eine neue Super-Referenz nach den Vorgaben des Parsers.
     * 
     * @param ctx
     *            Kontext des Parsers, wie er ihn für dieses Element erzeugt hat.
     */
    public SuperReference(final PrimarySuperReferenceContext ctx) {
        super(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "super";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        throw new RuntimeException("not yet implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallSource findCallSource(final Object model, final Environment environment) {
        throw new RuntimeException("not yet implemented");
    }
}