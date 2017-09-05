package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.GeneratorException;
import de.chrlembeck.codegen.generator.JavaUtil;
import de.chrlembeck.codegen.grammar.CodeGenParser.ExpressionConditionalContext;

/**
 * Implementiert den Conditional-Operator {@code cond ? if-exp : else-exp} für den java-ähnlichen Teil der
 * Template-Sprache.
 *
 * @author Christoph Lembeck
 */
public class ConditionalExpression extends AbstractExpression<ExpressionConditionalContext> {

    /**
     * Boolscher Ausdruck, der entscheidet, ob der if- oder der else-Teil ausgewertet werden soll.
     */
    private Expression condition;

    /**
     * Expression, die den if-Teil des Operators darstellt.
     */
    private Expression ifExpression;

    /**
     * Expression, die den else-Teil des Operators darstellt.
     */
    private Expression elseExpression;

    /**
     * Erstellt eine neue ConditionalExpression mit den übergebenen Werten.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser für dieses Element erzeugt hat.
     * @param condition
     *            Boolsche Bedingung, die ausgewertet wird, um zu entscheiden, ob der if- oder der else-Ausdruck bei der
     *            Auswertung zurückgegeben werden soll.
     * @param ifExpression
     *            Expression, die den if-Teil des Operators darstellt.
     * @param elseExpression
     *            Expression, die den else-Teil des Operators darstellt.
     */
    public ConditionalExpression(final ExpressionConditionalContext ctx, final Expression condition,
            final Expression ifExpression,
            final Expression elseExpression) {
        super(ctx);
        this.condition = condition;
        this.ifExpression = ifExpression;
        this.elseExpression = elseExpression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        final ObjectWithType<?> cond = condition.evaluate(model, environment);
        if (!JavaUtil.isBooleanType(cond.getType())) {
            throw new GeneratorException(
                    "Der conditional-Operator (b?e1:e1) benötigt ein boolean-Wert als ersten Operanden: " + cond,
                    this, environment);
        }
        // TODO Typen der beiden Ausdrücke auf Gleichheit prüfen und ggf. casten
        if (((Boolean) cond.getObject()).booleanValue()) {
            return ifExpression.evaluate(model, environment);
        } else {
            return elseExpression.evaluate(model, environment);
        }
    }
}