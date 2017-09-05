package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.GeneratorException;
import de.chrlembeck.codegen.generator.JavaUtil;
import de.chrlembeck.codegen.grammar.CodeGenParser.ExpressionShiftContext;

/**
 * Repräsentiert die logischen Bitverschiebungen numerischer Werte nach links und nach rechts mit und ohne Vorzeichen.
 * 
 * @author Christoph Lembeck
 */
public class ShiftExpression extends AbstractExpression<ExpressionShiftContext> {

    /**
     * Mögliche Operatoren für die ShiftExpression.
     * 
     * @author Christoph Lembeck
     */
    public enum Operator {

        /**
         * Bitweise Linksverschiebung.
         */
        LEFT_SHIFT,

        /**
         * Vorzeichenbehaftete Rechtsverschiebung.
         */
        SIGNED_RIGHT_SHIFT,

        /**
         * Vorzeichenlose Rechtsverschiebung.
         */
        UNSIGNED_RIGHT_SHIFT
    }

    /**
     * Ausdruck des linken Operanden.
     */
    private Expression left;

    /**
     * Ausdruck des rechten Operanden.
     */
    private Expression right;

    /**
     * Legt den Operator für die Ausführung fest (left-shift, unsigned-right-shift oder signed-right-shift).
     */
    private Operator operator;

    /**
     * Erzeugt eine neue ShiftExpression mit den übergebenen Werten.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen der Template-Datei erzeugt hat.
     * @param left
     *            Ausdruck für den linken Operanden.
     * @param right
     *            Ausdruck für den rechten Operanden.
     * @param operator
     *            Gewünschter Operator für den Ausdruck.
     */
    public ShiftExpression(final ExpressionShiftContext ctx, final Expression left, final Expression right,
            final Operator operator) {
        super(ctx);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        final ObjectWithType<?> leftObj = JavaUtil.unaryNumericPromotion(left.evaluate(model, environment));
        if (!JavaUtil.isIntegerOrLongType(leftObj.getType())) {
            throw new GeneratorException("Der linke Operand der Shift-Operation ist nicht vom Typ int oder long.",
                    this, environment);
        }
        final ObjectWithType<?> rightObj = JavaUtil.unaryNumericPromotion(right.evaluate(model, environment));
        if (!JavaUtil.isIntegerOrLongType(rightObj.getType())) {
            throw new GeneratorException("Der rechte Operand der Shift-Operation ist nicht vom Typ int oder long.",
                    this, environment);
        }
        int shiftDistance;
        if (int.class.isAssignableFrom(leftObj.getType())) {
            shiftDistance = ((Number) rightObj.getObject()).intValue() & 0x1f;
            final int leftValue = ((Number) leftObj.getObject()).intValue();
            switch (operator) {
                case LEFT_SHIFT:
                    return new ObjectWithType<Integer>(leftValue << shiftDistance, int.class);
                case SIGNED_RIGHT_SHIFT:
                    return new ObjectWithType<Integer>(leftValue >> shiftDistance, int.class);
                case UNSIGNED_RIGHT_SHIFT:
                    return new ObjectWithType<Integer>(leftValue >>> shiftDistance, int.class);
                default:
                    throw new IllegalStateException("unexpected operator: " + operator);
            }
        } else if (long.class.isAssignableFrom(leftObj.getType())) {
            shiftDistance = ((Number) rightObj.getObject()).intValue() & 0x3f;
            final long leftValue = ((Number) leftObj.getObject()).longValue();
            switch (operator) {
                case LEFT_SHIFT:
                    return new ObjectWithType<Long>(leftValue << shiftDistance, long.class);
                case SIGNED_RIGHT_SHIFT:
                    return new ObjectWithType<Long>(leftValue >> shiftDistance, long.class);
                case UNSIGNED_RIGHT_SHIFT:
                    return new ObjectWithType<Long>(leftValue >>> shiftDistance, long.class);
                default:
                    throw new IllegalStateException("unexpected operator: " + operator);
            }
        } else {
            throw new IllegalStateException("unexpected type: " + leftObj.getType());
        }
    }
}