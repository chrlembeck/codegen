package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.JavaUtil;
import lang.CodeGenParser.ExpressionPlusMinusContext;

/**
 * Repräsentiert die Konkatenation von Zeichenketten mit dem Plus-Operator oder die numerischen Operationen Plus oder
 * Minus.
 * 
 * @author Christoph Lembeck
 */
public class PlusMinusExpression extends AbstractExpression<ExpressionPlusMinusContext> {

    /**
     * Mögliche Operatoren für die PlusMinusExpression.
     * 
     * @author Christoph Lembeck
     */
    public static enum Operator implements PrimitiveOperations {

        /**
         * Verknüpft zwei Zeichenketten oder Addiert zwei numerische Werte.
         */
        PLUS,

        /**
         * Subtrahiert einen numerischen Wert von einem anderen.
         */
        MINUS;

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Integer> apply(final int a, final int b) {
            return new ObjectWithType<Integer>(Integer.valueOf(this == PLUS ? a + b : a - b), int.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Long> apply(final long a, final long b) {
            return new ObjectWithType<Long>(Long.valueOf(this == PLUS ? a + b : a - b), long.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Float> apply(final float a, final float b) {
            return new ObjectWithType<Float>(Float.valueOf(this == PLUS ? a + b : a - b), float.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ObjectWithType<Double> apply(final double a, final double b) {
            return new ObjectWithType<Double>(Double.valueOf(this == PLUS ? a + b : a - b), double.class);
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
     * Gewünschter Operator für die Operation (Plus oder Minus).
     */
    private Operator operator;

    /**
     * Erzeugt eine neue PlusMinusExpression mit den übergebenen Werten.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen der Template-Datei erzeugt hat.
     * @param left
     *            Ausdruck für den linken Operanden.
     * @param right
     *            Ausdruck für den rechten Operanden.
     * @param operator
     *            Gewünschter Operator für die Funktion (Plus oder Minus).
     */
    public PlusMinusExpression(final ExpressionPlusMinusContext ctx, final Expression left, final Expression right,
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
     * Gibt den Ausdruck des rechten Operaden zurück.
     * 
     * @return Rechter Operand.
     */
    public Expression getRight() {
        return right;
    }

    /**
     * Gibt den verwendeten Operator Plus oder Minus zurück.
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
     * {@inheritDoc} Siehe auch: <a href=
     * "https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.18">https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.18</a>
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        ObjectWithType<?> leftObj = left.evaluate(model, environment);
        ObjectWithType<?> rightObj = right.evaluate(model, environment);
        if (operator == Operator.PLUS
                && (String.class.isAssignableFrom(leftObj.getType())
                        || String.class.isAssignableFrom(rightObj.getType()))) {
            return new ObjectWithType<String>(
                    String.valueOf(leftObj.getObject()) + String.valueOf(rightObj.getObject()), String.class);
        } else {
            leftObj = JavaUtil.unaryNumericPromotion(leftObj);
            rightObj = JavaUtil.unaryNumericPromotion(rightObj);
            final Class<?> leftType = leftObj.getType();
            final Class<?> rightType = rightObj.getType();
            if (!JavaUtil.isNumberType(leftType)) {
                throw new RuntimeException("I can not use the " + operator + " operator to an "
                        + leftType.getName() + ". (" + left.getStartPosition() + ")");
            }
            if (!JavaUtil.isNumberType(rightType)) {
                throw new RuntimeException("I can not use the " + operator + " operator to an "
                        + rightType.getName() + ". (" + left.getStartPosition() + ")");
            }
            return JavaUtil.applyBinaryOperation(leftObj, rightObj, operator);
        }
    }
}