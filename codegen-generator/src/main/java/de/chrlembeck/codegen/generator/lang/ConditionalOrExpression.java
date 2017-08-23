package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.GeneratorException;
import de.chrlembeck.codegen.generator.JavaUtil;
import lang.CodeGenParser.ExpressionConditionalOrContext;

/**
 * Repräsentiert das bedingte Oder für Booleans.
 * 
 * @author Christoph Lembeck
 */
public class ConditionalOrExpression extends AbstractExpression<ExpressionConditionalOrContext> {

    /**
     * Ausdruck des linken Operanden.
     */
    private Expression left;

    /**
     * Ausdruck des rechten Operanden.
     */
    private Expression right;

    /**
     * Erzeugt eine neue ConditionalOrExpression mit den übergebenen Werten.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen der Template-Datei erzeugt hat.
     * @param left
     *            Ausdruck für den linken Operanden.
     * @param right
     *            Ausdruck für den rechten Operanden.
     */
    public ConditionalOrExpression(final ExpressionConditionalOrContext ctx, final Expression left,
            final Expression right) {
        super(ctx);
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        final ObjectWithType<?> leftObj = left.evaluate(model, environment);
        if (!JavaUtil.isBooleanType(leftObj.getType())) {
            throw new GeneratorException("Der bedingte Oder-Operator (||) kann nur auf booleans angewandt werden. ("
                    + leftObj.getType() + ")", this, environment);
        }
        if (((Boolean) leftObj.getObject()).booleanValue()) {
            return leftObj;
        }
        final ObjectWithType<?> rightObj = right.evaluate(model, environment);
        if (!JavaUtil.isBooleanType(rightObj.getType())) {
            throw new GeneratorException("Der bedingte Oder-Operator (||) kann nur auf booleans angewandt werden. ("
                    + rightObj.getType() + ")", this, environment);
        }
        return rightObj;
    }
}