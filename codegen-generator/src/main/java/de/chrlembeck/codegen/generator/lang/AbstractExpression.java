package de.chrlembeck.codegen.generator.lang;

import org.antlr.v4.runtime.ParserRuleContext;

import de.chrlembeck.codegen.generator.Environment;

/**
 * Gemeinsamme Oberklasse für alle Ausdrücke vom Grammatik-Typ Expression.
 * 
 * @author Christoph Lembeck
 *
 * @param <T>
 *            Typ des ParserRuleContext-Objekts, den der Parser für das Expression-Element erzeugt.
 */
public abstract class AbstractExpression<T extends ParserRuleContext> extends AbstractTemplateMember<T>
        implements Expression {

    /**
     * Erstellt eine neue Expression und hinterlegt den Kontext des Parsers.
     * 
     * @param context
     *            Kontext, wie ihn der Parser beim Lesen der Template-Datei erzeugt hat.
     */
    public AbstractExpression(final T context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallSource findCallSource(final Object model, final Environment environment) {
        return new ObjectCallSource(evaluate(model, environment));
    }
}