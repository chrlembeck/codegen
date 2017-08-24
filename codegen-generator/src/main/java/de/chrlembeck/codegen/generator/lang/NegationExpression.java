package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.GeneratorException;
import lang.CodeGenParser.ExpressionNegContext;

/**
 * Stellt die beiden Operationen {@code ~}, also die numerische bitweise negation, so wie {@code !}, die boolsche
 * Negation dar.
 *
 * @author Christoph Lembeck
 */
public class NegationExpression extends AbstractExpression<ExpressionNegContext> {

    /**
     * Mögliche Operatoren für die NegationExpression.
     *
     * @author Christoph Lembeck
     */
    public enum Operator {

        /**
         * Boolsche Negation.
         */
        BOOLEAN_NEGATION,

        /**
         * Numerische bitweise Negation.
         */
        NUMERIC_NEGATION;
    }

    /**
     * Zu negierender Ausdruck.
     */
    private Expression expression;

    /**
     * Operator, der auf die expression angewandt werden soll.
     */
    private Operator operator;

    /**
     * Erstellt eine neue NegationExpression mit den übergebenen Werten.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser für dieses Element erzeugt hat.
     * @param operator
     *            Operator, der auf den Ausdruck angewandt werden soll.
     * @param expression
     *            Ausdruck, der negiert werden soll.
     */
    public NegationExpression(final ExpressionNegContext ctx, final Operator operator, final Expression expression) {
        super(ctx);
        this.operator = operator;
        this.expression = expression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        final ObjectWithType<?> exp = expression.evaluate(model, environment);
        final Class<?> type = exp.getType();
        if (operator == Operator.BOOLEAN_NEGATION) {
            if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
                @SuppressWarnings({ "rawtypes", "unchecked" })
                final ObjectWithType<?> result = new ObjectWithType(!(Boolean) exp.getObject(), type);
                return result;
            } else {
                throw new GeneratorException(
                        "Die boolsche Negation kann nur auf booleans angewandt werden. (" + type.getName() + ")",
                        this, environment);
            }
        } else {
            final Number number = (Number) exp.getObject();
            Number negatedNumber;
            if (number instanceof Long) {
                negatedNumber = Long.valueOf(~number.longValue());
            } else if (number instanceof Integer) {
                negatedNumber = Integer.valueOf(~number.intValue());
            } else {
                throw new GeneratorException(
                        "Die numerische Negation (~) kann nur auf Werte des Typs int oder long angewandt werden. ("
                                + number.getClass().getName() + ")",
                        this, environment);
            }
            @SuppressWarnings({ "rawtypes", "unchecked" })
            final ObjectWithType<?> result = new ObjectWithType(negatedNumber, type);
            return result;
        }
    }
}