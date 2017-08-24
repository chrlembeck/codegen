package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import lang.CodeGenParser.ExpressionSignContext;

/**
 * Repräsentiert die unären numerischen Vorzeichenoperationen Plus und Minus.
 * 
 * @author Christoph Lembeck
 */
public class SignExpression extends AbstractExpression<ExpressionSignContext> {

    /**
     * Mögliche Vorzeichen für die SignExpression.
     * 
     * @author Christoph Lembeck
     */
    public enum Operator {

        /**
         * Positives Vorzeichen Plus.
         */
        PLUS,

        /**
         * Negatives Vorzeichen Minus.
         */
        MINUS
    }

    /**
     * Gewünschter Operator für die Vorzeichen-Operation (Plus oder Minus).
     */
    private Operator operator;

    /**
     * Ausdruck des Operanden, auf den die Vorzeichen-Operation angewandt werden soll.
     */
    private Expression expression;

    /**
     * Erzeugt eine neue SignExpression mit den übergebenen Werten.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen der Template-Datei erzeugt hat.
     * @param expression
     *            Ausdruck für die Vorzeichen-Operation.
     * @param operator
     *            Gewünschter Operator für die Funktion (Plus oder Minus).
     */
    public SignExpression(final ExpressionSignContext ctx, final Operator operator, final Expression expression) {
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
        final Number number = (Number) exp.getObject();
        Number result;
        if (operator == Operator.PLUS) {
            result = number;
        } else if (number instanceof Double) {
            result = Double.valueOf(-number.doubleValue());
        } else if (number instanceof Float) {
            result = Float.valueOf(-number.floatValue());
        } else if (number instanceof Long) {
            result = Long.valueOf(-number.longValue());
        } else if (number instanceof Integer) {
            result = Integer.valueOf(-number.intValue());
        } else {
            throw new RuntimeException("unkonwn type " + number.getClass().getName());
        }
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final ObjectWithType<?> owt = new ObjectWithType(result, type);
        return owt;
    }
}