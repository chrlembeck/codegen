package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.GeneratorException;
import de.chrlembeck.codegen.grammar.CodeGenParser.ExpressionInstanceofContext;

/**
 * Implementiert den {@code instanceof}-Operator innerhalb des java-ähnlichen Teils der Template-Sprache.
 *
 * @author Christoph Lembeck
 */
public class InstanceofExpression extends AbstractExpression<ExpressionInstanceofContext> {

    /**
     * Typ, in den der Ausdruck konvertiert werden soll.
     */
    private ClassOrPrimitiveType typeType;

    /**
     * Ausdruck, der in den angegebenen Typ konvertiert werden soll.
     */
    private Expression expression;

    /**
     * Erstellt eine neue InstanceofExpression mit den übergebenen Werten.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser für dieses Element erzeugt hat.
     * @param expression
     *            Ausdruck, der in einen anderen Typ konvertiert werden soll.
     * @param typeType
     *            Typ, in den de rausdruck konvertiert werden soll.
     */
    public InstanceofExpression(final ExpressionInstanceofContext ctx, final Expression expression,
            final ClassOrPrimitiveType typeType) {
        super(ctx);
        this.expression = expression;
        this.typeType = typeType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<Boolean> evaluate(final Object model, final Environment environment) {
        final Class<?> type;
        try {
            type = typeType.getClassRef();
        } catch (final ClassNotFoundException e) {
            throw new GeneratorException("Klasse konnte nicht gefunden werden: " + typeType.getClassName(), this,
                    environment, e);
        }

        final ObjectWithType<?> obj = expression.evaluate(model, environment);
        return new ObjectWithType<Boolean>(Boolean.valueOf(type.isInstance(obj.getObject())), boolean.class);
    }
}