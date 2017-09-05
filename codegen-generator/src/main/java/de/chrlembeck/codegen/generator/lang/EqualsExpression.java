package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.JavaUtil;
import de.chrlembeck.codegen.grammar.CodeGenParser.ExpressionEqualsContext;

/**
 * Führt einen Vergleich auf Gleichheit bzw. Ungleichheit aus (== oder !=).
 *
 * @author Christoph Lembeck
 */
public class EqualsExpression extends AbstractExpression<ExpressionEqualsContext> {

    /**
     * Operatoren für die Verwending innerhalb der EqualsExpression.
     */
    public enum Operator implements PrimitiveOperations {

        /**
         * Entspricht dem ==-Operator.
         */
        EQUAL,

        /**
         * Entspricht dem !=-Operator.
         */
        NOT_EQUAL;

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Boolean> apply(final int leftOperand, final int rightOperand) {
            if (this == EQUAL) {
                return new ObjectWithType<Boolean>(leftOperand == rightOperand, boolean.class);
            } else {
                return new ObjectWithType<Boolean>(leftOperand != rightOperand, boolean.class);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Boolean> apply(final float leftOperand, final float rightOperand) {
            if (this == EQUAL) {
                return new ObjectWithType<Boolean>(leftOperand == rightOperand, boolean.class);
            } else {
                return new ObjectWithType<Boolean>(leftOperand != rightOperand, boolean.class);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Boolean> apply(final long leftOperand, final long rightOperand) {
            if (this == EQUAL) {
                return new ObjectWithType<Boolean>(leftOperand == rightOperand, boolean.class);
            } else {
                return new ObjectWithType<Boolean>(leftOperand != rightOperand, boolean.class);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Boolean> apply(final double leftOperand, final double rightOperand) {
            if (this == EQUAL) {
                return new ObjectWithType<Boolean>(leftOperand == rightOperand, boolean.class);
            } else {
                return new ObjectWithType<Boolean>(leftOperand != rightOperand, boolean.class);
            }
        }
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
     * Gewünschter Operator für die Operation (Gleich oder Ungleich).
     */
    private Operator operator;

    /**
     * Erzeugt eine neue EqualsExpression mit den übergebenen Werten.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen der Template-Datei erzeugt hat.
     * @param left
     *            Ausdruck für den linken Operanden.
     * @param right
     *            Ausdruck für den rechten Operanden.
     * @param operator
     *            Operator für die Vergleichsoperation.
     */
    public EqualsExpression(final ExpressionEqualsContext ctx, final Expression left, final Expression right,
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
        ObjectWithType<?> rightObj = right.evaluate(model, environment);
        ObjectWithType<?> leftObj = left.evaluate(model, environment);
        if (JavaUtil.isBooleanType(leftObj.getType()) && JavaUtil.isBooleanType(rightObj.getType())) {
            if (operator == Operator.EQUAL) {
                return new ObjectWithType<Boolean>(((Boolean) leftObj.getObject())
                        .booleanValue() == ((Boolean) rightObj.getObject()).booleanValue(), boolean.class);
            } else {
                return new ObjectWithType<Boolean>(((Boolean) leftObj.getObject())
                        .booleanValue() != ((Boolean) rightObj.getObject()).booleanValue(), boolean.class);
            }
        }
        if (JavaUtil.isNumericType(leftObj.getType()) && JavaUtil.isNumericType(rightObj.getType())) {
            leftObj = JavaUtil.unaryNumericPromotion(leftObj);
            rightObj = JavaUtil.unaryNumericPromotion(rightObj);
            return JavaUtil.applyBinaryOperation(leftObj, rightObj, operator);
        }
        if (operator == Operator.EQUAL) {
            return new ObjectWithType<Boolean>(leftObj.getObject() == rightObj.getObject(), boolean.class);
        } else {
            return new ObjectWithType<Boolean>(leftObj.getObject() != rightObj.getObject(), boolean.class);
        }
    }
}