package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.JavaUtil;
import lang.CodeGenParser.ExpressionMultDivModContext;

/**
 * Repräsentiert die numerischen Operationen Multiplikation, Division oder Modulo.
 * 
 * @author Christoph Lembeck
 */
public class MultDivModExpression extends AbstractExpression<ExpressionMultDivModContext> {

    /**
     * Mögliche Operatoren für die PlusMinusExpression.
     * 
     * @author Christoph Lembeck
     */
    public enum Operator implements PrimitiveOperations {

        /**
         * Multipliziert einen numerischen Wert mit einem anderen.
         */
        MULT,

        /**
         * Dividiert einen numerischen Wert durch einen anderen.
         */
        DIV,

        /**
         * Führt die Modulo-Operation auf zwei numerischen Werten durch.
         */
        MOD;

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Integer> apply(final int leftOperand, final int rightOperand) {
            if (this == MULT) {
                return new ObjectWithType<Integer>(Integer.valueOf(leftOperand * rightOperand), int.class);
            } else if (this == DIV) {
                return new ObjectWithType<Integer>(Integer.valueOf(leftOperand / rightOperand), int.class);
            } else {
                return new ObjectWithType<Integer>(Integer.valueOf(leftOperand % rightOperand), int.class);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Long> apply(final long leftOperand, final long rightOperand) {
            if (this == MULT) {
                return new ObjectWithType<Long>(Long.valueOf(leftOperand * rightOperand), long.class);
            } else if (this == DIV) {
                return new ObjectWithType<Long>(Long.valueOf(leftOperand / rightOperand), long.class);
            } else {
                return new ObjectWithType<Long>(Long.valueOf(leftOperand % rightOperand), long.class);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Float> apply(final float leftOperand, final float rightOperand) {
            if (this == MULT) {
                return new ObjectWithType<Float>(Float.valueOf(leftOperand * rightOperand), float.class);
            } else if (this == DIV) {
                return new ObjectWithType<Float>(Float.valueOf(leftOperand / rightOperand), float.class);
            } else {
                return new ObjectWithType<Float>(Float.valueOf(leftOperand % rightOperand), float.class);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Double> apply(final double leftOperand, final double rightOperand) {
            if (this == MULT) {
                return new ObjectWithType<Double>(Double.valueOf(leftOperand * rightOperand), double.class);
            } else if (this == DIV) {
                return new ObjectWithType<Double>(Double.valueOf(leftOperand / rightOperand), double.class);
            } else {
                return new ObjectWithType<Double>(Double.valueOf(leftOperand % rightOperand), double.class);
            }
        }
    }

    /**
     * Gewünschter Operator für die Operation (Mal, Geteilt oder Modulo).
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
     * Erzeugt eine neue MultDivModExpression mit den übergebenen Werten.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen der Template-Datei erzeugt hat.
     * @param left
     *            Ausdruck für den linken Operanden.
     * @param right
     *            Ausdruck für den rechten Operanden.
     * @param operator
     *            Gewünschter Operator für die Operation (Multiplikation, Division oder Modulo).
     */
    public MultDivModExpression(final ExpressionMultDivModContext ctx, final Expression left, final Expression right,
            final Operator operator) {
        super(ctx);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    /**
     * Gibt den Ausdruck des linken Operanden zurück.
     * 
     * @return Linker Operand.
     */
    public Expression getLeft() {
        return left;
    }

    /**
     * Gibt den Ausdruck des rechten Operanden zurück.
     * 
     * @return Rechter Operand.
     */
    public Expression getRight() {
        return right;
    }

    /**
     * Gibt den verwendeten Operator Mal, Geteilt oder Modulo zurück.
     * 
     * @return Verwendeter Operator.
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
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
        final ObjectWithType<?> rightObj = JavaUtil
                .unaryNumericPromotion(right.evaluate(model, environment));
        final Class<?> rightType = rightObj.getType();
        if (!JavaUtil.isNumberType(rightType)) {
            throw new RuntimeException("I can not use the " + operator + " operator to an "
                    + rightType.getName() + ". (" + left.getStartPosition() + ")");
        }
        return JavaUtil.applyBinaryOperation(leftObj, rightObj, operator);
    }
}