package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.GeneratorException;
import de.chrlembeck.codegen.generator.JavaUtil;
import lang.CodeGenParser.ExpressionConditionalAndContext;

/**
 * Repräsentiert das bedingte Und für Booleans.
 * 
 * @author Christoph Lembeck
 */
public class ConditionalAndExpression extends AbstractExpression<ExpressionConditionalAndContext> {

    /**
     * Ausdruck des linken Operanden.
     */
    private Expression left;

    /**
     * Ausdruck des rechten Operanden.
     */
    private Expression right;

    /**
     * Erzeugt eine neue ConditionalAndExpression mit den übergebenen Werten.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser beim Lesen der Template-Datei erzeugt hat.
     * @param left
     *            Ausdruck für den linken Operanden.
     * @param right
     *            Ausdruck für den rechten Operanden.
     */
    public ConditionalAndExpression(final ExpressionConditionalAndContext ctx, final Expression left,
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
            throw new GeneratorException("Der bedingte Und-Operator (&&) kann nur auf booleans angewandt werden. ("
                    + leftObj.getType() + ")", this, environment);
        }
        if (!((Boolean) leftObj.getObject()).booleanValue()) {
            return new ObjectWithType<Boolean>(Boolean.FALSE, boolean.class);
        }
        final ObjectWithType<?> rightObj = right.evaluate(model, environment);
        if (!JavaUtil.isBooleanType(rightObj.getType())) {
            throw new GeneratorException("Der bedingte Und-Operator (&&) kann nur auf booleans angewandt werden. ("
                    + rightObj.getType() + ")", this, environment);
        }
        return rightObj;
    }
}