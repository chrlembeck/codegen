package de.chrlembeck.codegen.generator.lang;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.Environment;
import lang.CodeGenParser.ExpressionAttributeContext;

/**
 * Repräsentiert das Auslesen eines Attributes aus einem Objekt oder einer Klasse.
 * 
 * @author Christoph Lembeck
 */
public class AttributeExpression extends AbstractExpression<ExpressionAttributeContext> {

    /**
     * Der Logger für die Klasse.
     */
    private static Logger LOGGER = LoggerFactory.getLogger(AttributeExpression.class);

    /**
     * Expression, die das Objekt oder die Klasse enthält, aus dem oder der das Attribut gelesen werden soll.
     */
    private Expression expression;

    /**
     * Name des Attributs, welches ausgelesen werden soll.
     */
    private String identifier;

    /**
     * Erstelle eine neue AttributeExpression aus dem Kontext des Parsers.
     * 
     * @param ctx
     *            Kontext zu der Expression, wie ihn der Parser ermittelt hat.
     * @param expression
     *            Expression, die das Objekt oder die Klasse zum Auslesen des Attributs enthält.
     * @param identifier
     *            Name des zu lesenden Attributs.
     */
    public AttributeExpression(final ExpressionAttributeContext ctx, final Expression expression,
            final String identifier) {
        super(ctx);
        this.expression = expression;
        this.identifier = identifier;
    }

    /**
     * Gibt die Expression zurück, aus der die Daten gelesen werde sollen.
     * 
     * @return Expression zum auslesen der Daten.
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Gibt den Namen des auzulesenden Attriuts zurück.
     * 
     * @return Name des zu lesenden Attributs.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "AttributeExpression[expression=" + expression + ", identifier=" + identifier + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        LOGGER.debug("evaluate(" + toString() + ")");
        final CallSource source = expression.findCallSource(model, environment);
        Field field;
        try {
            if (source instanceof ObjectCallSource) {
                final Object objectRef = ((ObjectCallSource) source).getObjectRef();
                field = objectRef.getClass().getField(identifier);
                @SuppressWarnings({ "unchecked", "rawtypes" })
                final ObjectWithType<?> result = new ObjectWithType(field.get(objectRef), field.getType());
                return result;

            } else {
                field = ((StaticCallSource) source).getClassRef().getField(identifier);
                @SuppressWarnings({ "unchecked", "rawtypes" })
                final ObjectWithType<?> result = new ObjectWithType(field.get(null), field.getType()); // static
                return result;
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallSource findCallSource(final Object model, final Environment environment) {
        final CallSource source = expression.findCallSource(model, environment);
        if (source != null) {
            Field field;
            try {
                field = source.getClass().getField(identifier);
                @SuppressWarnings({ "rawtypes", "unchecked" })
                final ObjectWithType<?> objectWithType = new ObjectWithType(field.get(source), field.getType());
                return new ObjectCallSource(objectWithType);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        // falls source null, dann könnte es ein statischer Aufruf sein...
        String fqn = identifier;
        Expression tmp = expression;
        while (tmp instanceof AttributeExpression) {
            final AttributeExpression attributeExpression = (AttributeExpression) tmp;
            fqn = attributeExpression.identifier + "." + fqn;
            tmp = attributeExpression.expression;
        }
        if (tmp instanceof Identifier) {
            fqn = ((Identifier) tmp).getName() + "." + fqn;
        }
        try {
            LOGGER.debug("Searching for class '" + fqn + "'.");
            final Class<?> classRef = Class.forName(fqn);
            return new StaticCallSource(classRef);
        } catch (final ClassNotFoundException e) {
            // noch nichts gefunden. - Vielleicht wird in der Instanz darüber etwas gefunden (java.time ->
            // java.time.LocalDate)
            return null;
        }
    }
}