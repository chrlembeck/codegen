package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.JavaUtil;
import lang.CodeGenParser.ExpressionCompareContext;

/**
 * Symbolisiert die Vergleichsoperationen auf numerischen Datentypen.
 *
 * @author Christoph Lembeck
 */
public class CompareExpression extends AbstractExpression<ExpressionCompareContext> {

    /**
     * Operatoren, die innerhalb der CompareExpression verwendet werden können.
     *
     * @author Christoph Lembeck
     */
    public enum Operator implements PrimitiveOperations {

        /**
         * Kleiner-Operation (&lt;).
         */
        LESS_THAN,

        /**
         * Kleiner Gleich-Operation (&lt;=).
         */
        LESS_OR_EQUAL,

        /**
         * Größer Gleich-Operation (&gt;=).
         */
        GREATER_OR_EQUAL,

        /**
         * Größer-Operation (&gt;).
         */
        GREATER_THAN;

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Boolean> apply(final int leftOperand, final int rightOperand) {
            switch (this) {
                case LESS_THAN:
                    return new ObjectWithType<Boolean>(leftOperand < rightOperand, boolean.class);
                case LESS_OR_EQUAL:
                    return new ObjectWithType<Boolean>(leftOperand <= rightOperand, boolean.class);
                case GREATER_OR_EQUAL:
                    return new ObjectWithType<Boolean>(leftOperand >= rightOperand, boolean.class);
                case GREATER_THAN:
                    return new ObjectWithType<Boolean>(leftOperand > rightOperand, boolean.class);
                default:
                    throw new IllegalStateException("unexpected operator: " + this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Boolean> apply(final float leftOperand, final float rightOperand) {
            switch (this) {
                case LESS_THAN:
                    return new ObjectWithType<Boolean>(leftOperand < rightOperand, boolean.class);
                case LESS_OR_EQUAL:
                    return new ObjectWithType<Boolean>(leftOperand <= rightOperand, boolean.class);
                case GREATER_OR_EQUAL:
                    return new ObjectWithType<Boolean>(leftOperand >= rightOperand, boolean.class);
                case GREATER_THAN:
                    return new ObjectWithType<Boolean>(leftOperand > rightOperand, boolean.class);
                default:
                    throw new IllegalStateException("unexpected operator: " + this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Boolean> apply(final long leftOperand, final long rightOperand) {
            switch (this) {
                case LESS_THAN:
                    return new ObjectWithType<Boolean>(leftOperand < rightOperand, boolean.class);
                case LESS_OR_EQUAL:
                    return new ObjectWithType<Boolean>(leftOperand <= rightOperand, boolean.class);
                case GREATER_OR_EQUAL:
                    return new ObjectWithType<Boolean>(leftOperand >= rightOperand, boolean.class);
                case GREATER_THAN:
                    return new ObjectWithType<Boolean>(leftOperand > rightOperand, boolean.class);
                default:
                    throw new IllegalStateException("unexpected operator: " + this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Boolean> apply(final double leftOperand, final double rightOperand) {
            switch (this) {
                case LESS_THAN:
                    return new ObjectWithType<Boolean>(leftOperand < rightOperand, boolean.class);
                case LESS_OR_EQUAL:
                    return new ObjectWithType<Boolean>(leftOperand <= rightOperand, boolean.class);
                case GREATER_OR_EQUAL:
                    return new ObjectWithType<Boolean>(leftOperand >= rightOperand, boolean.class);
                case GREATER_THAN:
                    return new ObjectWithType<Boolean>(leftOperand > rightOperand, boolean.class);
                default:
                    throw new IllegalStateException("unexpected operator: " + this);
            }
        }
    }

    /**
     * Gewünschter Operator für die Operation (Kleiner, Kleiner oder Gleich, Größer oder Gleich oder Größer).
     */
    private Operator operator;

    /**
     * Ausdruck des linken Operanden.
     */
    private Expression left;

    /**
     * Ausdruck des rechten Operanden.
     */
    private Expression right;

    /**
     * Erzeugt eine neue CompareExpression mit den übergebenen Werten.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen der Template-Datei erzeugt hat.
     * @param left
     *            Ausdruck für den linken Operanden.
     * @param right
     *            Ausdruck für den rechten Operanden.
     * @param operator
     *            Operator für die Vergleichsfunktion.
     */
    public CompareExpression(final ExpressionCompareContext ctx, final Expression left, final Expression right,
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
        final Class<?> leftType = leftObj.getType();
        if (!JavaUtil.isNumberType(leftType)) {
            throw new RuntimeException("I can not use the " + operator + " operator to an "
                    + leftType.getName() + ". (" + left.getStartPosition() + ")");
        }
        final ObjectWithType<?> rightObj = JavaUtil.unaryNumericPromotion(right.evaluate(model, environment));
        final Class<?> rightType = rightObj.getType();
        if (!JavaUtil.isNumberType(rightType)) {
            throw new RuntimeException("I can not use the " + operator + " operator to an "
                    + rightType.getName() + ". (" + left.getStartPosition() + ")");
        }

        return JavaUtil.applyBinaryOperation(leftObj, rightObj, operator);
    }
}