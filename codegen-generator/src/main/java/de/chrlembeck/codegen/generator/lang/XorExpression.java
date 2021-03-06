package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.GeneratorException;
import de.chrlembeck.codegen.generator.JavaUtil;
import de.chrlembeck.codegen.grammar.CodeGenParser.ExpressionXorContext;

/**
 * Repräsentiert das exclusive Oder für Booleans oder die bitweise XOR-Funktion für numerische Werte.
 * 
 * @author Christoph Lembeck
 */
public class XorExpression extends AbstractExpression<ExpressionXorContext> {

    /**
     * Ausdruck des linken Operanden.
     */
    private Expression left;

    /**
     * Ausdruck des rechten Operanden.
     */
    private Expression right;

    /**
     * Erzeugt eine neue XorExpression mit den übergebenen Werten.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen der Template-Datei erzeugt hat.
     * @param left
     *            Ausdruck für den linken Operanden.
     * @param right
     *            Ausdruck für den rechten Operanden.
     */
    public XorExpression(final ExpressionXorContext ctx, final Expression left, final Expression right) {
        super(ctx);
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        ObjectWithType<?> leftObj = left.evaluate(model, environment);
        ObjectWithType<?> rightObj = right.evaluate(model, environment);
        if (JavaUtil.isBooleanType(leftObj.getType()) && JavaUtil.isBooleanType(rightObj.getType())) {
            return new ObjectWithType<Boolean>(
                    ((Boolean) leftObj.getObject()).booleanValue() ^ ((Boolean) rightObj.getObject()).booleanValue(),
                    boolean.class);
        }
        if (JavaUtil.isIntegralNumericType(leftObj.getType()) && JavaUtil.isIntegralNumericType(rightObj.getType())) {
            leftObj = JavaUtil.unaryNumericPromotion(leftObj);
            rightObj = JavaUtil.unaryNumericPromotion(rightObj);
            final Class<? extends Number> type = JavaUtil.getBinaryNumericPromotionType(leftObj.getType(),
                    rightObj.getType());
            if (int.class.isAssignableFrom(type)) {
                return new ObjectWithType<Integer>(
                        ((Number) leftObj.getObject()).intValue() ^ ((Number) rightObj.getObject()).intValue(),
                        int.class);
            } else {
                return new ObjectWithType<Long>(
                        ((Number) leftObj.getObject()).longValue() ^ ((Number) rightObj.getObject()).longValue(),
                        long.class);
            }
        }
        throw new GeneratorException(
                "Die Operatoren &, | und ^ können nur auf booleans oder ganzzahlige Werte angewandt werden. " + leftObj
                        + ":" + rightObj,
                this, environment);
    }
}