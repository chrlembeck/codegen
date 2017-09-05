package de.chrlembeck.codegen.generator.lang;

import java.lang.reflect.Array;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.GeneratorException;
import de.chrlembeck.codegen.grammar.CodeGenParser.ExpressionArrayAccessContext;

/**
 * Repräsentiert den Zugriff auf ein Array-Element innerhalb des Java-ähnlichen Teils der Template-Sprache.
 *
 * @author Christoph Lembeck
 */
public class ArrayAccessExpression extends AbstractExpression<ExpressionArrayAccessContext> {

    /**
     * Referenz auf das Array, aus dem gelesen werden soll.
     */
    private Expression arrayRef;

    /**
     * Indexposition des interessanten Array-Elements.
     */
    private Expression index;

    /**
     * Erzeugt einen neuen Array-Zugriff mit den Werten, die der Parser aus der Datei ermittelt hat.
     * 
     * @param ctx
     *            Kontext zu diesem Element, wie der Parser ihn ermittelt hat.
     * @param array
     *            Array, aus dem gelesen werden soll.
     * @param index
     *            Indexposition des Elements, das aus dem Array gelesen werden soll.
     */
    public ArrayAccessExpression(final ExpressionArrayAccessContext ctx, final Expression array,
            final Expression index) {
        super(ctx);
        this.arrayRef = array;
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        final ObjectWithType<?> src = arrayRef.evaluate(model, environment);
        if (!src.getType().isArray()) {
            throw new GeneratorException(
                    "Array-Zugriff auf ein Objekt, welches kein Array ist. (" + src.getType().getName() + ")",
                    this, environment);
        }
        final ObjectWithType<?> idx = index.evaluate(model, environment);
        if (!(Integer.class.isAssignableFrom(idx.getType())) && !(int.class.isAssignableFrom(idx.getType()))) {
            throw new GeneratorException("Array Index ist kein Integer. (" + idx.getObject().getClass().getName() + ")",
                    this, environment);
        }
        final Object result = Array.get(src.getObject(), (Integer) idx.getObject());
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final ObjectWithType<?> objectWithType = new ObjectWithType(result, src.getClass().getComponentType());
        return objectWithType;
    }
}