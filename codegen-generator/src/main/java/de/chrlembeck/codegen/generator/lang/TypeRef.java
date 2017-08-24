package de.chrlembeck.codegen.generator.lang;

import de.chrlembeck.codegen.generator.Environment;
import de.chrlembeck.codegen.generator.GeneratorException;
import lang.CodeGenParser.PrimaryTypeClassContext;

/**
 * Repräsentiert eine Klassendefinition, also alle Sybtypen der Klasse java.lang.Class.
 *
 * @author Christoph Lembeck
 */
public class TypeRef extends AbstractExpression<PrimaryTypeClassContext> implements Primary {

    /**
     * Zu repräsentierender Typ.
     */
    private ClassOrPrimitiveType typeType;

    /**
     * Erstellt einen neuen Klassentyp nach den Vorgaben des Parsers.
     * 
     * @param ctx
     *            Kontext, wie ihn der Parser für dieses Element gelesen und erzeugt hat.
     * @param typeType
     *            Zu repräsentierender Klassentyp.
     */
    public TypeRef(final PrimaryTypeClassContext ctx, final ClassOrPrimitiveType typeType) {
        super(ctx);
        this.typeType = typeType;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public ObjectWithType<Class> evaluate(final Object model, final Environment environment) {
        try {
            return new ObjectWithType<Class>(typeType.getClassRef(), Class.class);
        } catch (final ClassNotFoundException e) {
            throw new GeneratorException("Klasse konnte nicht gefunden werden: " + typeType.getClassName(), this,
                    environment, e);
        }
    }
}