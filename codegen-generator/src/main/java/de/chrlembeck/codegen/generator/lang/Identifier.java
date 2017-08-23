package de.chrlembeck.codegen.generator.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.GeneratorException;
import lang.CodeGenParser.PrimaryIdentifierContext;

/**
 * Repräsentiert eine Variable in einem Ausdruck innerhalb einer Template-Datei.
 * 
 * @author Christoph Lembeck
 */
public class Identifier extends AbstractExpression<PrimaryIdentifierContext> implements Primary {

    /**
     * Der Logger für diese Klasse.
     */
    private static Logger LOGGER = LoggerFactory.getLogger(Identifier.class);

    /**
     * Erstellt eine neue Variable mit dem Namen aus dem Kontext.
     * 
     * @param ctx
     *            Vom Parser erzeugter Kontext zu diesem Element.
     */
    public Identifier(final PrimaryIdentifierContext ctx) {
        super(ctx);
    }

    /**
     * Gibt den Namen der Variable zurück.
     * 
     * @return Name der Variable.
     */
    public String getName() {
        return getContext().Identifier().getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectWithType<?> evaluate(final Object model, final Environment environment) {
        if (environment.containsVariable(getName())) {
            return environment.lookupVariable(getName());
        } else {
            final String message = "Unbekannte Variable '" + getName() + "' gefunden.";
            LOGGER.warn(message);
            throw new GeneratorException(message, this, environment);
        }
    }

    /**
     * Sucht in der Laufzeitumgebung nach einer Belegung für diese Variable. Wird eine Belegung gefunden, wird diese als
     * Source zurückgegeben, sonst null.
     * 
     * @return Belegung der Variable in der Laufzeitumgebuns, falls eine solche existiert, sonst null.
     */
    @Override
    public CallSource findCallSource(final Object model, final Environment environment) {
        if (environment.containsVariable(getName())) {
            return new ObjectCallSource(environment.lookupVariable(getName()));
        }
        return null;
    }
}