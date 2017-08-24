package de.chrlembeck.codegen.generator.lang;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import de.chrlembeck.codegen.generator.Environment;
import lang.CodeGenParser.ExpressionMethodCallContext;

/**
 * Entspricht einem Methodenaufruf in dem java-ähnlichen Teil der Template-Sprache.
 *
 * @author Christoph Lembeck
 */
public class MethodCallExpression extends AbstractExpression<ExpressionMethodCallContext> {

    /**
     * Referenz auf die auszuführende Methode.
     */
    private Expression methodExpression;

    /**
     * Argumente, die der Methode übergeben werden sollen.
     */
    private List<Expression> arguments;

    /**
     * Erzeugt eine neue Repräsentation eines Methodenaufrufs mit den Werten des Parsers.
     * 
     * @param context
     *            Kontext für diesen Methodenaufruf, wie der Parser ihn in dem Template-Dokument erkannt hat.
     * @param methodExpression
     *            Ausdruck für die aufzurufende Methode.
     * @param arguments
     *            An die Methode zu übergebende Argumente.
     */
    public MethodCallExpression(final ExpressionMethodCallContext context, final Expression methodExpression,
            final List<Expression> arguments) {
        super(context);
        this.methodExpression = methodExpression;
        this.arguments = arguments;
    }

    /**
     * Gibt die aufzurufende Methode als Expression zurück.
     * 
     * @return Expression, die die aufzurufende Methode darstellt.
     */
    public Expression getMethodExpression() {
        return methodExpression;
    }

    /**
     * Gibt die an die Methode zu übergebenden Argumente als Expressions zurück.
     * 
     * @return Liste von Expressions, die die an die Methode zu übergebenden Argumente darstellen.
     */
    public List<Expression> getArguments() {
        return arguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "MethodCall[method=" + methodExpression + ", arguments=" + arguments + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        final AttributeExpression exp = (AttributeExpression) methodExpression;
        final CallSource source = exp.getExpression().findCallSource(model, environment);
        final String methodName = exp.getIdentifier();
        final Object[] params = new Object[arguments.size()];
        final Class<?>[] types = new Class<?>[params.length];
        for (int i = 0; i < arguments.size(); i++) {
            final ObjectWithType<?> owt = arguments.get(i).evaluate(model, environment);
            params[i] = owt.getObject();
            types[i] = owt.getType();
        }
        final Method method;

        try {
            if (source instanceof ObjectCallSource) {
                final Object objectRef = ((ObjectCallSource) source).getObjectRef();
                method = objectRef.getClass().getMethod(methodName, types);
                @SuppressWarnings({ "rawtypes", "unchecked" })
                final ObjectWithType<?> result = new ObjectWithType(method.invoke(objectRef, params),
                        method.getReturnType());
                return result;
            } else {
                method = ((StaticCallSource) source).getClassRef().getMethod(methodName, types);
                @SuppressWarnings({ "rawtypes", "unchecked" })
                final ObjectWithType<?> result = new ObjectWithType(method.invoke(null, params),
                        method.getReturnType());
                return result;
            }
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}