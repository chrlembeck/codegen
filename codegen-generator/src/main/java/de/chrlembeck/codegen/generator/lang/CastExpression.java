package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.GeneratorException;
import de.chrlembeck.codegen.generator.JavaUtil;
import lang.CodeGenParser.ExpressionCastContext;

/**
 * Entspricht einem ClassCast oder einem primitiven Cast eines Wertes in den eines anderen Typs für die JAva-ähnlichen
 * Ausdrücke der CodeGen-Template-Sprache.
 * 
 * @author Christoph Lembeck
 */
public class CastExpression extends AbstractExpression<ExpressionCastContext> {

    /**
     * Typ, in den das Objekt überführt werden soll.
     */
    private ClassOrPrimitiveType destinationType;

    /**
     * Referenz auf das Objekt oder den Ausdruck, der in den Zieltyp konvertiert werden soll.
     */
    private Expression expression;

    /**
     * Erstellt ein neues Cast-Objekt mit den Werten, die der Parser ermittelt hat.
     * 
     * @param ctx
     *            Kontext zu diesem Objekt, wie der Parser ihn gelesen hat.
     * @param newType
     *            Typ, in den der Ausdruck konvertiert werden soll.
     * @param expression
     *            Ausdruck, der in den Typ überführt werden soll.
     */
    public CastExpression(final ExpressionCastContext ctx, final ClassOrPrimitiveType newType,
            final Expression expression) {
        super(ctx);
        this.destinationType = newType;
        this.expression = expression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        final Class<?> type;
        try {
            type = destinationType.getClassRef();
        } catch (final ClassNotFoundException e) {
            throw new GeneratorException("Klasse konnte nicht gefunden werden: " + destinationType.getClassName(), this,
                    environment, e);
        }
        final ObjectWithType<?> objectWithType = expression.evaluate(model, environment);

        final Object object = objectWithType.getObject();
        if (type.isPrimitive()) {
            if (boolean.class.isAssignableFrom(type) && object instanceof Boolean) {
                return new ObjectWithType<Boolean>((Boolean) object, boolean.class);
            } else if (byte.class.isAssignableFrom(type) && object instanceof Number) {
                return new ObjectWithType<Byte>(((Number) object).byteValue(), byte.class);
            } else if (short.class.isAssignableFrom(type) && object instanceof Number) {
                return new ObjectWithType<Short>(((Number) object).shortValue(), short.class);
            } else if (char.class.isAssignableFrom(type) && object instanceof Number) {
                return new ObjectWithType<Character>((char) (((Number) object).intValue()), char.class);
            } else if (int.class.isAssignableFrom(type) && object instanceof Number) {
                return new ObjectWithType<Integer>(((Number) object).intValue(), int.class);
            } else if (long.class.isAssignableFrom(type) && object instanceof Number) {
                return new ObjectWithType<Long>(((Number) object).longValue(), long.class);
            } else if (float.class.isAssignableFrom(type) && object instanceof Number) {
                return new ObjectWithType<Float>(((Number) object).floatValue(), float.class);
            } else if (double.class.isAssignableFrom(type) && object instanceof Number) {
                return new ObjectWithType<Double>(((Number) object).doubleValue(), double.class);
            } else if (byte.class.isAssignableFrom(type) && object instanceof Character) {
                return new ObjectWithType<Byte>((byte) ((Character) object).charValue(), byte.class);
            } else if (short.class.isAssignableFrom(type) && object instanceof Character) {
                return new ObjectWithType<Short>((short) ((Character) object).charValue(), short.class);
            } else if (char.class.isAssignableFrom(type) && object instanceof Character) {
                return new ObjectWithType<Character>(((Character) object).charValue(), char.class);
            } else if (int.class.isAssignableFrom(type) && object instanceof Character) {
                return new ObjectWithType<Integer>((int) ((Character) object).charValue(), int.class);
            } else if (long.class.isAssignableFrom(type) && object instanceof Character) {
                return new ObjectWithType<Long>((long) ((Character) object).charValue(), long.class);
            } else if (float.class.isAssignableFrom(type) && object instanceof Character) {
                return new ObjectWithType<Float>((float) ((Character) object).charValue(), float.class);
            } else if (double.class.isAssignableFrom(type) && object instanceof Character) {
                return new ObjectWithType<Double>((double) ((Character) object).charValue(), double.class);
            }
            final Class<?> wrapper = JavaUtil.getWrapperClass(type);
            @SuppressWarnings({ "rawtypes", "unchecked" })
            final ObjectWithType<?> result = new ObjectWithType(wrapper.cast(object), type);
            return result;
        } else {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            final ObjectWithType<?> result = new ObjectWithType(type.cast(object), type);
            return result;
        }
    }
}